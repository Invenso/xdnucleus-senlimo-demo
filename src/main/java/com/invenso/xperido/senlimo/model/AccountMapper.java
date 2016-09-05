package com.invenso.xperido.senlimo.model;

import com.invenso.xperido.senlimo.model.db.*;
import com.invenso.xperido.senlimo.model.xml.account.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * Maps database entities to the objects generated from the AccountXSD.xsd schema
 */
@Mapper(uses=IntegerMapper.class)
public abstract class AccountMapper {
	public final static AccountMapper INSTANCE = Mappers.getMapper( AccountMapper.class );

	@Mapping(source="accountId", target = "accountID")
	@Mapping(source="accountVat", target = "accountVAT")
	@Mapping(source="accountAddressZip", target="accountAddressZIP")
	@Mapping(source="contactByAccountPrimaryContact", target="accountPrimaryContact")
	@Mapping(source="contactsByAccountId", target="contactAccount")
	public abstract Account dbAccountToXmlAccount(AccountEntity account);

	public Account.ContactAccount dbContactsToXmlContactAccount(Collection<ContactEntity> contacts) {
		Account.ContactAccount contract = new Account.ContactAccount();
		contract.getContact().addAll(dbContactsToXmlContacts(contacts));
		return contract;
	}

	public abstract List<Contact> dbContactsToXmlContacts(Collection<ContactEntity> contacts);

	@Mapping(source="contactId", target = "contactID")
	@Mapping(source="contactVisitorBarcode", target = "contactVsitorBarcode")
	@Mapping(source="accountByContactCompanyAccount.accountId", target = "contactCompanyAccount")
	public abstract Contact dbContactToXmlContact(ContactEntity entity);
}
