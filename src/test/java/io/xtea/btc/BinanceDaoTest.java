package io.xtea.btc;

import static org.junit.Assert.*;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-04-02 22:32
 */
public class BinanceDaoTest {

    BinanceDao binanceDao = new BinanceDao();

    @Test
    public void balance() {
        BigDecimal usdt = binanceDao.balance("LTC");
        assertTrue(usdt.doubleValue() == 0.0D);
    }

    @Test
    public void balanceUSDT() {
        BigDecimal usdt = binanceDao.balance("USDT");
        assertEquals(0, usdt.doubleValue());
    }
}