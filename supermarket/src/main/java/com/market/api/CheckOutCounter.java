package com.market.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.market.api.model.Cart;
import com.market.api.model.ProductItem;
import com.market.api.service.Deals;
import com.market.api.service.InvalidStockError;
import com.market.api.service.Inventory;

public class CheckOutCounter {
	private final Inventory inventory;
	private final Deals deals;

	private Logger logger = Logger.getAnonymousLogger();

	public CheckOutCounter(Inventory myInventory, Deals deals) {
		// TODO Auto-generated constructor stub
		this.inventory = myInventory;
		this.deals = deals;
	}

	public BigDecimal checkOut(Cart aShoppingCart) throws InvalidStockError {
		// TODO Auto-generated method stub
		BigDecimal payableAmount = new BigDecimal("0.00").setScale(2, RoundingMode.CEILING);

		Iterator<ProductItem> items = aShoppingCart.getProductItems();

		// Holds items those may have discount
		Map<String, Integer> discountableProductItemsInCart = new HashMap<>();

		while (items.hasNext()) {
			ProductItem item = items.next();
			payableAmount = payableAmount.add(inventory.scan(item.getProductId()));

			String productId = item.getProductId();

			// DB call is costly, if the product item is already in disc list don't make a DB
			// call, just check the local discounted items
			if (discountableProductItemsInCart.containsKey(productId) || deals.hasDiscount(productId)) {
				incrementDiscountableItemCount(discountableProductItemsInCart, productId);
			}
		}

		if (!discountableProductItemsInCart.isEmpty()) {
			BigDecimal totalDiscount = calculateDiscount(discountableProductItemsInCart);
			payableAmount = payableAmount.subtract(totalDiscount);
		}
		return payableAmount;
	}

	private BigDecimal calculateDiscount(Map<String, Integer> discountableProductItemsInCart)
			throws InvalidStockError {
		BigDecimal totalDiscount = new BigDecimal("0.00").setScale(2, RoundingMode.DOWN);
		
		for (String productId : discountableProductItemsInCart.keySet()) {

			Integer discountableItemsCount = numberOfDiscountableItemsInCart(discountableProductItemsInCart,
					productId);

			Map<Integer, BigDecimal> availableDiscountRatesForThisProduct = discountRatesForThe(productId);
			/**
			 * If max discount available on buy 2 get 30% off, the cart has 10 items. it should apply discount on all 10 items.
			 * 
			 */
			while (hasMinItemsToGetDiscount(discountableItemsCount, availableDiscountRatesForThisProduct)) {
				
				for (Integer buyCount : availableDiscountRatesForThisProduct.keySet()) {
					/**
					 * when items more than the discount count, such as 4 discountable items but max
					 * offer on buy 3 get 50%, apply 50% discount on 3 items and no discount on the
					 * 4th item.
					 * this buyCount is sorted, so first get the max discount, then gradually look for other 
					 */
					if (discountableItemsCount >= buyCount) {
						/**
						 * TODO: can keep the value locally in another data structure. no need to scan
						 * again
						 */
						BigDecimal originalProductPrice = inventory.scan(productId);
						BigDecimal availableDiscount = availableDiscountRatesForThisProduct.get(buyCount);

						BigDecimal discountOnSingleProductItem = originalProductPrice.multiply(availableDiscount)
								.setScale(2, RoundingMode.DOWN);
						BigDecimal discountOnAllDiscountableProductItems = discountOnSingleProductItem
								.multiply(new BigDecimal(buyCount)).setScale(2, RoundingMode.DOWN);

						totalDiscount = totalDiscount.add(discountOnAllDiscountableProductItems);
						
						//Need to look for discount on the remaining items
						discountableItemsCount = discountableItemsCount - buyCount;
						break;
					}
				}
			}
		}
		
		logger.log(Level.INFO, "Discount received ="+totalDiscount, totalDiscount);
		return totalDiscount;
	}

	private boolean hasMinItemsToGetDiscount(Integer discountableItemsCount,
			Map<Integer, BigDecimal> availableDiscountRatesForThisProduct) {
		Integer minimumItemCountForGettingDiscount = Collections.min(availableDiscountRatesForThisProduct.keySet());
		return  discountableItemsCount >= minimumItemCountForGettingDiscount;
	}

	private Map<Integer, BigDecimal> discountRatesForThe(String productId) {
		return deals.retrieveDealsFor(productId);
	}

	private Integer numberOfDiscountableItemsInCart(Map<String, Integer> discountedItems, String productId) {
		return discountedItems.get(productId);
	}

	private void incrementDiscountableItemCount(Map<String, Integer> discountableItems, String productId) {
		Integer itemCount = numberOfDiscountableItemsInCart(discountableItems, productId);
		if (itemCount == null) {
			itemCount = 0;
		}
		discountableItems.put(productId, itemCount + 1);
	}

}
