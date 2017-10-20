package com.market.api.service;

import com.market.api.model.ProductItem;

public class InvalidStockError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public InvalidStockError(String productId) {
		super(String.format("%s is not present in inventory", productId));
	}
}
