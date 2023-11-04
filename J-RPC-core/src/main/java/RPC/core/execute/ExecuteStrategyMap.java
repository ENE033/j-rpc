package RPC.core.execute;

import RPC.core.execute.impl.ExecuteInAsync;
import RPC.core.execute.impl.ExecuteInSync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecuteStrategyMap {
    public static final Map<Integer, ExecuteStrategy> MAP = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    public static ExecuteStrategy get(int type) {
        ExecuteStrategy executeStrategy;
        if ((executeStrategy = MAP.get(type)) == null) {
            synchronized (lock) {
                if ((executeStrategy = MAP.get(type)) == null) {
                    if (type == 0) {
                        MAP.put(type, new ExecuteInSync());
                    } else if (type == 1) {
                        MAP.put(type, new ExecuteInAsync());
                    }
                    executeStrategy = MAP.get(type);
                }
            }
        }
        return executeStrategy;
    }
}
