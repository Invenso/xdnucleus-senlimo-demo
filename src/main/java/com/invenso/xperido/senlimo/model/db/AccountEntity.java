package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.util.Collection;

/**
 * JPA Entity for the account table
 */
@Entity
@Table(name = "account", schema = "public", catalog = "senlimo")
@NamedQuery(name="AccountEntity.byID", query="select a from AccountEntity a where a.accountId = :id")
public class AccountEntity {
	private Integer accountId;
	private String accountName;
	private Integer accountNumber;
	private String accountAddressLine1;
	private String accountAddressLine2;
	private String accountAddressLine3;
	private String accountAddressCity;
	private String accountAddressZip;
	private String accountAddressState;
	private String accountAddressCountry;
	private String accountVat;
	private ContactEntity contactByAccountPrimaryContact;
	private Collection<ContactEntity> contactsByAccountId;
	private Collection<ContractEntity> contractsByAccountId;
	private Collection<InvoiceEntity> invoicesByAccountId;
	private Collection<QuoteEntity> quotesByAccountId;

	@Id
	@Column(name = "`AccountID`")
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Basic
	@Column(name = "`AccountName`")
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Basic
	@Column(name = "`AccountNumber`")
	public Integer getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Basic
	@Column(name = "`AccountAddressLine1`")
	public String getAccountAddressLine1() {
		return accountAddressLine1;
	}

	public void setAccountAddressLine1(String accountAddressLine1) {
		this.accountAddressLine1 = accountAddressLine1;
	}

	@Basic
	@Column(name = "`AccountAddressLine2`")
	public String getAccountAddressLine2() {
		return accountAddressLine2;
	}

	public void setAccountAddressLine2(String accountAddressLine2) {
		this.accountAddressLine2 = accountAddressLine2;
	}

	@Basic
	@Column(name = "`AccountAddressLine3`")
	public String getAccountAddressLine3() {
		return accountAddressLine3;
	}

	public void setAccountAddressLine3(String accountAddressLine3) {
		this.accountAddressLine3 = accountAddressLine3;
	}

	@Basic
	@Column(name = "`AccountAddressCity`")
	public String getAccountAddressCity() {
		return accountAddressCity;
	}

	public void setAccountAddressCity(String accountAddressCity) {
		this.accountAddressCity = accountAddressCity;
	}

	@Basic
	@Column(name = "`AccountAddressZIP`")
	public String getAccountAddressZip() {
		return accountAddressZip;
	}

	public void setAccountAddressZip(String accountAddressZip) {
		this.accountAddressZip = accountAddressZip;
	}

	@Basic
	@Column(name = "`AccountAddressState`")
	public String getAccountAddressState() {
		return accountAddressState;
	}

	public void setAccountAddressState(String accountAddressState) {
		this.accountAddressState = accountAddressState;
	}

	@Basic
	@Column(name = "`AccountAddressCountry`")
	public String getAccountAddressCountry() {
		return accountAddressCountry;
	}

	public void setAccountAddressCountry(String accountAddressCountry) {
		this.accountAddressCountry = accountAddressCountry;
	}

	@Basic
	@Column(name = "`AccountVAT`")
	public String getAccountVat() {
		return accountVat;
	}

	public void setAccountVat(String accountVat) {
		this.accountVat = accountVat;
	}

	@OneToMany(mappedBy = "accountByContactCompanyAccount")
	public Collection<ContactEntity> getContactsByAccountId() {
		return contactsByAccountId;
	}

	public void setContactsByAccountId(Collection<ContactEntity> contactsByAccountId) {
		this.contactsByAccountId = contactsByAccountId;
	}

	@OneToMany(mappedBy = "accountByContractAccount")
	public Collection<ContractEntity> getContractsByAccountId() {
		return contractsByAccountId;
	}

	public void setContractsByAccountId(Collection<ContractEntity> contractsByAccountId) {
		this.contractsByAccountId = contractsByAccountId;
	}

	@OneToMany(mappedBy = "accountByInvoiceAccount")
	public Collection<InvoiceEntity> getInvoicesByAccountId() {
		return invoicesByAccountId;
	}

	public void setInvoicesByAccountId(Collection<InvoiceEntity> invoicesByAccountId) {
		this.invoicesByAccountId = invoicesByAccountId;
	}

	@OneToMany(mappedBy = "accountByQuoteAccount")
	public Collection<QuoteEntity> getQuotesByAccountId() {
		return quotesByAccountId;
	}

	public void setQuotesByAccountId(Collection<QuoteEntity> quotesByAccountId) {
		this.quotesByAccountId = quotesByAccountId;
	}

	@ManyToOne
	@JoinColumn(name = "`AccountPrimaryContact`", referencedColumnName = "`ContactID`", nullable = false)
	public ContactEntity getContactByAccountPrimaryContact() {
		return contactByAccountPrimaryContact;
	}

	public void setContactByAccountPrimaryContact(ContactEntity contactByAccountPrimaryContact) {
		this.contactByAccountPrimaryContact = contactByAccountPrimaryContact;
	}
}
