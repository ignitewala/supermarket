package com.market.api.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.market.api.model.Cart;
import com.market.api.model.ProductItem;
import com.market.api.util.ErrorMessages;

public class CartTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void when_a_null_item_is_added_to_a_cart_Then_it_raises_error() throws Exception {
		// Set expectation
		thrown.expect(NullPointerException.class);
		thrown.expectMessage(ErrorMessages.NULL_ITEM_ADDED_TO_CART.message());

		Cart cart = new Cart();
		cart.add(null);
	}

	@Test
	public void an_item_can_be_added_to_the_cart() throws Exception {
		Cart cart = new Cart();
		cart.add(itemBuilder(inventory[0]));
		assertThat(cart.getItemcount(), is(1));
	}

	@Test
	public void many_items_can_be_added_to_the_cart() throws Exception {
		Cart cart = new Cart();
		cart.add(itemBuilder(inventory[0])).add(itemBuilder(inventory[1]));
		assertThat(cart.getItemcount(), is(2));
	}

	@Test
	public void when_items_are_added_they_can_be_browsed() throws Exception {
		Cart cart = new Cart();
		cart.add(itemBuilder(inventory[0]));
		assertThat(cart.getItemcount(), is(1));
		
		assertThat(cart.getProductItems().next(), is(notNullValue()));
	}

	private ProductItem itemBuilder(String... str) {
		return new ProductItem(str[0], str[1], str[2]);
	}

	private final String[][] inventory = new String[][] { { "111", "A004959", "Sugar" }, { "111", "A004959", "Sugar" },
			{ "222", "ZZZ0001", "Soccar Ball" } };
}
