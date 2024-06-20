package io.xtea.strategy.ema;

import lombok.Builder;
import lombok.Getter;

/**
 * EMA calculator.
 *
 * @author xtea
 * @date 2023-03-21 21:58
 */
@Getter
@Builder(toBuilder = true)
public class EMACalculator {

    public static final double SMOOTHING_FACTOR = 0.119;

    private double smoothingFactor;
    private double previousEMA;
    private double avgPrice;

    public EMACalculator(double smoothingFactor, double previousEMA, double avgPrice) {
        this.smoothingFactor = smoothingFactor;
        this.previousEMA = previousEMA;
        this.avgPrice = avgPrice;
    }

    public EMACalculator(double smoothingFactor) {
        this.smoothingFactor = smoothingFactor;
    }

    public double calculateEMA(double value) {
        if (Double.isNaN(previousEMA)) {
            previousEMA = value;
            avgPrice = value;
        } else {
            double ema = (value - previousEMA) * smoothingFactor + previousEMA;
            previousEMA = ema;
            avgPrice = ema;
        }
        return avgPrice;
    }

    public static EMACalculator getInstance() {
        return new EMACalculator(SMOOTHING_FACTOR);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EMACalculator{");
        sb.append("previousEMA=").append(previousEMA);
        sb.append(", avgPrice=").append(avgPrice);
        sb.append('}');
        return sb.toString();
    }
}

