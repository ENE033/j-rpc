package RPC.core.handler;

import RPC.core.protocol.RequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class RequestHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        // 服务名
        String serviceName = requestMessage.serviceName;
        // 方法名
        String methodName = requestMessage.methodName;
        // 参数类型
        Class<?>[] argsType = requestMessage.argsType;
        // 参数
        Object[] args = requestMessage.args;
        // 消息的序列号
        Integer seq = requestMessage.seq;
        // 超时时间
        Integer timeOut = requestMessage.timeOut;


        Class<?> clazz = Class.forName(serviceName);

        Method method = clazz.getDeclaredMethod(methodName);


    }
}
