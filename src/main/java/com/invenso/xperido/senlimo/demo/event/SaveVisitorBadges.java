package com.invenso.xperido.senlimo.demo.event;

import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.model.EventMapper;
import com.invenso.xperido.senlimo.model.db.EventEntity;
import com.invenso.xperido.senlimo.model.xml.event.Event;
import com.invenso.xperido.senlimo.model.xml.event.ObjectFactory;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This demo generates visitor badges based on XML data it generates from Database objects
 *
 * The badges are then saved to disk
 */
public class SaveVisitorBadges {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaveVisitorBadges.class);

	public static void main(String[] args) {

		try (Database db = Database.initialize();
			 XperiDoServices services = XperiDoServices.create()) {

			// get database event as XML document
			ObjectFactory xmlObjectFactory = new ObjectFactory();
			EventEntity entity = Utils.selectById(db, EventEntity.class, 401);
			Event xmlEntity = EventMapper.INSTANCE.dbEventToXmlEvent(entity);
			Document xmlData = Utils.createXML(xmlObjectFactory.createEvent(xmlEntity), Event.class);

			// select XperiDo template to use
			String filter = "eventvisitor%";
			List<Template> xdTemplates = services.getXDTemplates(filter);

			Template orderTemplate;
			if (xdTemplates.size() == 1)
				orderTemplate = xdTemplates.get(0);
			else
				throw new TemplateNotFoundException("No template found for dataset filter "+filter);

			// generate document
			GUID xdDocumentId = services.createXDDocument(
					orderTemplate,
					Priority.HIGH, FileExtension.PDF, FileNameSuffix.OVERWRITE,
					xmlData, Collections.emptyList());

			// check document generation status.
			Future<Boolean> finished = services.retrieveXDDocumentStatus(xdDocumentId, 1000, 10);

			// Post process when finished
			if (finished.get()) {
				// save to disk
				try {
					Path fileName = Paths.get(System.getProperty( "user.home" ), "badges.pdf");
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
