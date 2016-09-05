package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * JPA Entity for the invoice table
 */
@Entity
@Table(name = "invoice", schema = "public", catalog = "senlimo")
@NamedQuery(name="InvoiceEntity.byID", query="select c from InvoiceEntity c where c.invoiceId = :id")
public class InvoiceEntity {
	private Integer invoiceId;
	private String invoiceNumber;
	private String invoiceName;
	private Date invoiceEffectiveFrom;
	private Date invoiceEffectiveTo;
	private int invoiceDiscountPercentage;
	private int invoiceVatPercentage;
	private AccountEntity accountByInvoiceAccount;
	private EmployeeEntity employeeByInvoiceOwningUser;
	private ContactEntity contactByInvoiceContact;
	private Collection<LineitemEntity> lineitemsByInvoiceId;

	@Id
	@Column(name = "`InvoiceID`")
	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Basic
	@Column(name = "`InvoiceNumber`")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	@Basic
	@Column(name = "`InvoiceName`")
	public String getInvoiceName() {
		return invoiceName;
	}

	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}

	@Basic
	@Column(name = "`InvoiceEffectiveFrom`")
	public Date getInvoiceEffectiveFrom() {
		return invoiceEffectiveFrom;
	}

	public void setInvoiceEffectiveFrom(Date invoiceEffectiveFrom) {
		this.invoiceEffectiveFrom = invoiceEffectiveFrom;
	}

	@Basic
	@Column(name = "`InvoiceEffectiveTo`")
	public Date getInvoiceEffectiveTo() {
		return invoiceEffectiveTo;
	}

	public void setInvoiceEffectiveTo(Date invoiceEffectiveTo) {
		this.invoiceEffectiveTo = invoiceEffectiveTo;
	}

	@Basic
	@Column(name = "`InvoiceDiscountPercentage`")
	public int getInvoiceDiscountPercentage() {
		return invoiceDiscountPercentage;
	}

	public void setInvoiceDiscountPercentage(int invoiceDiscountPercentage) {
		this.invoiceDiscountPercentage = invoiceDiscountPercentage;
	}

	@Basic
	@Column(name = "`InvoiceVATPercentage`")
	public int getInvoiceVatPercentage() {
		return invoiceVatPercentage;
	}

	public void setInvoiceVatPercentage(int invoiceVatPercentage) {
		this.invoiceVatPercentage = invoiceVatPercentage;
	}

	@ManyToOne
	@JoinColumn(name = "`InvoiceAccount`", referencedColumnName = "`AccountID`", nullable = false)
	public AccountEntity getAccountByInvoiceAccount() {
		return accountByInvoiceAccount;
	}

	public void setAccountByInvoiceAccount(AccountEntity accountByInvoiceAccount) {
		this.accountByInvoiceAccount = accountByInvoiceAccount;
	}

	@ManyToOne
	@JoinColumn(name = "`InvoiceOwningUser`", referencedColumnName = "`UserID`", nullable = false)
	public EmployeeEntity getEmployeeByInvoiceOwningUser() {
		return employeeByInvoiceOwningUser;
	}

	public void setEmployeeByInvoiceOwningUser(EmployeeEntity employeeByInvoiceOwningUser) {
		this.employeeByInvoiceOwningUser = employeeByInvoiceOwningUser;
	}

	@ManyToOne
	@JoinColumn(name = "`InvoiceContact`", referencedColumnName = "`ContactID`", nullable = false)
	public ContactEntity getContactByInvoiceContact() {
		return contactByInvoiceContact;
	}

	public void setContactByInvoiceContact(ContactEntity contactByInvoiceContact) {
		this.contactByInvoiceContact = contactByInvoiceContact;
	}

	@OneToMany(mappedBy = "invoiceByLineItemInvoice")
	public Collection<LineitemEntity> getLineitemsByInvoiceId() {
		return lineitemsByInvoiceId;
	}

	public void setLineitemsByInvoiceId(Collection<LineitemEntity> lineitemsByInvoiceId) {
		this.lineitemsByInvoiceId = lineitemsByInvoiceId;
	}
}
