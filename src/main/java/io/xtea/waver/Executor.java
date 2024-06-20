package io.xtea.waver;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-16 22:50
 */
public interface Executor {


    void buy(String requestJson);


    void sell(String requestJson);


    boolean isHold();
}
