package RPC.core.handler;

import RPC.core.annotation.SyncRPC;
import RPC.core.config.ServerRPCConfig;
import RPC.core.writeBack.WriteBackMap;
import RPC.core.writeBack.WriteBackStrategy;
import RPC.core.protocol.RequestMessage;
import RPC.core.protocol.ResponseMessage;
import RPC.core.protocol.ResponseStatus;
import RPC.core.ServiceController;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RequestMessage> {

    private final ServiceController serviceController;

    private final ServerRPCConfig serverRpcConfig;

//    private static final EventExecutorGroup EXECUTOR_GROUP = new DefaultEventExecutorGroup(16);

//    public static final ThreadPoolExecutor EXECUTOR_GROUP = new ThreadPoolExecutor(
//            8,
//            16,
//            5,
//            TimeUnit.SECONDS,
//            new ArrayBlockingQueue<>(100),
//            new ThreadPoolExecutor.AbortPolicy());

    public RequestHandler(ServiceController serviceController, ServerRPCConfig serverRpcConfig) {
        this.serviceController = serviceController;
        this.serverRpcConfig = serverRpcConfig;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        Integer seq = null;

        // 服务名
        String interfaceName = requestMessage.interfaceName;
        // 方法名
        String methodName = requestMessage.methodName;
        // 参数类型
        Class<?>[] argsType = requestMessage.argsType;
        // 参数
        Object[] args = requestMessage.args;
        // 消息的序列号
        seq = requestMessage.seq;
        // 超时时间
        Integer timeOut = requestMessage.timeOut;

        Object obj = serviceController.getService(interfaceName);

        Class<?> clazz = serviceController.getServiceClass(interfaceName);

        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        boolean syncRPC = method.isAnnotationPresent(SyncRPC.class);

        WriteBackStrategy writeBackStrategy = WriteBackMap.get(syncRPC ? WriteBackStrategy.SYNC : WriteBackStrategy.ASYNC);

        Method finalMethod = method;
        Integer finalSeq = seq;
        writeBackStrategy.writeBack(() -> {
            ResponseMessage responseMessage = new ResponseMessage();

            Object result = null;
            try {
                result = finalMethod.invoke(obj, args);
                responseMessage.setResult(result);
                responseMessage.setResponseStatus(ResponseStatus.SUCCESS);
            } catch (Throwable e) {
                log.error("方法执行异常", e);
                responseMessage.setResponseStatus(ResponseStatus.FAIL);
            } finally {
                responseMessage.setSeq(finalSeq);
                channelHandlerContext.writeAndFlush(responseMessage);
            }
        });

    }
}
