package io.xtea.usstock;

import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteKlineRequest;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteKlineResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.KType;
import com.tigerbrokers.stock.openapi.client.struct.enums.RightOption;
import io.xtea.AppConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuoteDemo {


    private static TigerHttpClient client = AppConfig.client;

    public static void main(String[] args) {
        new QuoteDemo().kline();
    }

    public void kline() {
        List<String> symbols = new ArrayList<>();
        symbols.add("QQQ");
        QuoteKlineResponse response =
                client.execute(QuoteKlineRequest.newRequest(symbols, KType.day, "2023-01-01", "2023-12-05")
                        .withLimit(1000)
                        .withRight(RightOption.br));
        if (response.isSuccess()) {
            System.out.println(Arrays.toString(response.getKlineItems().toArray()));
        } else {
            System.out.println("response error:" + response.getMessage());
        }
    }
}