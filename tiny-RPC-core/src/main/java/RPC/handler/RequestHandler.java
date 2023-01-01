package RPC.handler;

import RPC.protocol.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        String serviceName = requestMessage.serviceName;

        Class<?> clazz = Class.forName(serviceName);

        String methodName = requestMessage.methodName;
    }
}
