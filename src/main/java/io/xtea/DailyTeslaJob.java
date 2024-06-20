package io.xtea;

import io.xtea.core.KLine;
import io.xtea.core.TradeExecutor;
import io.xtea.core.TradeUtils;
import io.xtea.notification.MessageSender;
import io.xtea.strategy.ema.EMACalculator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class DailyTeslaJob {

    @Autowired
    @Qualifier("tigerStockKlineFetcher")
    KLineFetcher kLineFetcher;

    @Autowired
    @Qualifier("tigerUsStock")
    TradeExecutor executor;


    @Autowired
    MessageSender messageSender;

    public static final String INTERVAL = "day";
    public static final String BEGIN_DATE = "2022-01-01";

    /**
     * seconds minutes hours day-of-month month day-of-week
     * 0       0      8        *         *        ?
     */
    @Scheduled(cron = "0 0 2 * * MON")
//    @Scheduled(cron = "0 */1 * ? * *")
    public void run() {
        try {
            log.info("Run at {}", new Date());
            messageSender.send("Start run at " + new Date());
            EMACalculator emaCalculator = trainEMA();
            // get current price.





        } catch (Exception e) {
            log.error(e.getMessage(), e);
            messageSender.send("ERROR!" + e.getMessage());
        }
    }


    public EMACalculator trainEMA() throws Exception {
        EMACalculator emaCalculator = new EMACalculator(0.22);
        List<KLine> lines = kLineFetcher.query(BEGIN_DATE, INTERVAL);
        log.info("query kline from {} to {}, result size is {}", BEGIN_DATE, TradeUtils.DATE_FORMAT.format(new Date()),
                lines.size());
        for (KLine line : lines) {
            emaCalculator.calculateEMA(line.getClosePrice());
        }
        log.info("Warp up done stock with price = {}", emaCalculator.getAvgPrice());
        return emaCalculator;
    }

}
