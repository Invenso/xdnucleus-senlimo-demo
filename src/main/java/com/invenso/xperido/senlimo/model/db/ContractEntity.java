package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

/**
 * JPA Entity for the contract table
 */
@Entity
@Table(name = "contract", schema = "public", catalog = "senlimo")
@NamedQuery(name="ContractEntity.byID", query="select c from ContractEntity c where c.contractId = :id")
public class ContractEntity {
	private Integer contractId;
	private String contractName;
	private Date contractStartDate;
	private Date contractEndDate;
	private AccountEntity accountByContractAccount;
	private EmployeeEntity employeeByContractOwningUser;
	private ContactEntity contactByContractContact;
	private Collection<LineitemEntity> lineitemsByContractId;

	@Id
	@Column(name = "`ContractID`")
	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	@Basic
	@Column(name = "`ContractName`")
	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	@Basic
	@Column(name = "`ContractStartDate`")
	public Date getContractStartDate() {
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	@Basic
	@Column(name = "`ContractEndDate`")
	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	@ManyToOne
	@JoinColumn(name = "`ContractAccount`", referencedColumnName = "`AccountID`", nullable = false)
	public AccountEntity getAccountByContractAccount() {
		return accountByContractAccount;
	}

	public void setAccountByContractAccount(AccountEntity accountByContractAccount) {
		this.accountByContractAccount = accountByContractAccount;
	}

	@ManyToOne
	@JoinColumn(name = "`ContractOwningUser`", referencedColumnName = "`UserID`", nullable = false)
	public EmployeeEntity getEmployeeByContractOwningUser() {
		return employeeByContractOwningUser;
	}

	public void setEmployeeByContractOwningUser(EmployeeEntity employeeByContractOwningUser) {
		this.employeeByContractOwningUser = employeeByContractOwningUser;
	}

	@ManyToOne
	@JoinColumn(name = "`ContractContact`", referencedColumnName = "`ContactID`")
	public ContactEntity getContactByContractContact() {
		return contactByContractContact;
	}

	public void setContactByContractContact(ContactEntity contactByContractContact) {
		this.contactByContractContact = contactByContractContact;
	}

	@OneToMany(mappedBy = "contractByLineItemContract")
	public Collection<LineitemEntity> getLineitemsByContractId() {
		return lineitemsByContractId;
	}

	public void setLineitemsByContractId(Collection<LineitemEntity> lineitemsByContractId) {
		this.lineitemsByContractId = lineitemsByContractId;
	}
}
