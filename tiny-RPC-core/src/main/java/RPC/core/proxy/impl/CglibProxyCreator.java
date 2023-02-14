package RPC.core.proxy.impl;

import RPC.core.proxy.ProxyCreatorAdapter;
import RPC.core.proxy.ProxyCreateStrategy;
import org.springframework.cglib.proxy.Enhancer;

public class CglibProxyCreator implements ProxyCreateStrategy {
    @Override
    @SuppressWarnings("unchecked")
    public <T> T doGetProxyObject(Class<T> clazz, ProxyCreatorAdapter proxyCreatorAdapter) {
        return (T) Enhancer.create(clazz, proxyCreatorAdapter);
    }
}
