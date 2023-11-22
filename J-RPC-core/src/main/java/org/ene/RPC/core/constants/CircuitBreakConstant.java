package org.ene.RPC.core.constants;

public interface CircuitBreakConstant {

    String exceptionCount = "count";

    String exceptionRate = "rate";

    int CLOSE = 1;
    int HALF_OPEN = 2;
    int OPEN = 3;

}
