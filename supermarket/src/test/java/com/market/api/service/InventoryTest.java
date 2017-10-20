package com.market.api.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.market.api.model.ProductItem;
import com.market.api.service.InvalidStockError;
import com.market.api.service.Inventory;
import com.market.api.util.ErrorMessages;

public class InventoryTest {
	private static final ProductItem ROGER_FEDERER = new ProductItem("", "", "");
	private static final BigDecimal TEA_PRICE = new BigDecimal("15.33");
	private static final ProductItem STOCK_TEA = new ProductItem("A", "1001", "Tea");
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	
	@Test
	public void when_a_null_item_is_added_to_inventory_Then_raises_error() throws Exception {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage(ErrorMessages.NULL_ITEM_ADDED_TO_INVENTORY.message());
		Inventory inventory = new Inventory();
		inventory.add(null, BigDecimal.TEN);
	}
	
	@Test
	public void when_a_null_item_price_is_added_to_inventory_Then_raises_error() throws Exception {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage(ErrorMessages.PRICE_NULL.message());
		
		Inventory inventory = new Inventory();
		inventory.add(new ProductItem("A", "123456", "Book"), null);
	}
	
	@Test
	public void can_add_an_item_to_inventory() throws Exception {
		Inventory inventory = new Inventory();
		inventory.add(new ProductItem("A", "123456", "Book"), BigDecimal.TEN);
		
		assertThat(inventory.getStockCount(), is(1));
	}
	

	@Test
	public void when_an_item_Not_stocked_Then_inventory_raises_error() throws Exception {
		thrown.expect(InvalidStockError.class);
		Inventory inventory = new Inventory();
		inventory.scan(ROGER_FEDERER);
	}
	
	@Test
	public void when_an_item_is_stocked_Then_returns_price() throws Exception {
		Inventory inventory = new Inventory();
		inventory.add(STOCK_TEA, TEA_PRICE);
		assertEquals(TEA_PRICE, inventory.scan(STOCK_TEA));
	}
	
}
