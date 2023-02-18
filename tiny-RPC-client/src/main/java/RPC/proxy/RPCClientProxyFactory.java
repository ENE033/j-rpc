package RPC.proxy;

import RPC.client.RPCClient;
import RPC.core.ServiceRegistry;
import RPC.core.config.ClientRPCConfig;
import RPC.core.config.nacos.NacosConfig;
import RPC.core.promise.ResponsePromise;
import RPC.core.protocol.RequestMessage;
import RPC.core.proxy.ProxyCreateStrategy;
import RPC.core.proxy.ProxyCreatorAdapter;
import RPC.core.proxy.ProxyCreatorMap;
import RPC.core.proxy.impl.CglibProxyCreator;
import RPC.core.proxy.impl.JdkProxyCreator;
import RPC.util.SeqCreator;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class RPCClientProxyFactory {

    private final ServiceRegistry serviceRegistry;

//    private final ClientRPCConfig clientRPCConfig;

    private final RPCClient rpcClient;

    private final ClientRPCConfig clientRPCConfig;

    public RPCClientProxyFactory(ClientRPCConfig clientRPCConfig) {
        this.clientRPCConfig = clientRPCConfig;
        this.clientRPCConfig.init();
        serviceRegistry = new ServiceRegistry(this.clientRPCConfig);
        rpcClient = new RPCClient(this.clientRPCConfig);
    }


    public <T> T getProxy(Class<T> clazz) {
        return ProxyCreatorMap.get(clientRPCConfig.getConfigAsInt(NacosConfig.PROXY_MODE))
                .doGetProxyObject(clazz, new ProxyCreatorAdapter() {
                    private final Object entity = new Object();

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        Class<?>[] argsClazz = args == null ? new Class[]{} : Arrays.stream(args).map(Object::getClass).collect(Collectors.toList()).toArray(new Class<?>[]{});

                        try {
                            clazz.getDeclaredMethod(method.getName(), argsClazz);
                        } catch (NoSuchMethodException e) {
                            Object backed = null;
                            try {
                                backed = method.invoke(entity, args);
                            } catch (IllegalAccessException | InvocationTargetException ex) {
                                ex.printStackTrace();
                            }
                            return backed;
                        }
                        RequestMessage requestMessage = new RequestMessage();
                        Integer seq = SeqCreator.getSeq();
                        requestMessage.setSeq(seq);
                        requestMessage.setInterfaceName(clazz.getCanonicalName());
                        requestMessage.setMethodName(method.getName());
                        requestMessage.setArgsType(argsClazz);
                        requestMessage.setArgs(args);

                        // 获取一个实例的地址
                        InetSocketAddress serviceAddress = serviceRegistry.getServiceAddress(clazz.getCanonicalName(), requestMessage);
                        Channel channel = rpcClient.getChannel(serviceAddress);
                        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
                        ResponsePromise.PROMISE_MAP.put(seq, promise);
                        channel.writeAndFlush(requestMessage);

                        try {
                            promise.await();
                            ResponsePromise.PROMISE_MAP.remove(seq);
                            if (promise.isSuccess()) {
                                Object result = promise.getNow();
                                Class<?> returnType = method.getReturnType();
                                Class<?> resultClass = result.getClass();
                                if (resultClass.isAssignableFrom(returnType)) {
                                    return result;
                                } else {
                                    throw new RuntimeException("返回类型与预期不匹配");
                                }
                            }
                        } catch (Exception e) {
//                            e.printStackTrace();
                            throw new RuntimeException("rpc远程调用失败", e);
                        }
                        return null;
                    }
                });

    }
}


