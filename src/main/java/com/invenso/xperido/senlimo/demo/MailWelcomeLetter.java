package com.invenso.xperido.senlimo.demo;

import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.model.AccountMapper;
import com.invenso.xperido.senlimo.model.db.AccountEntity;
import com.invenso.xperido.senlimo.model.xml.account.Account;
import com.invenso.xperido.senlimo.model.xml.account.ObjectFactory;
import com.invenso.xperido.senlimo.resources.Database;
import com.invenso.xperido.senlimo.resources.Utils;
import com.invenso.xperido.senlimo.resources.XperiDoServices;
import com.invenso.xperido.senlimo.resources.exceptions.ServerSideException;
import com.invenso.xperido.senlimo.resources.exceptions.TemplateNotFoundException;
import com.invenso.xperido.senlimo.resources.exceptions.XDNucleusDemoException;
import com.invenso.xperido.senlimo.resources.objects.EmailAddressing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This demo generates a welcome letter based on XML it reads from the filesystem
 *
 * The welcome letter is then mailed to the account's primary contact's email address
 */
public class MailWelcomeLetter {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailWelcomeLetter.class);

	public static void main(String[] args) {
		try (XperiDoServices services = XperiDoServices.create()) {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			builderFactory.setNamespaceAware(true);
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document xmlData;
			try (InputStream is = MailWelcomeLetter.class.getResourceAsStream("/account.xml")) {
				 xmlData = builder.parse(is);
			}

			// select XperiDo template to use
			String filter = "account%";
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

				JAXBContext context = JAXBContext.newInstance(Account.class);
				Account xmlEntity = context.createUnmarshaller().unmarshal(xmlData, Account.class).getValue();

				if (xmlEntity.getAccountPrimaryContact() != null) {
					EmailAddressing addresses = new EmailAddressing();
					addresses.setMailFrom("info@invenso.com");
					addresses.setMailTo(Collections.singletonList(xmlEntity.getAccountPrimaryContact().getContactEmail()));
					services.mailXDDocument(xdDocumentId, addresses, "Welcome "+xmlEntity.getAccountName(), "Please find your welcome letter attached");
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
		} catch (ParserConfigurationException | SAXException | JAXBException e) {
			System.err.println("Could not read account xml: "+e.getMessage());
			LOGGER.error("Could not read account xml", e);
		}
	}


}
