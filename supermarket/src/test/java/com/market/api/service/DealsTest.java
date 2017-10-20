package com.market.api.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.market.api.model.Deal;
import com.market.api.util.ErrorMessages;

public class DealsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void cannot_add_a_null_deal() throws Exception {
		thrown.expect(NullPointerException.class);
		thrown.expectMessage(ErrorMessages.DEAL_IS_NULL.message());

		Deals deals = new Deals();
		deals.add(null);
	}
	
	@Test
	public void cannot_add_a_deal_with_an_empty_product_id() throws Exception {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(ErrorMessages.PRODUCT_ID_EMPTY.message());

		Deals deals = new Deals();
		deals.add(new Deal("", 1, .99));
	}
	
	@Test
	public void cannot_add_a_deal_with_an_offer_of_negavtive_percentage() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		Deals deals = new Deals();
		deals.add(new Deal("A001", 1, -.99));
	}
	
	@Test
	public void cannot_add_a_deal_with_an_offer_of_more_than_100_percentage() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		Deals deals = new Deals();
		deals.add(new Deal("A001", 1, 1));
	}
	
	@Test
	public void cannot_add_a_deal_with_an_offer_of_zero_percentage() throws Exception {
		thrown.expect(IllegalArgumentException.class);

		Deals deals = new Deals();
		deals.add(new Deal("A001", 1, 0));
	}
	
	@Test
	public void can_add_a_deal_with_an_offer_of_between_1_to_99_percentage() throws Exception {
		Deals deals = new Deals();
		deals.add(new Deal("A001", 1, 0.01));
		assertNotNull(deals.retrieveDealsFor("A001"));
		
		deals.add(new Deal("B001", 1, 0.99));
		assertNotNull(deals.retrieveDealsFor("B001"));
		
		
		deals.add(new Deal("C001", 4, 0.59));
		assertNotNull(deals.retrieveDealsFor("C001"));
	}
	
	@Test
	/**
	 * max count is stored first. such as if you store 2,5,3 then it will store in the following sequence 5,3,2
	 */
	public void when_more_than_one_deals_added_then_deals_are_sorted_reversed_on_count() throws Exception {
		Deals deals = new Deals();
		deals.add(new Deal("B001", 2, 0.30));
		deals.add(new Deal("B001", 5, 0.50));
		deals.add(new Deal("B001", 3, 0.40));
		
		Map<Integer, BigDecimal> retrieveDealsFor = deals.retrieveDealsFor("B001");
		assertNotNull(retrieveDealsFor);
		
		Iterator<Integer> iterator = retrieveDealsFor.keySet().iterator();
		int next = iterator.next();
		assertEquals(5, next);
		
		next = iterator.next();
		assertEquals(3, next);
		
		next = iterator.next();
		assertEquals(2, next);
	}
	
	
}
