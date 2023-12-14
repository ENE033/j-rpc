package org.ene.RPC.core.circuitBreak;

import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.constants.CircuitBreakConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakRuleHolder {
    private static final Map<String, CircuitBreaker> ruleHolder = new ConcurrentHashMap<>();

    public static CircuitBreaker getCircuitBreaker(String circuitLogo) {
        return ruleHolder.get(circuitLogo);
    }

    public static CircuitBreaker getCircuitBreaker(CircuitBreakRule circuitBreakRule) {
        ruleHolder.computeIfAbsent(circuitBreakRule.strategy(), k -> {
            switch (k) {
                case CircuitBreakConstant.EXCEPTION_COUNT:
                    return ExceptionCountCircuitBreakerHolder.exceptionCountCircuitBreaker;
                default:
                    throw new RuntimeException("不存在该熔断策略");
            }
        });
        return ruleHolder.get(circuitBreakRule.strategy());
    }


    static class ExceptionCountCircuitBreakerHolder {
        public static ExceptionCountCircuitBreaker exceptionCountCircuitBreaker = new ExceptionCountCircuitBreaker();
    }

}
