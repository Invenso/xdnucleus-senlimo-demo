package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;

/**
 * JPA Entity for the lineitem table
 */
@Entity
@Table(name = "lineitem", schema = "public", catalog = "senlimo")
public class LineitemEntity {
	private Integer lineItemId;
	private int lineItemQuantity;
	private int lineItemTotal;
	private ProductEntity productByLineItemProduct;
	private QuoteEntity quoteByLineItemQuote;
	private InvoiceEntity invoiceByLineItemInvoice;
	private ContractEntity contractByLineItemContract;

	@Id
	@Column(name = "`LineItemID`")
	public Integer getLineItemId() {
		return lineItemId;
	}

	public void setLineItemId(Integer lineItemId) {
		this.lineItemId = lineItemId;
	}

	@Basic
	@Column(name = "`LineItemQuantity`")
	public int getLineItemQuantity() {
		return lineItemQuantity;
	}

	public void setLineItemQuantity(int lineItemQuantity) {
		this.lineItemQuantity = lineItemQuantity;
	}

	@Basic
	@Column(name = "`LineItemTotal`")
	public int getLineItemTotal() {
		return lineItemTotal;
	}

	public void setLineItemTotal(int lineItemTotal) {
		this.lineItemTotal = lineItemTotal;
	}

	@ManyToOne
	@JoinColumn(name = "`LineItemProduct`", referencedColumnName = "`ProductID`", nullable = false)
	public ProductEntity getProductByLineItemProduct() {
		return productByLineItemProduct;
	}

	public void setProductByLineItemProduct(ProductEntity productByLineItemProduct) {
		this.productByLineItemProduct = productByLineItemProduct;
	}

	@ManyToOne
	@JoinColumn(name = "`LineItemQuote`", referencedColumnName = "`QuoteID`")
	public QuoteEntity getQuoteByLineItemQuote() {
		return quoteByLineItemQuote;
	}

	public void setQuoteByLineItemQuote(QuoteEntity quoteByLineItemQuote) {
		this.quoteByLineItemQuote = quoteByLineItemQuote;
	}

	@ManyToOne
	@JoinColumn(name = "`LineItemInvoice`", referencedColumnName = "`InvoiceID`")
	public InvoiceEntity getInvoiceByLineItemInvoice() {
		return invoiceByLineItemInvoice;
	}

	public void setInvoiceByLineItemInvoice(InvoiceEntity invoiceByLineItemInvoice) {
		this.invoiceByLineItemInvoice = invoiceByLineItemInvoice;
	}

	@ManyToOne
	@JoinColumn(name = "`LineItemContract`", referencedColumnName = "`ContractID`")
	public ContractEntity getContractByLineItemContract() {
		return contractByLineItemContract;
	}

	public void setContractByLineItemContract(ContractEntity contractByLineItemContract) {
		this.contractByLineItemContract = contractByLineItemContract;
	}
}
