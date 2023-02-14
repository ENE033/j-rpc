package RPC.core.proxy.impl;

import RPC.core.proxy.ProxyCreatorAdapter;
import RPC.core.proxy.ProxyCreateStrategy;

import java.lang.reflect.Proxy;


public class JdkProxyCreator implements ProxyCreateStrategy {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T doGetProxyObject(Class<T> clazz, ProxyCreatorAdapter proxyCreatorAdapter) {
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{clazz},
                proxyCreatorAdapter);
    }
}
