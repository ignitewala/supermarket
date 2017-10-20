package com.market.api.util;

public enum ErrorMessages {
	NULL_ITEM_ADDED_TO_CART("You cannot add a null item to your cart"), NULL_ITEM_ADDED_TO_INVENTORY(
			"You cannot add a null item to inventory"), PRICE_NULL(
					"Price cannot be null"), DEAL_IS_NULL("Deal is null"), PRODUCT_ID_EMPTY("Product Id is null or empty");

	private String msg;

	private ErrorMessages(String msg) {
		this.msg = msg;
	}

	public String message() {
		return msg;
	}
}
