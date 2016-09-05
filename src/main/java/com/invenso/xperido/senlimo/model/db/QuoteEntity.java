package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * JPA Entity for the quote table
 */
@Entity
@Table(name = "quote", schema = "public", catalog = "senlimo")
@NamedQuery(name="QuoteEntity.byID", query="select q from QuoteEntity q where q.quoteId = :id")
public class QuoteEntity {
	private Integer quoteId;
	private String quoteNumber;
	private String quoteName;
	private Date quoteEffectiveFrom;
	private Date quoteEffectiveTo;
	private int quoteDiscountPercentage;
	private int quoteVatPercentage;
	private Collection<LineitemEntity> lineitemsByQuoteId;
	private AccountEntity accountByQuoteAccount;
	private EmployeeEntity employeeByQuoteOwningUser;
	private ContactEntity contactByQuoteContact;

	@Id
	@Column(name = "`QuoteID`")
	public Integer getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	@Basic
	@Column(name = "`QuoteNumber`")
	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}

	@Basic
	@Column(name = "`QuoteName`")
	public String getQuoteName() {
		return quoteName;
	}

	public void setQuoteName(String quoteName) {
		this.quoteName = quoteName;
	}

	@Basic
	@Column(name = "`QuoteEffectiveFrom`")
	public Date getQuoteEffectiveFrom() {
		return quoteEffectiveFrom;
	}

	public void setQuoteEffectiveFrom(Date quoteEffectiveFrom) {
		this.quoteEffectiveFrom = quoteEffectiveFrom;
	}

	@Basic
	@Column(name = "`QuoteEffectiveTo`")
	public Date getQuoteEffectiveTo() {
		return quoteEffectiveTo;
	}

	public void setQuoteEffectiveTo(Date quoteEffectiveTo) {
		this.quoteEffectiveTo = quoteEffectiveTo;
	}

	@Basic
	@Column(name = "`QuoteDiscountPercentage`")
	public int getQuoteDiscountPercentage() {
		return quoteDiscountPercentage;
	}

	public void setQuoteDiscountPercentage(int quoteDiscountPercentage) {
		this.quoteDiscountPercentage = quoteDiscountPercentage;
	}

	@Basic
	@Column(name = "`QuoteVATPercentage`")
	public int getQuoteVatPercentage() {
		return quoteVatPercentage;
	}

	public void setQuoteVatPercentage(int quoteVatPercentage) {
		this.quoteVatPercentage = quoteVatPercentage;
	}

	@OneToMany(mappedBy = "quoteByLineItemQuote")
	public Collection<LineitemEntity> getLineitemsByQuoteId() {
		return lineitemsByQuoteId;
	}

	public void setLineitemsByQuoteId(Collection<LineitemEntity> lineitemsByQuoteId) {
		this.lineitemsByQuoteId = lineitemsByQuoteId;
	}

	@ManyToOne
	@JoinColumn(name = "`QuoteAccount`", referencedColumnName = "`AccountID`", nullable = false)
	public AccountEntity getAccountByQuoteAccount() {
		return accountByQuoteAccount;
	}

	public void setAccountByQuoteAccount(AccountEntity accountByQuoteAccount) {
		this.accountByQuoteAccount = accountByQuoteAccount;
	}

	@ManyToOne
	@JoinColumn(name = "`QuoteOwningUser`", referencedColumnName = "`UserID`", nullable = false)
	public EmployeeEntity getEmployeeByQuoteOwningUser() {
		return employeeByQuoteOwningUser;
	}

	public void setEmployeeByQuoteOwningUser(EmployeeEntity employeeByQuoteOwningUser) {
		this.employeeByQuoteOwningUser = employeeByQuoteOwningUser;
	}

	@ManyToOne
	@JoinColumn(name = "`QuoteContact`", referencedColumnName = "`ContactID`", nullable = false)
	public ContactEntity getContactByQuoteContact() {
		return contactByQuoteContact;
	}

	public void setContactByQuoteContact(ContactEntity contactByQuoteContact) {
		this.contactByQuoteContact = contactByQuoteContact;
	}
}
