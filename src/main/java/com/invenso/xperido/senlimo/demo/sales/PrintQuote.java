package com.invenso.xperido.senlimo.demo.sales;

import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.printer.object.Printer;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.model.QuoteMapper;
import com.invenso.xperido.senlimo.model.db.QuoteEntity;
import com.invenso.xperido.senlimo.model.xml.quote.ObjectFactory;
import com.invenso.xperido.senlimo.model.xml.quote.Quote;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This demo generates a quote based on XML data it generates from Database objects
 *
 * The quote is then mailed to the contact assigned to the quote
 */
public class PrintQuote {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailQuote.class);

	public static void main(String[] args) {
		try (XperiDoServices services = XperiDoServices.create()) {

			// select XperiDo template to use
			String filter = "quotedb%";
			Template orderTemplate = services.getXDTemplates(filter)
					.stream().filter(x -> x.getName().equals("SenLimo Quote for Print")).findFirst()
					.orElseThrow(() -> new TemplateNotFoundException("No template found for dataset filter " + filter));

			// generate document
			List<Object> queryVariables = new ArrayList<>();
			queryVariables.add("A Datum%"); // you can add any of the base types that can be represented in JSON: String, Double/BigDecimal, Boolean, Integer/BigInteger, Long
			GUID xdDocumentId = services.createXDDocument(
					orderTemplate,
					Priority.HIGH, FileExtension.PDF, FileNameSuffix.OVERWRITE,
					"ByName", queryVariables);

			// check document generation status.
			Future<Boolean> finished = services.retrieveXDDocumentStatus(xdDocumentId, 1000, 10);

			// Post process when finished
			if (finished.get()) {
				// print document
				List<Printer> printers = services.listXDPrinter();

				services.printXDDocument(xdDocumentId, printers.stream().findAny().
						orElseThrow(() -> new XDNucleusDemoException("No printer found")), 1, true);
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
