package com.invenso.xperido.senlimo.demo.sales;

import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.printer.object.Printer;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.service.template.object.dynamicfield.DynamicField;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.model.InvoiceMapper;
import com.invenso.xperido.senlimo.model.db.InvoiceEntity;
import com.invenso.xperido.senlimo.model.xml.invoice.Invoice;
import com.invenso.xperido.senlimo.model.xml.invoice.ObjectFactory;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This demo generates an invoice based on XML data it generates from Database objects
 *
 * The invoice is then printed to the Samsung printer found on the server
 */
public class PrintInvoice {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintInvoice.class);

	public static void main(String[] args) {
		try (Database db = Database.initialize();
			 XperiDoServices services = XperiDoServices.create()) {

			// get database invoice as XML document
			ObjectFactory xmlObjectFactory = new ObjectFactory();
			InvoiceEntity entity = Utils.selectById(db, InvoiceEntity.class, 501);
			Invoice xmlEntity = InvoiceMapper.INSTANCE.dbInvoiceToXmlInvoice(entity);
			Document xmlData = Utils.createXML(xmlObjectFactory.createInvoice(xmlEntity), Invoice.class);

			// select XperiDo template to use
			String filter = "invoice%";
			List<Template> xdTemplates = services.getXDTemplates(filter);

			Template orderTemplate;
			if (xdTemplates.size() == 1)
				orderTemplate = xdTemplates.get(0);
			else
				throw new TemplateNotFoundException("No template found for dataset filter " + filter);

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

				// print document
				List<Printer> printers = services.listXDPrinter();

				services.printXDDocument(xdDocumentId, printers.stream().
						filter(x -> x.getName().contains("samsung")).findFirst().
						orElseThrow(() -> new XDNucleusDemoException("Samsung printer not found")), 1, true);
			}
		} catch (ServerSideException e) {
			System.err.println("Server side problem, check server error logs: " + e.getMessage());
			LOGGER.error("Problem on server side", e);
		} catch (XDNucleusDemoException e) {
			System.err.println(e.getMessage());
			LOGGER.error("Execution error", e);
		} catch (InterruptedException e) {
			System.err.println("Program was interrupted");
			LOGGER.error("Program was interrupted", e);
		} catch (IOException e) {
			System.err.println("Could not load properties: " + e.getMessage());
			LOGGER.error("Could not load properties", e);
		} catch (ExecutionException e) {
			System.err.println("Could not get document status: "+e.getMessage());
			LOGGER.error("Could not get document status", e);
		}
	}


}
