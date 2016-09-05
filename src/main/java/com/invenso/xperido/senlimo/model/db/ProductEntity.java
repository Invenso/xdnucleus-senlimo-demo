package com.invenso.xperido.senlimo.model.db;

import javax.persistence.*;
import java.util.Collection;

/**
 * JPA Entity for the product table
 */
@Entity
@Table(name = "product", schema = "public", catalog = "senlimo")
public class ProductEntity {
	private Integer productId;
	private String productName;
	private String productNumber;
	private String productType;
	private String productDescription;
	private int productPricePerUnit;
	private String productImage;
	private Integer productBarcode;
	private Collection<LineitemEntity> lineitemsByProductId;

	@Id
	@Column(name = "`ProductID`")
	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	@Basic
	@Column(name = "`ProductName`")
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Basic
	@Column(name = "`ProductNumber`")
	public String getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	@Basic
	@Column(name = "`ProductType`")
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	@Basic
	@Column(name = "`ProductDescription`")
	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	@Basic
	@Column(name = "`ProductPricePerUnit`")
	public int getProductPricePerUnit() {
		return productPricePerUnit;
	}

	public void setProductPricePerUnit(int productPricePerUnit) {
		this.productPricePerUnit = productPricePerUnit;
	}

	@Basic
	@Column(name = "`ProductImage`")
	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	@Basic
	@Column(name = "`ProductBarcode`")
	public Integer getProductBarcode() {
		return productBarcode;
	}

	public void setProductBarcode(Integer productBarcode) {
		this.productBarcode = productBarcode;
	}

	@OneToMany(mappedBy = "productByLineItemProduct")
	public Collection<LineitemEntity> getLineitemsByProductId() {
		return lineitemsByProductId;
	}

	public void setLineitemsByProductId(Collection<LineitemEntity> lineitemsByProductId) {
		this.lineitemsByProductId = lineitemsByProductId;
	}
}
