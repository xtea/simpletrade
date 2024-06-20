package io.xtea.core;

import java.math.BigDecimal;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:20
 */
public interface TradeExecutor {

    Order buy(double price, long time);

    TradeResult sell(Order sellOrder) throws Exception;

    BigDecimal balance();

    default double getMarketPrice() {
        throw new UnsupportedOperationException();
    }

    default boolean isEmpty() {
        return balance().doubleValue() == 0;
    }

    default boolean isHold() {
        return !isEmpty();
    }
}
