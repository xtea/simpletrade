package io.xtea.core;

import io.xtea.strategy.ema.EMACalculator;

import org.junit.Test;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-21 21:59
 */
public class ExponentialMovingAverageTest {

    EMACalculator average = new EMACalculator(0.5D);

    @Test
    public void calculateEMA() {
        System.out.println(average.calculateEMA(1));
        System.out.println(average.calculateEMA(2));
        System.out.println(average.calculateEMA(5));
        System.out.println(average.calculateEMA(9));
        System.out.println(average.calculateEMA(0));
    }
}