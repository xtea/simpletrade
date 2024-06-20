package io.xtea.btc;

import com.binance.connector.client.impl.SpotClientImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

/**
 * TODO: doc this.
 * <p>
 * https://github.com/binance/binance-connector-java
 *
 * @author xtea
 * @date 2023-03-21 22:37
 */
public class BtcDemo {

//    public static void main(String[] args) {
//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("RKGWjl5uFlJW2fnkVgOrOcfcTTZAtow6dcjPm494xL9ylnp7uhFmWY2jzyMhJUGd",
//                "olxUfqZvLNlDrwT8CfZbY4yWsTIDtvLFV9hSLvlxpda0OWVz0Ce65eIyMvGT7saR");
//
//    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SpotClientImpl client = new SpotClientImpl("RKGWjl5uFlJW2fnkVgOrOcfcTTZAtow6dcjPm494xL9ylnp7uhFmWY2jzyMhJUGd",
                "olxUfqZvLNlDrwT8CfZbY4yWsTIDtvLFV9hSLvlxpda0OWVz0Ce65eIyMvGT7saR", "https://api3.binance.com");

        // 2023-01-16

        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("timestamp", "1680498904669");
        String account = BinanceClientFactor.client.createTrade().account(parameters);
        System.out.println(account);
    }
}
