package org.ene.RPC.core.circuitBreak;

import org.ene.RPC.core.annotation.CircuitBreakRule;

import java.lang.reflect.Method;

public interface CircuitBreaker {

    boolean tryPass(CircuitBreakRule circuitBreakRule, Method method);

    void receiveResult(CircuitBreakRule circuitBreakRule, Method methodName, boolean success);
}
