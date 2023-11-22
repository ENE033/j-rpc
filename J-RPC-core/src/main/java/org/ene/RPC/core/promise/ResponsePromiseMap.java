package org.ene.RPC.core.promise;

import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponsePromiseMap {
    private static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

    public static Promise<Object> getAndRemove(int seq) {
        return PROMISE_MAP.remove(seq);
    }

    public static void put(int seq, Promise<Object> promise) {
        PROMISE_MAP.put(seq, promise);
    }

}
