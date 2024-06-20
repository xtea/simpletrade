package io.xtea.waver;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-16 22:47
 */
public interface Analyzer {

    /**
     * Can Buy it.
     *
     * @return
     */
    ExecutionPlan canBuy(String symbol, double currentPrice);

    /**
     * Can sell it.
     *
     * @return
     */
    ExecutionPlan canSell(String symbol, double currentPrice);

}
