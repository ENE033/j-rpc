package RPC.proxy;

import RPC.client.RPCClient;
import RPC.core.ServiceRegistry;
import RPC.core.promise.ResponsePromise;
import RPC.core.protocol.RequestMessage;
import RPC.util.SeqCreator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class RPCClientProxyFactory {

    private static final RPCClient rpcClient = new RPCClient();

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
//                    RPCClient rpcClient = new RPCClient();
                    RequestMessage requestMessage = new RequestMessage();
                    Integer seq = SeqCreator.getSeq();
                    requestMessage.setSeq(seq);
                    requestMessage.setServiceName(clazz.getCanonicalName());
                    requestMessage.setMethodName(method.getName());
                    if (args != null && args.length != 0) {
                        requestMessage.setArgsType(Arrays.stream(args).map(Object::getClass).collect(Collectors.toList()).toArray(new Class<?>[]{}));
                        requestMessage.setArgs(args);
                    }

                    // 获取一个实例的地址
                    InetSocketAddress serviceAddress = ServiceRegistry.getServiceAddress(clazz.getCanonicalName(), requestMessage);
                    Channel channel = rpcClient.getChannel(serviceAddress);
                    DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
                    ResponsePromise.PROMISE_MAP.put(seq, promise);
                    channel.writeAndFlush(requestMessage);

                    try {
                        promise.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (promise.isSuccess()) {
                        return promise.getNow();
                    }
                    return null;
                });
    }

    public static void chooseLoadBalance(Integer mode) {
        ServiceRegistry.chooseLoadBalance(mode);
    }

}
