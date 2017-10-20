package com.market.api.model;

/**
 * 
 * Not putting it in hashmap
 *
 */
public class ProductItem {

	private final String productId;
	private final String serialNo;
	private final String desciption;

	public ProductItem(String id, String serialNo, String desciption) {
		super();
		this.productId = id;
		this.serialNo = serialNo;
		this.desciption = desciption;
	}

	public String getProductId() {
		return productId;
	}
	
}
