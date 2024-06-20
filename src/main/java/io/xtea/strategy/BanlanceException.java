package io.xtea.strategy;

import io.xtea.core.TradeResult;
import lombok.Getter;

/**
 * Balance exception
 *
 * @author xtea
 * @date 2023-03-31 22:10
 */
@Getter
public class BanlanceException extends Exception {

    TradeResult tradeResult;

    public BanlanceException() {
    }

    public BanlanceException(TradeResult tradeResult) {
        this.tradeResult = tradeResult;
    }

    public BanlanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BanlanceException(Throwable cause) {
        super(cause);
    }

    public BanlanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
