package org.ene.RPC.core.circuitBreak;

import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.constants.CircuitBreakConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakRuleHolder {
    private static final Map<String, CircuitBreaker> ruleHolder = new ConcurrentHashMap<>();

    static {
        ruleHolder.put(CircuitBreakConstant.exceptionCount, new ExceptionCountCircuitBreaker());
    }

    public static CircuitBreaker getCircuitBreaker(String circuitLogo) {
        return ruleHolder.get(circuitLogo);
    }

    public static CircuitBreaker getCircuitBreaker(CircuitBreakRule circuitBreakRule) {
        return ruleHolder.get(circuitBreakRule.strategy());
    }

}
