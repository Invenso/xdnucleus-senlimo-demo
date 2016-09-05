package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;

/**
 * JPA Entity for the contact table
 */
@Entity
@Table(name = "contact", schema = "public", catalog = "senlimo")
public class ContactEntity {
	private Integer contactId;
	private String contactFullName;
	private String contactFirstName;
	private String contactLastName;
	private String contactGender;
	private String contactEmail;
	private String contactTelephoneNumber;
	private String contactMobilePhone;
	private String contactJobTitle;
	private Integer contactMagazineSubscription;
	private String contactProfile;
	private String contactVisitorBarcode;
	private AccountEntity accountByContactCompanyAccount;

	@Id
	@Column(name = "`ContactID`")
	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	@Basic
	@Column(name = "`ContactFullName`")
	public String getContactFullName() {
		return contactFullName;
	}

	public void setContactFullName(String contactFullName) {
		this.contactFullName = contactFullName;
	}

	@Basic
	@Column(name = "`ContactFirstName`")
	public String getContactFirstName() {
		return contactFirstName;
	}

	public void setContactFirstName(String contactFirstName) {
		this.contactFirstName = contactFirstName;
	}

	@Basic
	@Column(name = "`ContactLastName`")
	public String getContactLastName() {
		return contactLastName;
	}

	public void setContactLastName(String contactLastName) {
		this.contactLastName = contactLastName;
	}

	@Basic
	@Column(name = "`ContactGender`")
	public String getContactGender() {
		return contactGender;
	}

	public void setContactGender(String contactGender) {
		this.contactGender = contactGender;
	}

	@Basic
	@Column(name = "`ContactEmail`")
	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	@Basic
	@Column(name = "`ContactTelephoneNumber`")
	public String getContactTelephoneNumber() {
		return contactTelephoneNumber;
	}

	public void setContactTelephoneNumber(String contactTelephoneNumber) {
		this.contactTelephoneNumber = contactTelephoneNumber;
	}

	@Basic
	@Column(name = "`ContactMobilePhone`")
	public String getContactMobilePhone() {
		return contactMobilePhone;
	}

	public void setContactMobilePhone(String contactMobilePhone) {
		this.contactMobilePhone = contactMobilePhone;
	}

	@Basic
	@Column(name = "`ContactJobTitle`")
	public String getContactJobTitle() {
		return contactJobTitle;
	}

	public void setContactJobTitle(String contactJobTitle) {
		this.contactJobTitle = contactJobTitle;
	}

	@Basic
	@Column(name = "`ContactMagazineSubscription`")
	public Integer getContactMagazineSubscription() {
		return contactMagazineSubscription;
	}

	public void setContactMagazineSubscription(Integer contactMagazineSubscription) {
		this.contactMagazineSubscription = contactMagazineSubscription;
	}

	@Basic
	@Column(name = "`ContactProfile`")
	public String getContactProfile() {
		return contactProfile;
	}

	public void setContactProfile(String contactProfile) {
		this.contactProfile = contactProfile;
	}

	@Basic
	@Column(name = "`ContactVsitorBarcode`")
	public String getContactVisitorBarcode() {
		return contactVisitorBarcode;
	}

	public void setContactVisitorBarcode(String contactVisitorBarcode) {
		this.contactVisitorBarcode = contactVisitorBarcode;
	}

	@ManyToOne
	@JoinColumn(name = "`ContactCompanyAccount`", referencedColumnName = "`AccountID`", nullable = false)
	public AccountEntity getAccountByContactCompanyAccount() {
		return accountByContactCompanyAccount;
	}

	public void setAccountByContactCompanyAccount(AccountEntity accountByContactCompanyAccount) {
		this.accountByContactCompanyAccount = accountByContactCompanyAccount;
	}
}
