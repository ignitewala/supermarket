package com.market.api.model;

public class Deal {

	private final String productId;
	private final int count;
	private final double offerPercentage;

	public Deal(String productId, int count, double offerPercentage) {
		super();
		this.productId = productId;
		this.count = count;
		this.offerPercentage = offerPercentage;
	}

	public String getProductId() {
		return productId;
	}

	public int getCount() {
		return count;
	}

	public double getOfferPercentage() {
		return offerPercentage;
	}

}
