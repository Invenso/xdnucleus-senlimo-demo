package com.invenso.xperido.senlimo.model;

import com.invenso.xperido.senlimo.model.db.*;
import com.invenso.xperido.senlimo.model.xml.invoice.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * Maps database entities to the objects generated from the InvoiceData.xsd schema
 */
@Mapper(uses=IntegerMapper.class)
public abstract class InvoiceMapper {
	public final static InvoiceMapper INSTANCE = Mappers.getMapper( InvoiceMapper.class );

	@Mapping(source="invoiceId", target = "invoiceID")
	@Mapping(source="invoiceVatPercentage", target = "invoiceVATPercentage")
	@Mapping(source="lineitemsByInvoiceId", target = "lineItemInvoice")
	@Mapping(source="accountByInvoiceAccount", target = "invoiceAccount")
	@Mapping(source="contactByInvoiceContact", target = "invoiceContact")
	@Mapping(source="employeeByInvoiceOwningUser", target = "invoiceOwner")
	public abstract Invoice dbInvoiceToXmlInvoice(InvoiceEntity invoice);

	public Invoice.LineItemInvoice dbLineItemsToXmlLineItemInvoice(Collection<LineitemEntity> lineitems) {
		Invoice.LineItemInvoice contract = new Invoice.LineItemInvoice();
		contract.getLineitem().addAll(dbLineitemsToXmlLineItems(lineitems));
		return contract;
	}

	public abstract List<Lineitem> dbLineitemsToXmlLineItems(Collection<LineitemEntity> lineitems);

	@Mapping(source="accountId", target = "accountID")
	@Mapping(source="accountVat", target = "accountVAT")
	@Mapping(source="accountAddressZip", target="accountAddressZIP")
	@Mapping(source="contactByAccountPrimaryContact.contactId", target="accountPrimaryContact")
	public abstract Account dbAccountToXmlAccount(AccountEntity account);

	@Mapping(source="userId", target = "userID")
	public abstract Employee dbEmployeeToXmlEmployee(EmployeeEntity employee);

	@Mapping(source="lineItemId", target = "lineItemID")
	@Mapping(source="productByLineItemProduct", target="lineItemProduct")
	@Mapping(source="quoteByLineItemQuote.quoteId", target="lineItemQuote")
	@Mapping(source="invoiceByLineItemInvoice.invoiceId", target="lineItemInvoice")
	@Mapping(source="contractByLineItemContract.contractId", target="lineItemContract")
	public abstract Lineitem dbLineItemToXmlLineitem(LineitemEntity lineitem);

	@Mapping(source="productId", target = "productID")
	public abstract Product dbProductToXmlProduct(ProductEntity product);


	@Mapping(source="contactId", target = "contactID")
	@Mapping(source="contactVisitorBarcode", target = "contactVsitorBarcode")
	@Mapping(source="accountByContactCompanyAccount.accountId", target = "contactCompanyAccount")
	public abstract Contact dbContactToXmlContact(ContactEntity entity);
}
