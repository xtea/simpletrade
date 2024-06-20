package io.xtea.strategy.test;

import io.xtea.core.Order;
import io.xtea.core.TradeExecutor;
import io.xtea.core.TradeResult;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:50
 */
@Service("mockTradeExecutor")
public class MockTradeExecutor implements TradeExecutor {

    Order buyOrder;

    @Override
    public Order buy(double price, long time) {
        buyOrder = Order.builder().price(price).time(time).build();
        return buyOrder;
    }

    @Override
    public TradeResult sell(Order sellOrder) {
        if (buyOrder == null) {
            throw new IllegalStateException("No buy order.");
        }
        TradeResult result = TradeResult.builder().buy(buyOrder).sell(sellOrder).build();
        buyOrder = null;
        return result;
    }

    @Override
    public BigDecimal balance() {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean isEmpty() {
        return buyOrder == null;
    }
}
