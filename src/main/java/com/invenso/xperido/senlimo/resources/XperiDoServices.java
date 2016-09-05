package com.invenso.xperido.senlimo.resources;

import com.invenso.xdws.connection.ConnectionParameters;
import com.invenso.xdws.exception.ServiceInvocationException;
import com.invenso.xdws.service.core.ServiceInvoker;
import com.invenso.xdws.service.document.create.CreateDocumentWithDatabaseQueryRequest;
import com.invenso.xdws.service.document.create.CreateDocumentWithXMLDataRequest;
import com.invenso.xdws.service.document.create.GetDocumentCreationStatusRequest;
import com.invenso.xdws.service.document.create.Priority;
import com.invenso.xdws.service.document.object.DocumentCreationInfo;
import com.invenso.xdws.service.document.object.FileExtension;
import com.invenso.xdws.service.document.object.FileNameSuffix;
import com.invenso.xdws.service.document.post.MailDocumentRequest;
import com.invenso.xdws.service.document.post.PostProcessDocumentRequest;
import com.invenso.xdws.service.document.post.PrintDocumentRequest;
import com.invenso.xdws.service.document.post.SaveDocumentRequest;
import com.invenso.xdws.service.document.retrieve.RetrieveDocumentAsStreamRequest;
import com.invenso.xdws.service.printer.GetPrintersRequest;
import com.invenso.xdws.service.printer.object.Printer;
import com.invenso.xdws.service.template.GetTemplateMetaDataRequest;
import com.invenso.xdws.service.template.GetTemplatesRequest;
import com.invenso.xdws.service.template.MarkerQuery;
import com.invenso.xdws.service.template.object.Template;
import com.invenso.xdws.service.template.object.dynamicfield.DynamicField;
import com.invenso.xdws.util.GUID;
import com.invenso.xperido.senlimo.resources.exceptions.ServerSideException;
import com.invenso.xperido.senlimo.resources.objects.EmailAddressing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Example wrapper class around the xperido-client SDK
 */
public class XperiDoServices implements AutoCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger(XperiDoServices.class);

	/**
	 * Create a service wrapper using the connection properties in /src/main/resources/xperido.properties
	 * <p>
	 * server: the name or ip address of the server
	 * port: the Webservice Interface port (normally 8080)
	 * user: the username to connect to your XperiDo Nucleus project
	 * password: the password
	 *
	 * It also creates a new singlethread scheduled executor, which is shutdown in the {@link #close()} method
	 *
	 * @return the service wrapper
	 * @throws IOException if the properties file cannot be read
	 */
	public static XperiDoServices create() throws IOException {
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		Properties props = new Properties();
		try (InputStream propsStream = XperiDoServices.class.getResourceAsStream("/xperido.properties")) {
			props.load(propsStream);
		}

		String server = getRequiredProperty(props, "server");
		int port = Integer.parseInt(getRequiredProperty(props, "port"));
		String user = getRequiredProperty(props, "user");
		String password = getRequiredProperty(props, "password");

		ConnectionParameters.ConnectionParametersBuilder connectionParametersBuilder = new ConnectionParameters.ConnectionParametersBuilder(user, password, server, port);
		ServiceInvoker serviceInvoker = new ServiceInvoker(connectionParametersBuilder.build());
		return new XperiDoServices(serviceInvoker, executorService);
	}

	/**
	 * Read a property from the properties collection
	 *
	 * @param props    the properties collection
	 * @param propName the name of the property
	 * @return the property value
	 * @throws IllegalArgumentException if the property cannot be found
	 */
	private static String getRequiredProperty(Properties props, String propName) {
		if (props.containsKey(propName)) {
			return props.getProperty(propName);
		} else
			throw new IllegalArgumentException("Property " + propName + " is required to connect to XperiDo");
	}

	private final ServiceInvoker serviceInvoker;
	private final ScheduledExecutorService executorService;

	private XperiDoServices(ServiceInvoker invoker, ScheduledExecutorService executorService) {
		this.serviceInvoker = invoker;
		this.executorService = executorService;
	}

	/**
	 * Get all templates as defined by the dataset filter
	 *
	 * @param datasetFilter the dataset filter (see {@link #getXDTemplates(String, String, MarkerQuery) getXDTemplates} for the filter spec)
	 * @return a list of templates
	 * @throws ServerSideException if an error occurs on the server
	 */
	public List<Template> getXDTemplates(String datasetFilter) throws ServerSideException {
		return getXDTemplates(datasetFilter, null);
	}

	/**
	 * Get all templates as defined by the dataset filter and marker query
	 *
	 * @param datasetFilter the dataset filter (see {@link #getXDTemplates(String, String, MarkerQuery) getXDTemplates} for the filter spec)
	 * @param query         a combined query of template markers
	 * @return a list of templates
	 * @throws ServerSideException if an error occurs on the server
	 */
	public List<Template> getXDTemplates(String datasetFilter, MarkerQuery query) throws ServerSideException {
		return getXDTemplates(datasetFilter, null, query);
	}

	/**
	 * Get all templates as defined by the dataset filter, template filter and marker query
	 *
	 * @param datasetFilter  dataset filter allows filtering on dataset name using the wildcard <b>%</b>
	 *                       more information about the use of wildcard on parameter templateFilter
	 * @param templateFilter template name filter allows filtering on template name using the wildcard <b>%</b>
	 *                       <ul>
	 *                       <li>%orders : select all items ending on orders</li>
	 *                       <li>orders% : select all items starting with orders</li>
	 *                       <li>%order% : select all items containing the word order</li>
	 *                       <li>order   : select all items equal to the word order</li>
	 *                       </ul>
	 * @param query          a combined query of template markers
	 * @return a list of templates
	 * @throws ServerSideException if an error occurs on the server
	 */
	public List<Template> getXDTemplates(String datasetFilter, String templateFilter, MarkerQuery query) throws ServerSideException {


		GetTemplatesRequest.GetTemplatesRequestBuilder templatesRequestBuilder = new GetTemplatesRequest.GetTemplatesRequestBuilder();

		// add query when provided
		if (query != null)
			templatesRequestBuilder.setMarkerQuery(query);

		// add filter when provided
		if (datasetFilter != null || templateFilter != null) {

			JsonObjectBuilder filterJsonBuilder = Json.createObjectBuilder();
			if (datasetFilter != null)
				filterJsonBuilder.add("dataset", datasetFilter);
			if (templateFilter != null)
				filterJsonBuilder.add("template", templateFilter);

			JsonObject filterJson = filterJsonBuilder.build();
			templatesRequestBuilder.setFilter(filterJson.toString());
		}

		// invoke, call XperiDo server to obtain templates
		try {
			return serviceInvoker.invoke(templatesRequestBuilder.build());
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	/**
	 * Retrieve template metadata : dynamic fields.
	 *
	 * @param template the template
	 * @return The list of dynamic fields defined fo the template
	 * @throws ServerSideException if an error occurs on the server
	 */
	public List<DynamicField> getXDTemplateMetadata(Template template) throws ServerSideException {

		GetTemplateMetaDataRequest.GetTemplateMetaDataRequestBuilder getTemplateMetaDataRequestBuilder = new GetTemplateMetaDataRequest.GetTemplateMetaDataRequestBuilder(template.getId());
		GetTemplateMetaDataRequest getTemplateMetaDataRequest = getTemplateMetaDataRequestBuilder.build();

		try {
			return serviceInvoker.invoke(getTemplateMetaDataRequest);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	/**
	 * Converts the document to a different format
	 *
	 * @param documentId the ID of the generated document (as returned by {@link #createXDDocument(Template, Priority, FileExtension, FileNameSuffix, Document, List)}
	 * @param extension  the format to convert to
	 * @param fileName   the new filename
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void convertXDDocument(GUID documentId, FileExtension extension, String fileName) throws ServerSideException {
		convertXDDocument(documentId, extension, fileName, null);
	}

	/**
	 * Converts the document to a different format
	 *
	 * @param documentId the ID of the generated document (as returned by {@link #createXDDocument(Template, Priority, FileExtension, FileNameSuffix, Document, List)}
	 * @param extension  the format to convert to
	 * @param fileName   the new filename
	 * @param location   the location to save to
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void convertXDDocument(GUID documentId, FileExtension extension, String fileName, String location) throws ServerSideException {
		convertXDDocument(documentId, extension, fileName, location, null);
	}

	/**
	 * Converts the document to a different format
	 *
	 * @param documentId the ID of the generated document (as returned by {@link #createXDDocument(Template, Priority, FileExtension, FileNameSuffix, Document, List)}
	 * @param extension  the format to convert to
	 * @param fileName   the new filename
	 * @param location   the location to save to
	 * @param suffix     the suffix to use
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void convertXDDocument(GUID documentId, FileExtension extension, String fileName, String location, FileNameSuffix suffix) throws ServerSideException {

		SaveDocumentRequest.SaveDocumentRequestBuilder saveDocumentRequestBuilder = new SaveDocumentRequest.SaveDocumentRequestBuilder(documentId, location != null ? location : "default", fileName);

		// add parameters.
		saveDocumentRequestBuilder
				.setDocumentFileExtension(extension);

		if (suffix != null)
			saveDocumentRequestBuilder.setDocumentFileNameSuffix(suffix);

		SaveDocumentRequest saveDocumentRequest = saveDocumentRequestBuilder.build();

		try {
			serviceInvoker.invoke(saveDocumentRequest);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	private static Document createDummyDocument() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.newDocument();
			Element root = doc.createElement("root");
			doc.appendChild(root);
			return doc;
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Generate the document on the server
	 *
	 * @param template the template as retrieved from {@link #getXDTemplates(String)}
	 * @param priority whether the document should be in the High or Low priority queueu
	 * @param fileExtension the type of document to generate
	 * @param fileNameSuffix whether to use a sequence number or timestamp in the filename
	 * @param xmlData the namespace aware XML document, use null to generate a document without data
	 * @param dynamicFieldList the values for the dynamic fields
	 * @return the ID of the generated document for use in post processing calls
	 * @throws ServerSideException if an error occurs on the server
	 */
	public GUID createXDDocument(Template template, Priority priority, FileExtension fileExtension, FileNameSuffix fileNameSuffix, Document xmlData, List<DynamicField> dynamicFieldList) throws ServerSideException {
		if (template == null)
			throw new NullPointerException("template");

		Document xmlDataTemplate = xmlData != null ? xmlData : createDummyDocument();

		String datasetName;
		if (template.getDataSet() != null) {
			datasetName = template.getDataSet().getName();
		} else {
			datasetName = "None";
		}

		// build request using required properties.
		CreateDocumentWithXMLDataRequest.CreateDocumentWithXMLDataRequestBuilder documentWithXMLDataRequestBuilder =
				new CreateDocumentWithXMLDataRequest.CreateDocumentWithXMLDataRequestBuilder(template.getId(), datasetName, xmlDataTemplate);

		// set additional request properties.
		documentWithXMLDataRequestBuilder
				.setDocumentFileExtension(fileExtension != null ? fileExtension : FileExtension.DOCX)
				.setPriority(priority != null ? priority : Priority.LOW)
				.setDocumentFileNameSuffix(fileNameSuffix != null ? fileNameSuffix : FileNameSuffix.TIMESTAMP_DATE_TIME);

		// pass dynamic field information when provided
		if (dynamicFieldList != null)
			documentWithXMLDataRequestBuilder.setDynamicFields(dynamicFieldList);


		// build request
		CreateDocumentWithXMLDataRequest documentWithXMLDataRequest = documentWithXMLDataRequestBuilder.build();

		try {
			// invoke request on server, XperiDo will return document Id as an unique reference
			DocumentCreationInfo documentCreationInfo = serviceInvoker.invoke(documentWithXMLDataRequest);
			return documentCreationInfo.getDocumentGuid();
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	public GUID createXDDocument(Template template, Priority priority, FileExtension fileExtension, FileNameSuffix fileNameSuffix, String queryName, List<? extends Object> queryVariables) throws ServerSideException {
		// build request using required properties.
		CreateDocumentWithDatabaseQueryRequest.CreateDocumentWithDatabaseQueryRequestBuilder builder = new CreateDocumentWithDatabaseQueryRequest.CreateDocumentWithDatabaseQueryRequestBuilder(template.getId(), queryName);
		// set additional request properties.
		builder
				.setDocumentFileExtension(fileExtension != null ? fileExtension : FileExtension.DOCX)
				.setPriority(priority != null ? priority : Priority.LOW)
				.setDocumentFileNameSuffix(fileNameSuffix != null ? fileNameSuffix : FileNameSuffix.TIMESTAMP_DATE_TIME);

		builder.setQueryVariables(queryVariables);

		// build request
		CreateDocumentWithDatabaseQueryRequest request = builder.build();

		try {
			// invoke request on server, XperiDo will return document Id as an unique reference
			DocumentCreationInfo documentCreationInfo = serviceInvoker.invoke(request);
			return documentCreationInfo.getDocumentGuid();
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}


	/**
	 * Find out whether the document generation has finished
	 *
	 * @param documentId   the ID of the generated document (as returned by {@link #createXDDocument(Template, Priority, FileExtension, FileNameSuffix, Document, List)}
	 * @param wait         the number of milliseconds to wait between tries
	 * @param retryCounter the number of times to retry
	 * @return whether the document generation has finished
	 * @throws ServerSideException if an error occurs on the server
	 */
	public CompletableFuture<Boolean> retrieveXDDocumentStatus(GUID documentId, int wait, int retryCounter) throws ServerSideException {

		CompletableFuture<Boolean> future = new CompletableFuture<>();

		Runnable isFinished = new Runnable() {
			int tries = 0;

			@Override
			public void run() {
				GetDocumentCreationStatusRequest documentCreationStatusRequest = new GetDocumentCreationStatusRequest(documentId);

				try {
					LOGGER.debug("Retrieving document status for {}, try {}", documentId, tries);
					DocumentCreationInfo documentCreationInfo = serviceInvoker.invoke(documentCreationStatusRequest);
					LOGGER.debug("Document status {	}", documentCreationInfo.isFinished());
					if (documentCreationInfo.isFinished()) {
						future.complete(true);
					} else if (++tries > retryCounter) {
						future.complete(false);
					}
				} catch (ServiceInvocationException e) {
					throw new ServerSideException(e);
				}
			}
		};

		ScheduledFuture scheduledFuture = executorService.scheduleAtFixedRate(isFinished, 0, wait, TimeUnit.MILLISECONDS);
		return future.thenApply(x -> {
			LOGGER.debug("Cancelling scheduled task");
			scheduledFuture.cancel(false);
			return x;
		});
	}


	/**
	 * Retrieve the document from the server and save it locally
	 *
	 * @param documentId the id of the generated document
	 * @param fileName   the file to save to
	 * @throws ServerSideException if an error occurs on the server
	 * @throws IOException         if the file cannot be saved locally
	 */
	public void saveXDDocument(GUID documentId, Path fileName) throws ServerSideException, IOException {

		RetrieveDocumentAsStreamRequest documentAsStreamRequest = new RetrieveDocumentAsStreamRequest(documentId);

		try (InputStream inputStream = serviceInvoker.invoke(documentAsStreamRequest)) {
			Files.copy(inputStream, fileName, StandardCopyOption.REPLACE_EXISTING);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	/**
	 * Call a postprocessing script
	 *
	 * @param documentId to id of the generated document
	 * @param script     the full name of the script
	 * @param routine    the script routine
	 * @param parameters the parameters for the script
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void callXDPostProcessing(GUID documentId, String script, String routine, String parameters) throws ServerSideException {

		PostProcessDocumentRequest.PostProcessDocumentRequestBuilder builder =
				new PostProcessDocumentRequest.PostProcessDocumentRequestBuilder(documentId, script, routine)
						.setPostProcessInput(parameters);

		PostProcessDocumentRequest postProcessDocumentRequest = new PostProcessDocumentRequest(builder);

		try {
			serviceInvoker.invoke(postProcessDocumentRequest);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}


	/**
	 * Print the generated document on the specified printer
	 *
	 * @param documentId the id of the generated document
	 * @param printer    the printer to print to
	 * @param copies     the number of copies to print
	 * @param rectoVerso whether or not to do duplex printing
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void printXDDocument(GUID documentId, Printer printer, int copies, boolean rectoVerso) throws ServerSideException {


		// create request builder
		PrintDocumentRequest.PrintDocumentRequestBuilder builder = new PrintDocumentRequest.PrintDocumentRequestBuilder(documentId, printer);

		// set parameters
		builder
				.setCopies(copies)
				.setRectoVerso(rectoVerso);

		PrintDocumentRequest printDocumentRequest = builder.build();

		try {
			serviceInvoker.invoke(printDocumentRequest);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	/**
	 * Mail the document as an attachment, using a generated document as the mail body
	 *
	 * @param documentId       the document to send as attachment
	 * @param emailAddressing  the address fields
	 * @param subject          the subject of the e-mail
	 * @param messageRequestId the id of the generated document to use as the mail body
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void mailXDDocument(GUID documentId, EmailAddressing emailAddressing, String subject, GUID messageRequestId) throws ServerSideException {
		mailXDDocument(documentId, emailAddressing, subject, messageRequestId, null);
	}

	/**
	 * Mail the document as an attachment
	 *
	 * @param documentId      the document to send as attachment
	 * @param emailAddressing the address fields
	 * @param subject         the subject of the e-mail
	 * @param messageBody     the text to use as the mail body
	 * @throws ServerSideException if an error occurs on the server
	 */
	public void mailXDDocument(GUID documentId, EmailAddressing emailAddressing, String subject, String messageBody) throws ServerSideException {
		mailXDDocument(documentId, emailAddressing, subject, null, messageBody);
	}

	private void mailXDDocument(GUID documentId, EmailAddressing emailAddressing, String subject, GUID messageRequestId, String messageBody) throws ServerSideException {
		// create request builder
		MailDocumentRequest.MailDocumentRequestBuilder builder = new MailDocumentRequest.MailDocumentRequestBuilder(documentId, emailAddressing.getMailToString());

		// set common parameters
		builder
				.setMailFrom(emailAddressing.getMailFrom())
				.setCc(emailAddressing.getMailCcString())
				.setBcc(emailAddressing.getMailBCcString())
				.setSubjectLine(subject);

		if (messageBody != null)
			builder.setMessage(messageBody);
			// add message request id when provided.
		else if (messageRequestId != null)
			builder.setMessageDocumentGuid(messageRequestId);


		// build request
		MailDocumentRequest mailDocumentRequest = builder.build();

		// invoke request
		try {
			serviceInvoker.invoke(mailDocumentRequest);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	/**
	 * Lists the available printers on the server.
	 * <p>
	 * Includes the Google Cloud Printers, if configured on the server.
	 *
	 * @return a list of printers
	 * @throws ServerSideException if an error occurs on the server
	 */
	public List<Printer> listXDPrinter() throws ServerSideException {
		return listXDPrinter(null);
	}

	/**
	 * Lists the available printers on the server.
	 * <p>
	 * Includes the Google Cloud Printers, if configured on the server.
	 *
	 * @param filter the filter to use for the printer name
	 * @return a list of printers
	 * @throws ServerSideException if an error occurs on the server
	 */
	public List<Printer> listXDPrinter(String filter) throws ServerSideException {

		// create request builder
		GetPrintersRequest.GetPrintersRequestBuilder builder = new GetPrintersRequest.GetPrintersRequestBuilder();

		// set parameters
		if (filter != null)
			builder.setFilter(filter);

		GetPrintersRequest getPrintersRequest = builder.build();

		try {
			return serviceInvoker.invoke(getPrintersRequest);
		} catch (ServiceInvocationException e) {
			throw new ServerSideException(e);
		}
	}

	@Override
	public void close() {
		executorService.shutdown();
	}
}
