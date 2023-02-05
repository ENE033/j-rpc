package RPC.core.config.client.loadBanlance;

import RPC.core.config.client.loadBanlance.impl.ConsistHashLoadBalance;
import RPC.core.config.client.loadBanlance.impl.RandomByWeightLoadBalance;
import RPC.core.config.server.writeBack.WriteBackStrategy;
import RPC.core.config.server.writeBack.impl.WriteBackInAsync;
import RPC.core.config.server.writeBack.impl.WriteBackInSync;

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
