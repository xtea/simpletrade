package io.xtea.btc;

import com.google.gson.Gson;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-04-02 22:18
 */
public class BinanceDao {


    public static final int BIT_COIN_SCALE = 5;
    Gson gson = new Gson();

    long getServerTime() {
        String jsonStr = BinanceClientFactor.client.createMarket().time();
        Map map = gson.fromJson(jsonStr, Map.class);
        return ((Number) map.get("serverTime")).longValue();
    }


    public BigDecimal balance(String symbol) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("timestamp", getServerTime());
        String accountStr = BinanceClientFactor.client.createTrade().account(parameters);
        Map account = gson.fromJson(accountStr, Map.class);
        List<Map<String, Object>> list = (List<Map<String, Object>>) account.get("balances");
        for (Map<String, Object> stringObjectMap : list) {
            if (symbol.equals(stringObjectMap.get("asset"))) {
                BigDecimal free = NumberUtils.createBigDecimal((String) stringObjectMap.get("free"));
                return free.setScale(BIT_COIN_SCALE, BigDecimal.ROUND_DOWN);
            }
        }
        return BigDecimal.ZERO;
    }

    public String makeOder(String symbol, String side,
                           Optional<Double> quantity,
                           Optional<Double> quoteOrderQty) {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        // BTCUSDT
        parameters.put("symbol", symbol);
        //LIMIT 限价单
//        MARKET 市价单
//        STOP_LOSS 止损单
//        STOP_LOSS_LIMIT 限价止损单
//        TAKE_PROFIT 止盈单
//        TAKE_PROFIT_LIMIT 限价止盈单
//        LIMIT_MAKER 限价只挂单
        parameters.put("type", "MARKET");

//        订单方向 (方向 side):
//        BUY 买入
//        SELL 卖出
        parameters.put("side", side);
        parameters.put("timestamp", getServerTime());
//        使用 quoteOrderQty 的市价单MARKET 明确的是通过买入(或卖出)想要花费(或获取)的报价资产数量; 此时的正确报单数量将会以市场流动性和quoteOrderQty被计算出来。
//        以BTCUSDT为例, quoteOrderQty=100:
//        下买单的时候, 订单会尽可能的买进价值100USDT的BTC.
//        下卖单的时候, 订单会尽可能的卖出价值100USDT的BTC.
//          使用 quoteOrderQty 的市价单MARKET不会突破LOT_SIZE的限制规则; 报单会按给定的quoteOrderQty尽可能接近地被执行。
        quantity.ifPresent((v) -> parameters.put("quantity", v));
        quoteOrderQty.ifPresent((v) -> parameters.put("quoteOrderQty", v));
        return BinanceClientFactor.client.createTrade().newOrder(parameters);
    }


    double toDouble(String str) {
        return NumberUtils.toDouble(str);
    }

    long toLong(String str) {
        return NumberUtils.toLong(str);
    }
}
