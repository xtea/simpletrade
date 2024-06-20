package io.xtea.usstock;

import com.alibaba.fastjson.JSONObject;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.model.ContractModel;
import com.tigerbrokers.stock.openapi.client.https.domain.trade.item.PrimeAssetItem;
import com.tigerbrokers.stock.openapi.client.https.domain.trade.item.TradeOrder;
import com.tigerbrokers.stock.openapi.client.https.request.contract.ContractRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteRealTimeQuoteRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.EstimateTradableQuantityRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.PrimeAssetRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.TradeOrderRequest;
import com.tigerbrokers.stock.openapi.client.https.response.contract.ContractResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteRealTimeQuoteResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.EstimateTradableQuantityResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.PrimeAssetResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.TradeOrderResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.ActionType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Category;
import com.tigerbrokers.stock.openapi.client.struct.enums.Currency;
import com.tigerbrokers.stock.openapi.client.struct.enums.OrderType;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import io.xtea.AppConfig;
import io.xtea.core.Order;
import io.xtea.core.TradeExecutor;
import io.xtea.core.TradeResult;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * https://quant.itigerup.com/openapi/zh/java/operation/trade/placeOrder.html#%E6%9E%84%E5%BB%BA%E5%90%88%E7%BA%A6%E5%AF%B9%E8%B1%A1
 *
 * @author xtea
 * @date 2023-12-20 22:36
 */
@Service("tigerUsStock")
@Slf4j
public class TigerTradeExecutorImpl implements TradeExecutor {

    public static final String CODE = "TSLA";
    private static TigerHttpClient client = AppConfig.client;


    @Override
    public Order buy(double price, long time) {
        // get contract(use default account)
        ContractRequest contractRequest = ContractRequest.newRequest(new ContractModel(CODE));
        ContractResponse contractResponse = client.execute(contractRequest);
        ContractItem contract = contractResponse.getItem();

        Integer tradableQuantity = getTradableQuantity(ActionType.BUY, CODE);
        if (tradableQuantity > 0) {
            TradeOrderRequest request = TradeOrderRequest.buildMarketOrder(contract, ActionType.BUY,
                    tradableQuantity);
            TradeOrderResponse response = client.execute(request);
            System.out.println(JSONObject.toJSONString(response));
            TradeOrder tradeOrder = response.getItem().getOrders().get(0);
            return Order.builder().price(tradeOrder.getLatestPrice()).time(tradeOrder.getLatestTime()).build();
        }
        System.out.println("Ignore buy order");
        return Order.builder().build();
    }

    public Integer getTradableQuantity(ActionType actionType, String code) {
        /**
         * STK:股票/FUT:期货/OPT:期权/WAR:窝轮/IOPT:牛熊证, 期货暂不支持。
         */
        EstimateTradableQuantityRequest request = EstimateTradableQuantityRequest.buildRequest(
                SecType.STK, code, actionType, OrderType.MKT, null, null);
        EstimateTradableQuantityResponse response = client.execute(request);

        if (response.isSuccess()) {
            System.out.println(JSONObject.toJSONString(response));
            if (actionType == ActionType.BUY) {
                return response.getTradableQuantityItem().getTradableQuantity().intValue();
            } else {
                return response.getTradableQuantityItem().getPositionQuantity().intValue();
            }
        } else {
            throw new IllegalStateException("fail get getTradableQuantity." + JSONObject.toJSONString(response));
        }
    }


    @Override
    public TradeResult sell(Order sellOrder) throws Exception {
        // get contract(use default account)
        ContractRequest contractRequest = ContractRequest.newRequest(new ContractModel(CODE));
        ContractResponse contractResponse = client.execute(contractRequest);
        ContractItem contract = contractResponse.getItem();

        Integer tradableQuantity = getTradableQuantity(ActionType.SELL, CODE);
        if (tradableQuantity > 0) {
            TradeOrderRequest request = TradeOrderRequest.buildMarketOrder(contract, ActionType.SELL,
                    tradableQuantity);
            TradeOrderResponse response = client.execute(request);
            System.out.println(JSONObject.toJSONString(response));
            TradeOrder tradeOrder = response.getItem().getOrders().get(0);
            return TradeResult.builder()
                    .sell(Order.builder().price(tradeOrder.getLatestPrice()).time(tradeOrder.getLatestTime()).build())
                    .build();
        }
        System.out.println("Ignore sell order");
        return TradeResult.builder().build();
    }

    @Override
    public BigDecimal balance() {
        PrimeAssetRequest assetRequest = PrimeAssetRequest.buildPrimeAssetRequest("20230317003303912", Currency.USD);
        PrimeAssetResponse primeAssetResponse = client.execute(assetRequest);
        PrimeAssetItem.Segment segment = primeAssetResponse.getSegment(Category.S);
        System.out.println("segment: " + JSONObject.toJSONString(segment));
        if (segment != null) {
            PrimeAssetItem.CurrencyAssets assetByCurrency = segment.getAssetByCurrency(Currency.USD);
            System.out.println("assetByCurrency: " + JSONObject.toJSONString(assetByCurrency));
            return BigDecimal.valueOf(assetByCurrency.getCashBalance());
        }
        throw new IllegalStateException("Unable get balance from tiger");
    }

    @Override
    public boolean isHold() {
        Integer tradableQuantity = getTradableQuantity(ActionType.SELL, CODE);
        return tradableQuantity > 0;
    }

    @Override
    public double getMarketPrice() {
        QuoteRealTimeQuoteResponse response = client.execute(QuoteRealTimeQuoteRequest.newRequest(Arrays.asList(CODE),
                true));
        if (response.isSuccess()) {
            System.out.println(Arrays.toString(response.getRealTimeQuoteItems().toArray()));
        } else {
            System.out.println("response error:" + response.getMessage());
        }
        return 0;
    }
}
