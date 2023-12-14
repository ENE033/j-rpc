package org.ene.RPC.core.proxy;

@FunctionalInterface
public interface ProxyCreateStrategy {
    int JDK_MODE = 0;
    int CGLIB_MODE = 1;


    <T> T doGetProxyObject(Class<T> clazz, ProxyCreatorAdapter proxyCreatorAdapter);
}
