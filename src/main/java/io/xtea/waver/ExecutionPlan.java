package io.xtea.waver;

import lombok.Builder;
import lombok.Data;

/**
 * TODO: doc this.
 *
 * @author xtea
 * @date 2023-03-21 22:45
 */
@Data
@Builder
public class ExecutionPlan {

    boolean enable;

    String message;

}
