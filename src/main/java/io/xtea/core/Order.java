package io.xtea.core;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 21:21
 */
@Value
@Builder(toBuilder = true)
public class Order {

    long time;
    double price;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Order{");
        sb.append("time=").append(TradeUtils.DATE_FORMAT.format(new Date(time)));
        sb.append(", price=").append(price);
        sb.append('}');
        return sb.toString();
    }
}
