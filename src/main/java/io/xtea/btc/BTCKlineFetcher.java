package io.xtea.btc;

import io.xtea.KLineFetcher;
import io.xtea.core.KLine;
import io.xtea.core.TradeUtils;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Binance BTC Kline Fetcher.
 *
 * @author xtea
 * @date 2023-03-30 20:41
 */
@Service("btcKlineFetcher")
public class BTCKlineFetcher extends BinanceDao implements KLineFetcher {

    public List<KLine> query(String startDate, String interval) throws Exception {
        LinkedHashMap<String, Object> parameters = getQueryParameters(
                TradeUtils.getBeginTime(startDate) + "",
                null,
                interval);
        return getKLines(parameters);
    }

    public List<KLine> query(String startDate, String endDate, String interval) throws Exception {
        LinkedHashMap<String, Object> parameters = getQueryParameters(
                TradeUtils.getBeginTime(startDate) + "",
                TradeUtils.getCloseTime(endDate) + "",
                interval);

        return getKLines(parameters);
    }

    private List<KLine> getKLines(LinkedHashMap<String, Object> parameters) {
        String rawJsonResult = BinanceClientFactor.client.createMarket().klines(parameters);
        List<List<Object>> list = gson.fromJson(rawJsonResult, List.class);
        List<KLine> result = new ArrayList<>();
        for (List<Object> obj : list) {
            Number dateTime = (Number) obj.get(0);
            double openPrice = NumberUtils.createDouble(obj.get(1).toString());
            double highPrice = NumberUtils.createDouble(obj.get(2).toString());
            double lowPrice = NumberUtils.createDouble(obj.get(3).toString());
            double closePrice = NumberUtils.createDouble(obj.get(4).toString());
            result.add(KLine.builder()
                    .openPrice(openPrice)
                    .closePrice(closePrice)
                    .highPrice(highPrice)
                    .lowPrice(lowPrice)
                    .openTime(dateTime.longValue())
                    .build());
        }
        return result;
    }

    private LinkedHashMap<String, Object> getQueryParameters(String startDate,
                                                             String endDate,
                                                             String interval) throws ParseException {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("symbol", "BTCUSDT");
        parameters.put("interval", interval);
        parameters.put("startTime", startDate);
        if (endDate != null) {
            parameters.put("endTime", endDate);
        }
        return parameters;
    }

}
