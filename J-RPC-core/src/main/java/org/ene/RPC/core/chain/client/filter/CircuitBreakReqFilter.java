package org.ene.RPC.core.chain.client.filter;

import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.circuitBreak.CircuitBreakRuleHolder;
import org.ene.RPC.core.circuitBreak.CircuitBreaker;
import org.ene.RPC.core.circuitBreak.ExceptionCountCircuitBreaker;
import org.ene.RPC.core.constants.CircuitBreakConstant;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.exception.RPCInvokeException;

import java.lang.reflect.Method;

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
            throw new RPCInvokeException("熔断器开启且没有降级措施，请求被打回");
        }
        return null;
    }
}
