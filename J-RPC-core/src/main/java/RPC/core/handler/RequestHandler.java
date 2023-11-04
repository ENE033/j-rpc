package RPC.core.handler;

import RPC.core.chain.Invocation;
import RPC.core.chain.InvocationWrapper;
import RPC.core.chain.ServerFilterChain;
import RPC.core.config.ServerRPCConfig;
import RPC.core.protocol.RequestMessage;
import RPC.core.ServiceController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private final ServiceController serviceController;

    private final ServerRPCConfig serverRpcConfig;

    private final ServerFilterChain serverFilterChain;

    public RequestHandler(ServiceController serviceController, ServerRPCConfig serverRpcConfig) {
        this.serviceController = serviceController;
        this.serverRpcConfig = serverRpcConfig;
        this.serverFilterChain = new ServerFilterChain();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        InvocationWrapper invocationWrapper = wrapperInvocation(buildInvocation(requestMessage), channelHandlerContext);

        serverFilterChain.handler(invocationWrapper);

        invocationWrapper.execute();
    }

    private Invocation buildInvocation(RequestMessage requestMessage) {
        Invocation invocation = new Invocation();
        invocation.setSeq(requestMessage.getSeq());
        invocation.setArgs(requestMessage.getA());
        invocation.setArgsType(requestMessage.getAT());
        invocation.setMethodName(requestMessage.getMN());
        invocation.setInterfaceName(requestMessage.getIfN());
        return invocation;
    }

    private InvocationWrapper wrapperInvocation(Invocation invocation, ChannelHandlerContext channelHandlerContext) {
        InvocationWrapper invocationWrapper = new InvocationWrapper();
        invocationWrapper.setInv(invocation);
        invocationWrapper.setObj(serviceController.getService(invocation.getInterfaceName()));
        Class<?> clazz = serviceController.getServiceClass(invocation.getInterfaceName());
        invocationWrapper.setClazz(clazz);
        Method method = serviceController.getMethod(clazz, invocation.getMethodName(), invocation.getArgsType());
        invocationWrapper.setMethod(method);
        invocationWrapper.setChannelHandlerContext(channelHandlerContext);
        return invocationWrapper;
    }

}