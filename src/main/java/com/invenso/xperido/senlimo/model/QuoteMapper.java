package com.invenso.xperido.senlimo.model;

import com.invenso.xperido.senlimo.model.db.*;
import com.invenso.xperido.senlimo.model.xml.quote.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * Maps database entities to the objects generated from the QuoteData.xsd schema
 */
@Mapper(uses=IntegerMapper.class)
public abstract class QuoteMapper {
	public final static QuoteMapper INSTANCE = Mappers.getMapper( QuoteMapper.class );

	@Mapping(source="quoteId", target="quoteID")
	@Mapping(source="quoteVatPercentage", target = "quoteVATPercentage")
	@Mapping(source="accountByQuoteAccount", target="quoteAccount")
	@Mapping(source="contactByQuoteContact", target="quoteContact")
	@Mapping(source="employeeByQuoteOwningUser", target="quoteOwner")
	@Mapping(source="lineitemsByQuoteId", target="lineItemQuote")
	public abstract Quote dbQuoteToXmlQuote(QuoteEntity quote);

	public Quote.LineItemQuote dbLineItemsToXmlLineItemQuote(Collection<LineitemEntity> lineitems) {
		Quote.LineItemQuote contract = new Quote.LineItemQuote();
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
