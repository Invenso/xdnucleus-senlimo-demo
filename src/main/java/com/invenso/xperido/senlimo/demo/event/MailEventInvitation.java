package com.invenso.xperido.senlimo.demo.event;

import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.model.VisitorMapper;
import com.invenso.xperido.senlimo.model.db.VisitorEntity;
import com.invenso.xperido.senlimo.model.xml.visitor.*;
import com.invenso.xperido.senlimo.resources.Database;
import com.invenso.xperido.senlimo.resources.Utils;
import com.invenso.xperido.senlimo.resources.XperiDoServices;
import com.invenso.xperido.senlimo.resources.exceptions.TemplateNotFoundException;
import com.invenso.xperido.senlimo.resources.exceptions.ServerSideException;
import com.invenso.xperido.senlimo.resources.exceptions.XDNucleusDemoException;
import com.invenso.xperido.senlimo.resources.objects.EmailAddressing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * This demo generates an event invitation based on XML data it generates from Database objects
 *
 * The invitation is then mailed to the visitor
 */
public class MailEventInvitation {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailEventInvitation.class);

	public static void main(String[] args) {
		try (Database db = Database.initialize();
			 XperiDoServices services = XperiDoServices.create()) {

			// get database invoice as XML document
			ObjectFactory xmlObjectFactory = new ObjectFactory();
			VisitorEntity entity = Utils.selectById(db, VisitorEntity.class, 701);
			Visitor xmlEntity = VisitorMapper.INSTANCE.dbVisitorToXmlVisitor(entity);
			Document xmlData = Utils.createXML(xmlObjectFactory.createVisitor(xmlEntity), Visitor.class);

			// select XperiDo template to use
			String filter = "visitor%";
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
				EmailAddressing addresses = new EmailAddressing();
				addresses.setMailFrom("info@invenso.com");
				addresses.setMailTo(Collections.singletonList(entity.getContactByVisitorContact().getContactEmail()));
				services.mailXDDocument(xdDocumentId, addresses, "Invitation for "+entity.getContactByVisitorContact().getContactFullName(), "Please find your invitation attached");
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
