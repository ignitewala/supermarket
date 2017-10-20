package com.market.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.market.api.model.Cart;
import com.market.api.model.ProductItem;
import com.market.api.service.Deals;
import com.market.api.service.Inventory;

/**
 * <u><b>Description:</b></u> The checkout process will scan each item and get
 * the price, finally apply discount. It will have a pre-configured inventory
 * with item prices. The scanning process will retrieve price from inventory.
 * Each item will have a serialnumber, id, description
 * <ul>
 * ID = 001, SER No = 1001, Desc = 'Classic butter'
 * </ul>
 * System will have Offers/deals as a sequence of rules. <br>
 * <br>
 * <b><u>Assumptions</u></b>: each deal will be associated with only one
 * product, such as product A will have its own deal.Product 'B' will have its
 * own rule <b><i>It will not combine 'A' and 'B' </i></b>such as buy 2 pieces
 * of item 'A' and 3 pieces of item 'B' then get 60% off. <br>
 * Each product can have many rules such as - buy 2 get 30% off, buy 3 get 40%
 * off, buy 5 get 50% etc. System will apply the maximum possible discount - for
 * example if one buys 5 items then instead of giving 30% on 2 items and 40% on
 * remaining 3 items, it will calculate flat 50% discount
 *
 * @author Sujoy
 *
 */
public class CheckOutTest {

	private static final BigDecimal DISCOUNT_30_PERCENT = new BigDecimal(".30").setScale(2, RoundingMode.UP);
	private static final BigDecimal DISCOUNT_40_PERCENT = new BigDecimal(".40").setScale(2, RoundingMode.UP);
	private static final BigDecimal DISCOUNT_50_PERCENT = new BigDecimal(".50").setScale(2, RoundingMode.UP);
	
	private static final BigDecimal ONE_HUNDRED_AND_FORTY_DOLLARS = new BigDecimal("140.00");
	private static final BigDecimal TEN_DOLLARS = new BigDecimal("10.00");
	private static final BigDecimal HUNDRED_DOLLARS = new BigDecimal("100.00");
	private static final BigDecimal ZERO = new BigDecimal("0.00");

	private static final int FOUR_ITEMS = 4;
	private static final int THREE_ITEMS = 3;
	private static final int TWO_ITEMS = 2;

	private static final String PRODUCT_ID = "A";

	private static final ProductItem MILK = new ProductItem(PRODUCT_ID, "001", "Milk");

	@Mock
	private Inventory inventory;

	@Mock
	private Deals deals;

	private CheckOutCounter counter;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		counter = new CheckOutCounter(inventory, deals);
	}

	@Test
	public void when_the_shopping_cart_is_empty_Then_the_actual_amount_to_pay_is_zero() throws Exception {
		Cart myCart = new Cart();
		CheckOutCounter atCounter = new CheckOutCounter(new Inventory(), new Deals());
		BigDecimal actualAmountToPay = atCounter.checkOut(myCart);

		assertThat(actualAmountToPay, is(ZERO));
	}

	@Test
	public void when_there_is_no_discount_offer_And_only_one_item_in_cart_Then_doesnt_apply_any_discount()
			throws Exception {
		Inventory myInventory = new Inventory();
		myInventory.add(MILK, BigDecimal.TEN);

		Cart myCart = new Cart();
		myCart.add(MILK);
		CheckOutCounter atCounter = new CheckOutCounter(myInventory, new Deals());
		BigDecimal actualAmountToPay = atCounter.checkOut(myCart);

		assertThat(actualAmountToPay, is(TEN_DOLLARS));
	}

	@Test
	public void when_a_product_has_a_buy_two_get_30_percent_discount_deal_Then_deducts_the_discount_amount_()
			throws Exception {
		// set deals
		when(deals.hasDiscount(eq(PRODUCT_ID))).thenReturn(true);
		HashMap<Integer, BigDecimal> discounts = new HashMap<Integer, BigDecimal>();
		discounts.put(TWO_ITEMS, DISCOUNT_30_PERCENT);
		when(deals.retrieveDealsFor(eq(PRODUCT_ID))).thenReturn(discounts);

		// set inventory price catalog
		// when(inventory.scan(isA(ProductItem.class))).thenReturn(HUNDRED_DOLLARS);
		when(inventory.scan(eq(PRODUCT_ID))).thenReturn(HUNDRED_DOLLARS);

		// Setup Data, add two milk bottles
		Cart myCart = new Cart();
		myCart.add(MILK);
		myCart.add(MILK);

		// Test
		BigDecimal totalAmount = counter.checkOut(myCart);
		assertThat(totalAmount, is(ONE_HUNDRED_AND_FORTY_DOLLARS));

	}

	@Test
	public void when_a_product_has_many_deals_Then_picks_the_matching_deal() throws Exception {
		/**
		 * set deals - 2 items 30%, 3 items 40%, 4 items 50%
		 */
		when(deals.hasDiscount(eq(PRODUCT_ID))).thenReturn(true);
		Map<Integer, BigDecimal> discounts = new TreeMap<>(Collections.reverseOrder());

		discounts.put(TWO_ITEMS, DISCOUNT_30_PERCENT);
		discounts.put(THREE_ITEMS, DISCOUNT_40_PERCENT);
		discounts.put(FOUR_ITEMS, DISCOUNT_50_PERCENT);

		when(deals.retrieveDealsFor(eq(PRODUCT_ID))).thenReturn(discounts);

		// set inventory price catalog
		// when(inventory.scan(isA(ProductItem.class))).thenReturn(HUNDRED_DOLLARS);
		when(inventory.scan(eq(PRODUCT_ID))).thenReturn(HUNDRED_DOLLARS);

		/**
		 * Setup Data, add three milk bottles,
		 */
		Cart myCart = new Cart();
		myCart.add(MILK);
		myCart.add(MILK);
		myCart.add(MILK);

		/**
		 * Test, 3 bottles should avail the 40% offer, which is 120$
		 */
		BigDecimal totalAmount = counter.checkOut(myCart);
		assertThat(totalAmount, is(new BigDecimal("180.00")));

	}

	@Test
	public void when_discountable_items_more_than_the_max_offer_count_Then_applies_the_discount_on_number_of_offer_item_count()
			throws Exception {
		/**
		 * set deals - 2 items 30%
		 */
		when(deals.hasDiscount(eq(PRODUCT_ID))).thenReturn(true);
		Map<Integer, BigDecimal> discounts = new TreeMap<>(Collections.reverseOrder());
		discounts.put(TWO_ITEMS, DISCOUNT_30_PERCENT);

		when(deals.retrieveDealsFor(eq(PRODUCT_ID))).thenReturn(discounts);

		// set inventory price catalog
		// when(inventory.scan(isA(ProductItem.class))).thenReturn(HUNDRED_DOLLARS);
		when(inventory.scan(eq(PRODUCT_ID))).thenReturn(HUNDRED_DOLLARS);

		/**
		 * Setup Data, add three milk bottles, but it should get offer on 2 bottles and
		 * the 3rd bottle should be fully charged
		 */
		Cart myCart = new Cart();
		myCart.add(MILK);
		myCart.add(MILK);
		myCart.add(MILK);

		/**
		 * Test, 30% off on 2 bottles + 0% off on the 3rd - (60 off from 300)
		 */
		BigDecimal totalAmount = counter.checkOut(myCart);
		assertThat(totalAmount, is(new BigDecimal("240.00")));

	}

	@Test
	public void when_a_product_has_many_deals_Then_picks_the_matching_deal_based_on_count() throws Exception {
		/**
		 * set deals - 2 items 30%, 3 items 40%
		 */
		when(deals.hasDiscount(eq(PRODUCT_ID))).thenReturn(true);
		Map<Integer, BigDecimal> discounts = new TreeMap<>(Collections.reverseOrder());

		discounts.put(TWO_ITEMS, DISCOUNT_30_PERCENT);
		discounts.put(THREE_ITEMS, DISCOUNT_40_PERCENT);

		when(deals.retrieveDealsFor(eq(PRODUCT_ID))).thenReturn(discounts);

		// set inventory price catalog
		// when(inventory.scan(isA(ProductItem.class))).thenReturn(HUNDRED_DOLLARS);
		when(inventory.scan(eq(PRODUCT_ID))).thenReturn(HUNDRED_DOLLARS);

		/**
		 * Setup Data, add 5 milk bottles,
		 */
		Cart myCart = new Cart();
		myCart.add(MILK);
		myCart.add(MILK);
		myCart.add(MILK);
		myCart.add(MILK);
		myCart.add(MILK);

		/**
		 * Test, 3 bottles should avail the 40% offer($120), remaining 2 bottles should
		 * get 30% ($60) - total $180 off from $500. total payable = $320
		 */
		BigDecimal totalAmount = counter.checkOut(myCart);
		assertThat(totalAmount, is(new BigDecimal("320.00")));

	}
}
