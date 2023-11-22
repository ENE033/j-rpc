package org.ene.RPC.core.chain.client;

import io.netty.util.concurrent.Promise;
import lombok.Data;
import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.protocol.ResponseMessage;

import java.lang.reflect.Method;

@Data
public class ReceiverWrapper extends Flow {

    // 消息响应体
    ResponseMessage responseMessage;

    // 是否成功
    boolean success;

    // 结果
    Object result;

    // 方法
    Method method;

    // Promise
    Promise<?> promise;

}
