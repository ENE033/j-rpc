package RPC.core.handler;

import RPC.core.promise.ResponsePromise;
import RPC.core.protocol.ResponseMessage;
import RPC.core.protocol.ResponseStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage) throws Exception {
        ResponseStatus responseStatus = responseMessage.getResponseStatus();
        Integer seq = responseMessage.getSeq();
        Object result = responseMessage.getResult();
        Promise<Object> promise = ResponsePromise.PROMISE_MAP.get(seq);
        promise.setSuccess(result);
    }
}
