package org.ene.RPC.core.chain.server.filter;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.annotation.SyncRPC;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.server.Invocation;
import org.ene.RPC.core.chain.server.InvocationWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.execute.ExecuteStrategy;
import org.ene.RPC.core.execute.ExecuteStrategyMap;
import org.ene.RPC.core.protocol.ResponseMessage;
import org.ene.RPC.core.protocol.ResponseStatus;

import java.lang.reflect.Method;

/**
 * 执行处理器
 * 根据注解确定同步执行或是异步执行
 */
@FilterComponent(group = CommonConstant.INVOKER, order = 128)
@Slf4j
public class ExecuteFilter implements InvokerFilter {
    @Override
    public Object stream(ChainNode nextNode, InvocationWrapper invocationWrapper) {
        Method method = invocationWrapper.getMethod();
        ExecuteStrategy executeStrategy = ExecuteStrategyMap.get(method.isAnnotationPresent(SyncRPC.class) ? ExecuteStrategy.SYNC : ExecuteStrategy.ASYNC);
        invocationWrapper.setExecuteStrategy(executeStrategy);

        Invocation inv = invocationWrapper.getInv();
        Object obj = invocationWrapper.getObj();
        ChannelHandlerContext channelHandlerContext = invocationWrapper.getChannelHandlerContext();

        Method finalMethod = method;
        Integer finalSeq = inv.getSeq();
        Object[] args = inv.getArgs();
        executeStrategy.writeBack(() -> {
            ResponseMessage responseMessage = new ResponseMessage();
            try {
                Object result = finalMethod.invoke(obj, args);
                responseMessage.setS(ResponseStatus.S);
                responseMessage.setR(result);
            } catch (Throwable e) {
                log.error("服务端方法执行异常", e);
                responseMessage.setS(ResponseStatus.F);
                responseMessage.setR("服务端方法执行异常");
            } finally {
                responseMessage.setSeq(finalSeq);
                channelHandlerContext.writeAndFlush(responseMessage);
            }
        });

        return nextNode.stream(invocationWrapper);
    }
}
