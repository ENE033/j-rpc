package org.ene.RPC.core.loadBanlance;

import org.ene.RPC.core.loadBanlance.impl.ConsistHashLoadBalance;
import org.ene.RPC.core.loadBanlance.impl.RandomByWeightLoadBalance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalanceMap {
    public static final Map<Integer, LoadBalanceStrategy> MAP = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    public static LoadBalanceStrategy get(int type) {
        LoadBalanceStrategy loadBalanceStrategy;
        if ((loadBalanceStrategy = MAP.get(type)) == null) {
            synchronized (lock) {
                if ((loadBalanceStrategy = MAP.get(type)) == null) {
                    if (type == 0) {
                        MAP.put(type, new RandomByWeightLoadBalance());
                    } else if (type == 1) {
                        MAP.put(type, new ConsistHashLoadBalance());
                    }
                    loadBalanceStrategy = MAP.get(type);
                }
            }
        }
        return loadBalanceStrategy;
    }
}
