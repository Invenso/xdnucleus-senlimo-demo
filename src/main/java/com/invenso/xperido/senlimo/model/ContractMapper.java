package com.invenso.xperido.senlimo.model;

import com.invenso.xperido.senlimo.model.db.*;
import com.invenso.xperido.senlimo.model.xml.contract.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * Maps database entities to the objects generated from the ContractData.xsd schema
 */
@Mapper(uses=IntegerMapper.class)
public abstract class ContractMapper {
	public final static ContractMapper INSTANCE = Mappers.getMapper( ContractMapper.class );

	@Mapping(source="contractId", target = "contractID")
	@Mapping(source="accountByContractAccount", target = "contractAccount")
	@Mapping(source="employeeByContractOwningUser", target = "contractOwner")
	@Mapping(source="contactByContractContact", target = "contractContact")
	@Mapping(source="lineitemsByContractId", target="lineItemContract")
	public abstract Contract dbContractToXmlContract(ContractEntity contract);

	public Contract.LineItemContract dbLineItemsToXmlLineItemContract(Collection<LineitemEntity> lineitems) {
		Contract.LineItemContract contract = new Contract.LineItemContract();
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
