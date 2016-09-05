package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.util.Collection;

/**
 * JPA Entity for the employee table
 */
@Entity
@Table(name = "employee", schema = "public", catalog = "senlimo")
public class EmployeeEntity {
	private Integer userId;
	private String userFullName;
	private String userFirstName;
	private String userLastName;
	private String userGender;
	private String userEmail;
	private String userTelephoneNumber;
	private String userMobilePhone;
	private String userJobTitle;
	private Collection<ContractEntity> contractsByUserId;
	private Collection<EventEntity> eventsByUserId;
	private Collection<InvoiceEntity> invoicesByUserId;
	private Collection<QuoteEntity> quotesByUserId;

	@Id
	@Column(name = "`UserID`")
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Basic
	@Column(name = "`UserFullName`")
	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}

	@Basic
	@Column(name = "`UserFirstName`")
	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	@Basic
	@Column(name = "`UserLastName`")
	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	@Basic
	@Column(name = "`UserGender`")
	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	@Basic
	@Column(name = "`UserEmail`")
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Basic
	@Column(name = "`UserTelephoneNumber`")
	public String getUserTelephoneNumber() {
		return userTelephoneNumber;
	}

	public void setUserTelephoneNumber(String userTelephoneNumber) {
		this.userTelephoneNumber = userTelephoneNumber;
	}

	@Basic
	@Column(name = "`UserMobilePhone`")
	public String getUserMobilePhone() {
		return userMobilePhone;
	}

	public void setUserMobilePhone(String userMobilePhone) {
		this.userMobilePhone = userMobilePhone;
	}

	@Basic
	@Column(name = "`UserJobTitle`")
	public String getUserJobTitle() {
		return userJobTitle;
	}

	public void setUserJobTitle(String userJobTitle) {
		this.userJobTitle = userJobTitle;
	}

	@OneToMany(mappedBy = "employeeByContractOwningUser")
	public Collection<ContractEntity> getContractsByUserId() {
		return contractsByUserId;
	}

	public void setContractsByUserId(Collection<ContractEntity> contractsByUserId) {
		this.contractsByUserId = contractsByUserId;
	}

	@OneToMany(mappedBy = "employeeByEventOwningUser")
	public Collection<EventEntity> getEventsByUserId() {
		return eventsByUserId;
	}

	public void setEventsByUserId(Collection<EventEntity> eventsByUserId) {
		this.eventsByUserId = eventsByUserId;
	}

	@OneToMany(mappedBy = "employeeByInvoiceOwningUser")
	public Collection<InvoiceEntity> getInvoicesByUserId() {
		return invoicesByUserId;
	}

	public void setInvoicesByUserId(Collection<InvoiceEntity> invoicesByUserId) {
		this.invoicesByUserId = invoicesByUserId;
	}

	@OneToMany(mappedBy = "employeeByQuoteOwningUser")
	public Collection<QuoteEntity> getQuotesByUserId() {
		return quotesByUserId;
	}

	public void setQuotesByUserId(Collection<QuoteEntity> quotesByUserId) {
		this.quotesByUserId = quotesByUserId;
	}
}
