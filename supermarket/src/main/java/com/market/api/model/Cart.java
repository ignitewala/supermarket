package com.market.api.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import com.market.api.util.ErrorMessages;



public class Cart {
	
	private final List<ProductItem> items = new ArrayList<>();

	public Cart add(ProductItem item) {
		// TODO Auto-generated method stub
		Validate.notNull(item, ErrorMessages.NULL_ITEM_ADDED_TO_CART.message());
		items.add(item);
		
		return this;
	}

	public Iterator<ProductItem> getProductItems(){
		return items.iterator();
	}
	
	public int getItemcount() {
		return items.size();
	}
}
