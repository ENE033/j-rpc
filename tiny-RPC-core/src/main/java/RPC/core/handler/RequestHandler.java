package RPC.core.handler;

import RPC.core.protocol.RequestMessage;
import RPC.core.protocol.ResponseMessage;
import RPC.core.protocol.ResponseStatus;
import RPC.core.util.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
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

        Object obj = ServiceProvider.getService(serviceName);

        Class<?> clazz = ServiceProvider.getClass(serviceName);

        Method method = clazz.getDeclaredMethod(methodName, argsType);

        Object result = method.invoke(obj, args);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setResult(result);
        responseMessage.setResponseStatus(ResponseStatus.SUCCESS);
        responseMessage.setSeq(seq);
        channelHandlerContext.writeAndFlush(responseMessage);

    }
}
