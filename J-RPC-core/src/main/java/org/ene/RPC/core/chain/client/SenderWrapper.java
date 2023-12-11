package org.ene.RPC.core.chain.client;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.protocol.RequestMessage;

import java.lang.reflect.Method;

@Data
@Accessors(chain = true)
public class SenderWrapper extends Flow {
    // 默认实体
    Object entity;
    // 类对象
    Class<?> clazz;
    // 方法
    Method method;
    // 参数列表
    Object[] args;
    // 参数类型列表
    Class<?>[] argsClazz;
    // 请求消息体
    RequestMessage requestMessage;
    // 通道
    Channel channel;
    // promise
    DefaultPromise<?> promise;
}
