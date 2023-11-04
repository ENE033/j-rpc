package org.ene.RPC.client.proxy;

import org.ene.RPC.client.RPCClient;
import org.ene.RPC.core.ServiceRegistry;
import org.ene.RPC.core.config.ClientRPCConfig;
import org.ene.RPC.core.config.nacos.NacosConfig;
import org.ene.RPC.core.exception.RPCParamException;
import org.ene.RPC.core.promise.ResponsePromise;
import org.ene.RPC.core.protocol.RequestMessage;
import org.ene.RPC.core.proxy.ProxyCreatorAdapter;
import org.ene.RPC.core.proxy.ProxyCreatorMap;
import org.ene.RPC.client.util.SeqUtil;
import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

@Slf4j
public class RPCClientProxyFactory {

    private final ServiceRegistry serviceRegistry;

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
//                        ?? Class<?>[] argsClazz = a == null ? new Class[]{} : Arrays.stream(a).map(Object::getClass).collect(Collectors.toList()).toArray(new Class<?>[]{});

                        Class<?>[] argsClazz = method.getParameterTypes();
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
                        Integer seq = SeqUtil.getSeq();
                        requestMessage.setSeq(seq);
                        requestMessage.setIfN(clazz.getCanonicalName());
                        requestMessage.setMN(method.getName());
                        requestMessage.setAT(argsClazz);
                        requestMessage.setA(args);

                        // 获取一个实例的地址
                        InetSocketAddress serviceAddress = serviceRegistry.getServiceAddress(clazz.getCanonicalName(), requestMessage);
                        Channel channel = rpcClient.getChannel(serviceAddress);
                        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
                        ResponsePromise.PROMISE_MAP.put(seq, promise);
                        channel.writeAndFlush(requestMessage);

                        try {
                            promise.await();
                            ResponsePromise.PROMISE_MAP.remove(seq);
                            SeqUtil.removeSeq(seq);
                            if (promise.isSuccess()) {
                                Class<?> returnType = method.getReturnType();
                                Object result = promise.getNow();
                                if (void.class.equals(returnType) || result == null) {
                                    return null;
                                }
                                Class<?> resultClass = result.getClass();
                                if (resultClass.isAssignableFrom(returnType)) {
                                    return result;
                                } else {
                                    throw new RPCParamException("返回类型与预期不匹配");
                                }
                            }
                            throw promise.cause();
                        } catch (Throwable e) {
                            throw new RuntimeException("rpc远程调用失败", e);
                        }
                    }
                });

    }
}


