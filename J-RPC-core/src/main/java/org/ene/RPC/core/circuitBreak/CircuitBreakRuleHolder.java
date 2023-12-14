package org.ene.RPC.core.circuitBreak;

import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.constants.CircuitBreakConstant;
import org.ene.RPC.core.exception.JRPCException;

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
                    throw new JRPCException(JRPCException.STRATEGY_NOT_FOUND, "不存在该熔断策略：" + k);
            }
        });
        return ruleHolder.get(circuitBreakRule.strategy());
    }


    static class ExceptionCountCircuitBreakerHolder {
        public static ExceptionCountCircuitBreaker exceptionCountCircuitBreaker = new ExceptionCountCircuitBreaker();
    }

}
