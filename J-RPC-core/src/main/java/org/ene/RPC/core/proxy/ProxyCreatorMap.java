package org.ene.RPC.core.proxy;

import org.ene.RPC.core.proxy.impl.CglibProxyCreator;
import org.ene.RPC.core.proxy.impl.JdkProxyCreator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyCreatorMap {
    public final static Map<Integer, ProxyCreateStrategy> MAP = new ConcurrentHashMap<>();

    public final static Object lock = new Object();

    public static ProxyCreateStrategy get(int type) {
        ProxyCreateStrategy proxyCreateStrategy;
        if ((proxyCreateStrategy = MAP.get(type)) == null) {
            synchronized (lock) {
                if ((proxyCreateStrategy = MAP.get(type)) == null) {
                    if (type == ProxyCreateStrategy.JDK_MODE) {
                        MAP.put(type, new JdkProxyCreator());
                    } else if (type == ProxyCreateStrategy.CGLIB_MODE) {
                        MAP.put(type, new CglibProxyCreator());
                    }
                    proxyCreateStrategy = MAP.get(type);
                }
            }
        }
        return proxyCreateStrategy;
    }

}
