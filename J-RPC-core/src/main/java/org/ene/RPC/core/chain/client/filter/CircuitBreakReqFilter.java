package org.ene.RPC.core.chain.client.filter;

import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.circuitBreak.CircuitBreakRuleHolder;
import org.ene.RPC.core.circuitBreak.CircuitBreaker;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.exception.JRPCException;

import java.lang.reflect.Method;

/**
 * 熔断器(请求)处理器
 * 如果被熔断，那么执行降级处理，没有降级处理则抛出异常
 * 如果没有被熔断，那么正常通过
 */
@FilterComponent(group = CommonConstant.SENDER, order = 7)
@Slf4j
public class CircuitBreakReqFilter implements SenderFilter {

    @Override
    public Object filter(ChainNode nextNode, SenderWrapper senderWrapper) {
        Method method = senderWrapper.getMethod();
        if (!method.isAnnotationPresent(CircuitBreakRule.class)) {
            return nextNode.stream(senderWrapper);
        }
        CircuitBreakRule circuitBreakRule = method.getAnnotation(CircuitBreakRule.class);
        CircuitBreaker circuitBreaker = CircuitBreakRuleHolder.getCircuitBreaker(circuitBreakRule);

        if (!circuitBreaker.tryPass(circuitBreakRule, method)) {
            log.warn("熔断器开启且没有降级措施，请求被打回");
            throw new JRPCException(JRPCException.CIRCUIT_BREAK_EXCEPTION, "熔断器开启且没有降级措施，请求被打回");
        }
        return nextNode.stream(senderWrapper);
    }
}
