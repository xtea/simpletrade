package io.xtea.btc;

import com.binance.connector.client.impl.SpotClientImpl;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-30 20:39
 */
public class BinanceClientFactor {

    public static final SpotClientImpl client;

    static {
        // trade
        client = new SpotClientImpl("replace your api key",
                "replace your secret key", "https://api3.binance.com");

    }
}
