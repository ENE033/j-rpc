package RPC.core.handler;

import RPC.core.config.ServerRPCConfig;
import RPC.core.config.nacos.NacosConfig;
import RPC.core.writeBack.WriteBackMap;
import RPC.core.writeBack.WriteBackStrategy;
import RPC.core.protocol.RequestMessage;
import RPC.core.protocol.ResponseMessage;
import RPC.core.protocol.ResponseStatus;
import RPC.core.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private final ServiceProvider serviceProvider;

    private final ServerRPCConfig serverRpcConfig;

    public RequestHandler(ServiceProvider serviceProvider, ServerRPCConfig serverRpcConfig) {
        this.serviceProvider = serviceProvider;
        this.serverRpcConfig = serverRpcConfig;
    }

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

        Object obj = serviceProvider.getService(serviceName);

        Class<?> clazz = serviceProvider.getClass(serviceName);

        Method method = clazz.getDeclaredMethod(methodName, argsType);

        Object result = method.invoke(obj, args);

        WriteBackStrategy writeBackStrategy = WriteBackMap.get(serverRpcConfig.getConfigAsInt(NacosConfig.WRITEBACK_TYPE));
        // 仿照redis的多线程模式，只在写回数据的时候使用多线程
        writeBackStrategy.writeBack(() -> {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setResult(result);
            responseMessage.setResponseStatus(ResponseStatus.SUCCESS);
            responseMessage.setSeq(seq);
            channelHandlerContext.writeAndFlush(responseMessage);
        });

    }
}
