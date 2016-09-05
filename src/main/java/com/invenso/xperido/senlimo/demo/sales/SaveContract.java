package com.invenso.xperido.senlimo.demo.sales;

import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.template.MarkerCondition;
import com.invenso.xdws.service.template.MarkerQuery;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.service.template.object.TemplateType;
import com.invenso.xdws.service.template.object.dynamicfield.DynamicField;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.model.ContractMapper;
import com.invenso.xperido.senlimo.model.db.ContractEntity;
import com.invenso.xperido.senlimo.model.xml.contract.Contract;
import com.invenso.xperido.senlimo.model.xml.contract.ObjectFactory;
import com.invenso.xperido.senlimo.resources.Database;
import com.invenso.xperido.senlimo.resources.Utils;
import com.invenso.xperido.senlimo.resources.XperiDoServices;
import com.invenso.xperido.senlimo.resources.exceptions.ServerSideException;
import com.invenso.xperido.senlimo.resources.exceptions.TemplateNotFoundException;
import com.invenso.xperido.senlimo.resources.exceptions.XDNucleusDemoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This demo generates a contract based on XML data it generates from Database objects
 *
 * The contract is then saved to disk
 */
public class SaveContract {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaveContract.class);

	public static void main(String[] args) {
		try (Database db = Database.initialize();
			 XperiDoServices services = XperiDoServices.create()) {
			// get database contract as XML document
			ObjectFactory xmlObjectFactory = new ObjectFactory();
			ContractEntity entity = Utils.selectById(db, ContractEntity.class, 301);
			Contract xmlEntity = ContractMapper.INSTANCE.dbContractToXmlContract(entity);
			Document xmlData = Utils.createXML(xmlObjectFactory.createContract(xmlEntity), Contract.class);

			// select XperiDo template to use
			MarkerQuery stageFollowUp2016 = new MarkerQuery();
			stageFollowUp2016.setMarkerQueryOperator(MarkerQuery.MarkerQueryOperator.AND);
			MarkerCondition stageFollowUp = new MarkerCondition(MarkerCondition.MarkerValueConditionOperator.HAS_MARKER_AND_MARKER_VALUE, "Stage", "FollowUp");
			stageFollowUp2016.addMarkerCondition(stageFollowUp);
			MarkerCondition year2016 = new MarkerCondition(MarkerCondition.MarkerValueConditionOperator.HAS_MARKER_AND_MARKER_VALUE, "Year", "2016");
			stageFollowUp2016.addMarkerCondition(year2016);

			String filter = "contractdata%";
			List<Template> xdTemplates = services.getXDTemplates(filter, stageFollowUp2016);

			// contracts
			Template orderTemplate = xdTemplates.stream().
					filter(x -> x.getType() == TemplateType.MULTIPART).findFirst()
					.orElseThrow(() -> new TemplateNotFoundException((xdTemplates.isEmpty() ? "No" : "Multiple") + " templates with filter " + filter + " and markers start and year found"));

			// a template can have dynamic fields. These fields are to be updated before creating the document.
			// in most cases, these fields are resolved by first asking the user to input the corresponding data.
			// this application here is responsible to gather that information.

			List<DynamicField> dynamicFieldList = services.getXDTemplateMetadata(orderTemplate);

			// update dynamic fields within the application.
			for (DynamicField field : dynamicFieldList) {

				if (field.getName().equals("Creation_Date")) {

					field.setValue(LocalDate.now(ZoneId.systemDefault()).toString());
				} else {
					field.setValue(field.getDefaultValue());
				}
			}

			// generate document
			GUID xdDocumentId = services.createXDDocument(
					orderTemplate,
					Priority.HIGH, FileExtension.PDF, FileNameSuffix.OVERWRITE,
					xmlData, dynamicFieldList);

			// check document generation status.
			Future<Boolean> finished = services.retrieveXDDocumentStatus(xdDocumentId, 1000, 10);

			// Post process when finished
			if (finished.get()) {
				// save to disk
				try {
					Path fileName = Paths.get(System.getProperty( "user.home" ), "contract.pdf");
					services.saveXDDocument(xdDocumentId, fileName);
					LOGGER.info("The document has been generated at {}", fileName);
				} catch (IOException e) {
					System.err.println("Document was generated, but could not be saved locally: " + e.getMessage());
					LOGGER.error("Problem writing file", e);
				}
			}
		} catch (ServerSideException e) {
			System.err.println("Server side problem, check server error logs: "+e.getMessage());
			LOGGER.error("Problem on server side", e);
		} catch (XDNucleusDemoException e) {
			System.err.println(e.getMessage());
			LOGGER.error("Execution error", e);
		} catch (InterruptedException e) {
			System.err.println("Program was interrupted");
			LOGGER.error("Program was interrupted", e);
		} catch (IOException e) {
			System.err.println("Could not load properties: "+e.getMessage());
			LOGGER.error("Could not load properties", e);
		} catch (ExecutionException e) {
			System.err.println("Could not get document status: "+e.getMessage());
			LOGGER.error("Could not get document status", e);
		}
	}


}
