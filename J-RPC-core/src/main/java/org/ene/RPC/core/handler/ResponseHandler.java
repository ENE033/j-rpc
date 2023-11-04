package org.ene.RPC.core.handler;

import org.ene.RPC.core.exception.RPCInvokeException;
import org.ene.RPC.core.promise.ResponsePromise;
import org.ene.RPC.core.protocol.ResponseMessage;
import org.ene.RPC.core.protocol.ResponseStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage) throws Exception {
        ResponseStatus responseStatus = responseMessage.getS();
        int seq = responseMessage.getSeq();
        Promise<Object> promise = ResponsePromise.PROMISE_MAP.get(seq);
        switch (responseStatus) {
            case S:
                Object result = responseMessage.getR();
                promise.setSuccess(result);
                break;
            case F:
                String errMsg = (String) responseMessage.getR();
                promise.setFailure(new RPCInvokeException(errMsg));
                break;
        }


    }
}
