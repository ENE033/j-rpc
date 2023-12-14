package org.ene.RPC.core.chain.client.filter;

import io.netty.util.concurrent.DefaultPromise;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.exception.JRPCException;

import java.lang.reflect.Method;

/**
 * 响应结果处理器
 * 负责等待Promise结果
 * 成功则返回，不成功则抛出异常
 */
@FilterComponent(group = CommonConstant.SENDER, order = 10)
public class ResultFilter implements SenderFilter {
    @Override
    public Object filter(ChainNode nextNode, SenderWrapper senderWrapper) {
        try {
            DefaultPromise<?> promise = senderWrapper.getPromise();
            Method method = senderWrapper.getMethod();

            promise.await();

            if (promise.isSuccess()) {
                Class<?> returnType = method.getReturnType();
                Object result = promise.getNow();
                if (void.class.equals(returnType) || result == null) {
                    return null;
                }
                Class<?> resultClass = result.getClass();
                if (resultClass.isAssignableFrom(returnType)) {
                    nextNode.stream(senderWrapper);
                    return result;
                } else {
                    throw new JRPCException(JRPCException.VALIDATION_EXCEPTION, "返回类型与预期不匹配");
                }
            }
            throw promise.cause();
        } catch (Throwable e) {
            throw new JRPCException(JRPCException.UNKNOWN_EXCEPTION, "rpc远程调用失败", e);
        }
    }
}
