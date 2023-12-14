package org.ene.RPC.core.constants;

public interface CircuitBreakConstant {

    String EXCEPTION_COUNT = "count";

    String EXCEPTION_RATE = "rate";

    int CLOSE = 1;
    int HALF_OPEN = 2;
    int OPEN = 3;

}
