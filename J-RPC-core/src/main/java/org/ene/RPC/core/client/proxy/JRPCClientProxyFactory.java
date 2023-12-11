package org.ene.RPC.core.client.proxy;

import org.ene.RPC.core.client.JRPCClient;
import org.ene.RPC.core.ServiceRegistry;
import org.ene.RPC.core.chain.client.SenderFilterChain;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.config.ClientRPCConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class JRPCClientProxyFactory {

    private final ServiceRegistry serviceRegistry;

    private final JRPCClient jrpcClient;

    private final ClientRPCConfig clientRPCConfig;

    private final SenderFilterChain senderFilterChain;

    public JRPCClientProxyFactory(ClientRPCConfig clientRPCConfig) {
        this.clientRPCConfig = clientRPCConfig;
        this.clientRPCConfig.init();
        serviceRegistry = new ServiceRegistry(this.clientRPCConfig);
        jrpcClient = new JRPCClient(this.clientRPCConfig);
        senderFilterChain = new SenderFilterChain(serviceRegistry, jrpcClient);
    }

    private SenderWrapper builderSender(Class<?> clazz, Method method, Object[] args) {
        SenderWrapper senderWrapper = new SenderWrapper();
        senderWrapper.setClazz(clazz)
                .setMethod(method)
                .setArgs(args)
                .setArgsClazz(method.getParameterTypes());
        return senderWrapper;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                (Object proxy, Method method, Object[] args)
                        -> senderFilterChain.handler(builderSender(clazz, method, args)));

//        return ProxyCreatorMap.get(clientRPCConfig.getConfigAsInt(NacosConfig.PROXY_MODE))
//                .doGetProxyObject(clazz, new ProxyCreatorAdapter() {
//                    @Override
//                    public Object invoke(Object proxy, Method method, Object[] args) {
//                        return senderFilterChain.handler(builderSender(clazz, proxy, method, args));
//                    }
//                });

    }
}


