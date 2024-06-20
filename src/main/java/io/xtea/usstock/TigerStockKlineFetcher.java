package io.xtea.usstock;

import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlineItem;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.KlinePoint;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteKlineRequest;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteKlineResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.RightOption;
import com.tigerbrokers.stock.openapi.client.util.DateUtils;
import io.xtea.AppConfig;
import io.xtea.KLineFetcher;
import io.xtea.core.KLine;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-04-23 21:41
 */
@Slf4j
@Service("tigerStockKlineFetcher")
public class TigerStockKlineFetcher implements KLineFetcher {

    private static TigerHttpClient client = AppConfig.client;

    private String symbol = "QQQ";

    private static Map<String, Object> cache = new HashMap<>();

    @Override
    @Cacheable
    public List<KLine> query(String startDate, String endDate, String interval) throws Exception {
        String key = startDate + endDate + interval;
        if (cache.containsKey(key)) {
            return (List<KLine>) cache.get(key);
        }

        List<String> symbols = new ArrayList<>();
        symbols.add(symbol);
        QuoteKlineResponse response =
                client.execute(QuoteKlineRequest.newRequest(symbols, KType.valueOf(interval), startDate, endDate)
                        .withLimit(1000)
                        .withRight(RightOption.br));
        log.info(response.getMessage());
        if (response.isSuccess()) {
//            System.out.println(Arrays.toString(response.getKlineItems().toArray()));
            List<KlinePoint> klinePoints = response.getKlineItems().stream()
                    .findFirst().map(KlineItem::getItems).orElse(Collections.emptyList());
            List<KLine> result = klinePoints.stream().map(this::convert).collect(Collectors.toList());
            cache.put(key, result);
            return result;

        } else {
            throw new IllegalStateException(response.getMessage());
        }
    }

    private KLine convert(KlinePoint klinePoint) {
        return KLine.builder()
                .closePrice(klinePoint.getClose())
                .openPrice(klinePoint.getOpen())
                .openTime(klinePoint.getTime())
                .highPrice(klinePoint.getHigh())
                .lowPrice(klinePoint.getLow())
                .build();
    }


    @Override
    public List<KLine> query(String startDate, String interval) throws Exception {
        return this.query(startDate, DateUtils.printSystemDate(), interval);
    }
}
