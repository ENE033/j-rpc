package org.ene.RPC.core.proxy.impl;

import org.ene.RPC.core.proxy.ProxyCreatorAdapter;
import org.ene.RPC.core.proxy.ProxyCreateStrategy;

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
