package org.ene.RPC.core.chain.server;

import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.protocol.ResponseMessage;
import org.ene.RPC.core.protocol.ResponseStatus;
import org.ene.RPC.core.execute.ExecuteStrategy;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Data
@Slf4j
@ToString
public class InvocationWrapper extends Flow {

    // 从request中获取到的原始信息
    Invocation inv;
    // 超时时间
    int timeOut;
    // 实体
    Object obj;
    // 接口类信息
    Class<?> clazz;
    // 接口方法
    Method method;
    // 执行策略
    ExecuteStrategy executeStrategy;
    // channel上下文
    ChannelHandlerContext channelHandlerContext;

}
