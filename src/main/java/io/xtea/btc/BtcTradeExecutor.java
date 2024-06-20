package io.xtea.btc;

import io.xtea.core.Order;
import io.xtea.core.TradeExecutor;
import io.xtea.core.TradeResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * BTC Trader.
 *
 * @author xtea
 * @date 2023-04-02 22:10
 */
@Slf4j
@Service("btcTradeExecutor")
public class BtcTradeExecutor extends BinanceDao implements TradeExecutor {


    private static final String TOKEN = "BTC";
    private static final String BASE = "USDT";
    private static final String SYMBOL = TOKEN + BASE;

    @Override
    public Order buy(double price, long time) {
        log.info("Buy {} order {} at {}.", TOKEN, price, new Date(time));
        BigDecimal balance = this.balance(BASE);
        makeOder(SYMBOL, "BUY", Optional.empty(), Optional.of(balance.doubleValue()));
        return Order.builder().price(price).time(time).build();
    }

    @Override
    public TradeResult sell(Order sellOrder) throws Exception {
        BigDecimal balance = this.balance(TOKEN);
        if (!isEmpty(balance.doubleValue())) {
            log.info("Get {} = {}, make sell order.", TOKEN, balance);
            String result = makeOder(SYMBOL, "SELL", Optional.of(balance.doubleValue()), Optional.empty());
            Map fromJson = gson.fromJson(result, Map.class);
            double sellPrice = toDouble((String) fromJson.get("price"));
            long time = toLong(String.valueOf(fromJson.get("workingTime")));
            // override actual order.
            Order actualSellOrder = Order.builder().time(time).price(sellPrice).build();
            log.info("Make sell order success, {}", actualSellOrder);
            return TradeResult.builder()
                    .sell(actualSellOrder)
                    .build();
        } else {
            log.error("No {} balance. Cannot sell it.", TOKEN);
            throw new IllegalStateException("No BTC balance. Cannot sell it. " + sellOrder);
        }
    }

    @Override
    public BigDecimal balance() {
        return this.balance(TOKEN);
    }

    @Override
    public boolean isEmpty() {
        return isEmpty(balance().doubleValue());
    }

    public boolean isEmpty(double value) {
        return value < 0.001D;
    }
}
