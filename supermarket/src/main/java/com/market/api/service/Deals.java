package com.market.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Validate;

import com.market.api.model.Deal;
import com.market.api.util.ErrorMessages;

public class Deals {
	/**
	 * Key is Item Id and value is a map of item count and offers Ex - Item Id =
	 * A001, offers Buy 2 get 40% off, Buy 3 get 50% off
	 * 
	 * "A001" -> 2 -> .40 -> 3 -> .50
	 */
	private final Map<String, Map<Integer, BigDecimal>> productDealsMap = new HashMap<>();

	private final Range<Double> percentageRange = Range.between(0.01, 0.99);

	public Deals add(Deal aDeal) {
		Validate.notNull(aDeal, ErrorMessages.DEAL_IS_NULL.message());
		Validate.notEmpty(aDeal.getProductId(), ErrorMessages.PRODUCT_ID_EMPTY.message());

		if (!percentageRange.contains(aDeal.getOfferPercentage())) {
			throw new IllegalArgumentException(
					(aDeal.getOfferPercentage()+" doesn't belong to 1 to 99% range"));
		}

		Map<Integer, BigDecimal> deals = productDealsMap.get(aDeal.getProductId());
		if (deals == null) {
			deals = new TreeMap<>(Collections.reverseOrder());
			productDealsMap.put(aDeal.getProductId(), deals);
		}

		BigDecimal discount = new BigDecimal(aDeal.getOfferPercentage()).setScale(2, RoundingMode.UP);
		deals.put(aDeal.getCount(), discount);
		return this;
	}

	public Map<Integer, BigDecimal> retrieveDealsFor(String productId) {
		Map<Integer, BigDecimal> deal = productDealsMap.get(productId);
		return deal;
	}

	public boolean hasDiscount(String productId) {
		// TODO Auto-generated method stub
		return retrieveDealsFor(productId) !=  null;
	}
}
