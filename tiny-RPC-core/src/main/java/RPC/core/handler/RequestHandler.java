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

import java.lang.reflect.Method;

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
        String interfaceName = requestMessage.ifN;
        // 方法名
        String methodName = requestMessage.mN;
        // 参数类型
        Class<?>[] argsType = requestMessage.aT;
        // 参数
        Object[] args = requestMessage.a;
        // 消息的序列号
        seq = requestMessage.seq;
        // 超时时间
        int timeOut = requestMessage.to;

        Object obj = serviceController.getService(interfaceName);

        Class<?> clazz = serviceController.getServiceClass(interfaceName);

        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            log.error("服务端没有找到对应的方法", e);
        }

        boolean syncRPC = method.isAnnotationPresent(SyncRPC.class);

        WriteBackStrategy writeBackStrategy = WriteBackMap.get(syncRPC ? WriteBackStrategy.SYNC : WriteBackStrategy.ASYNC);

        Method finalMethod = method;
        Integer finalSeq = seq;
        writeBackStrategy.writeBack(() -> {
            ResponseMessage responseMessage = new ResponseMessage();

            try {
                Object result = finalMethod.invoke(obj, args);
                responseMessage.setS(ResponseStatus.S);
                responseMessage.setR(result);
            } catch (Throwable e) {
                log.error("服务端方法执行异常", e);
                responseMessage.setS(ResponseStatus.F);
                responseMessage.setR("服务端方法执行异常");
//                responseMessage.setE(e.getCause());
//                e.getCause();
//                StackTraceElement[] stackTrace = e.getCause().getStackTrace();
//                for (StackTraceElement stackTraceElement : stackTrace) {
//                    System.out.println(stackTraceElement.toString());
//                }
            } finally {
                responseMessage.setSeq(finalSeq);
                channelHandlerContext.writeAndFlush(responseMessage);
            }
        });

    }
}
