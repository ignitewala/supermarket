package com.market.api.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.market.api.model.ProductItem;
import com.market.api.util.ErrorMessages;

public class Inventory {
	private final Map<String, BigDecimal> stocksMap = new HashMap<>();

	public void add(ProductItem stock, BigDecimal price) {
		// TODO Auto-generated method stub
		Validate.notNull(stock, ErrorMessages.NULL_ITEM_ADDED_TO_INVENTORY.message());
		Validate.notNull(price, ErrorMessages.PRICE_NULL.message());
		
		stocksMap.put(stock.getProductId(), price);
	}
	
	public int getStockCount() {
		return stocksMap.size();
	}

	public BigDecimal scan(ProductItem item) throws InvalidStockError{
		return this.scan(item.getProductId());
	}
	
	public BigDecimal scan(String productId) throws InvalidStockError {
		BigDecimal price = stocksMap.get(productId);
		if(price == null) {
			throw new InvalidStockError(productId);
		}
		return price;
	}
}
