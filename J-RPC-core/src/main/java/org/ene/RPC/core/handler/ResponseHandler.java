package org.ene.RPC.core.handler;

import org.ene.RPC.core.chain.client.ReceiverFilterChain;
import org.ene.RPC.core.chain.client.ReceiverWrapper;
import org.ene.RPC.core.protocol.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<ResponseMessage> {

    ReceiverFilterChain receiverFilterChain = new ReceiverFilterChain();

    private ReceiverWrapper buildReceiverWrapper(ResponseMessage responseMessage) {
        ReceiverWrapper receiverWrapper = new ReceiverWrapper();
        receiverWrapper.setResponseMessage(responseMessage);
        return receiverWrapper;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage) throws Exception {
        receiverFilterChain.handler(buildReceiverWrapper(responseMessage));
    }
}
