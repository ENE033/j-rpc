package RPC.core.promise;

import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResponsePromise {
    public static final Map<Integer, Promise<Object>> PROMISE_MAP = new ConcurrentHashMap<>();

}
