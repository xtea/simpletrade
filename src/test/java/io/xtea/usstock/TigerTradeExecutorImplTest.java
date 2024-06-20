package io.xtea.usstock;

import static org.junit.Assert.*;

import com.tigerbrokers.stock.openapi.client.struct.enums.ActionType;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2024-02-28 22:04
 */
public class TigerTradeExecutorImplTest {

    TigerTradeExecutorImpl tigerTradeExecutor = new TigerTradeExecutorImpl();


    @Test
    void testBalance() {
        BigDecimal balance = tigerTradeExecutor.balance();
        System.out.println(balance);
        assertTrue(balance.intValue() > 0);
    }


    @Test
    void getTradableQuantity() {
        Integer quantity = tigerTradeExecutor.getTradableQuantity(ActionType.BUY, "TSLA");
        assertTrue(quantity > 0);
        int sell = tigerTradeExecutor.getTradableQuantity(ActionType.SELL, "TSLA");
        assertEquals(0, sell);
    }

    @Test
    void isHold() {
        assertFalse(tigerTradeExecutor.isHold());
    }

    @Test
    void testMarketPrice() {
        double marketPrice = tigerTradeExecutor.getMarketPrice();
        assertTrue(marketPrice > 0);
    }
}