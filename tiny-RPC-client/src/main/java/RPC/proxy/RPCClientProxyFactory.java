package RPC.proxy;

import RPC.client.RPCClient;
import RPC.core.promise.ResponsePromise;
import RPC.core.protocol.RequestMessage;
import RPC.core.util.ServiceProvider;
import RPC.service.TestService;
import RPC.util.SeqCreator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class RPCClientProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                (proxy, method, args) -> {
                    RPCClient rpcClient = new RPCClient();
                    Channel channel = rpcClient.getChannel(new InetSocketAddress("localhost", 4555));
                    RequestMessage requestMessage = new RequestMessage();
                    DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
                    Integer seq = SeqCreator.getSeq();
                    requestMessage.setSeq(seq);
                    ResponsePromise.PROMISE_MAP.put(seq, promise);
                    requestMessage.setServiceName(clazz.getCanonicalName());
                    requestMessage.setMethodName(method.getName());
                    requestMessage.setArgsType(Arrays.stream(args).map(Object::getClass).collect(Collectors.toList()).toArray(new Class<?>[]{}));
                    requestMessage.setArgs(args);
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
}
