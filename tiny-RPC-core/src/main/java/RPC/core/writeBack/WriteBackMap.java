package RPC.core.writeBack;

import RPC.core.writeBack.impl.WriteBackInAsync;
import RPC.core.writeBack.impl.WriteBackInSync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WriteBackMap {
    public static final Map<Integer, WriteBackStrategy> MAP = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    public static WriteBackStrategy get(int type) {
        WriteBackStrategy writeBackStrategy;
        if ((writeBackStrategy = MAP.get(type)) == null) {
            synchronized (lock) {
                if ((writeBackStrategy = MAP.get(type)) == null) {
                    if (type == 0) {
                        MAP.put(type, new WriteBackInSync());
                    } else if (type == 1) {
                        MAP.put(type, new WriteBackInAsync());
                    }
                    writeBackStrategy = MAP.get(type);
                }
            }
        }
        return writeBackStrategy;
    }
}
