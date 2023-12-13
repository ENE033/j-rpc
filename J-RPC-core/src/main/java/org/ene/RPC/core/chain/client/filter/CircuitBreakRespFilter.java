package org.ene.RPC.core.chain.client.filter;

import org.ene.RPC.core.annotation.CircuitBreakRule;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.ReceiverWrapper;
import org.ene.RPC.core.circuitBreak.CircuitBreakRuleHolder;
import org.ene.RPC.core.circuitBreak.CircuitBreaker;
import org.ene.RPC.core.constants.CommonConstant;

import java.lang.reflect.Method;

/**
 * 熔断器(响应)处理器
 * 负责判断返回的结果，由相应的熔断规则判断是否需要开启熔断器
 */
@FilterComponent(group = CommonConstant.RECEIVER, order = 3)
public class CircuitBreakRespFilter implements ReceiverFilter {
    @Override
    public Object filter(ChainNode nextNode, ReceiverWrapper receiverWrapper) {
        Method method = receiverWrapper.getMethod();
        if (!method.isAnnotationPresent(CircuitBreakRule.class)) {
            return nextNode.stream(receiverWrapper);
        }
        // 返回的结果是否有异常
        CircuitBreakRuleHolder.getCircuitBreaker(method.getAnnotation(CircuitBreakRule.class))
                .receiveResult(method.getName(), receiverWrapper.isSuccess());
        return nextNode.stream(receiverWrapper);
    }
}
