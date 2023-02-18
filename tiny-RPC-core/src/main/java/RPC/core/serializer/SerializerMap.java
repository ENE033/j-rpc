package RPC.core.serializer;

import RPC.core.serializer.impl.HessianSerializer;
import RPC.core.serializer.impl.JdkSerializer;
import RPC.core.serializer.impl.JsonSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerMap {

    public static final Map<Integer, SerializerStrategy> MAP = new ConcurrentHashMap<>();

    private final static Object lock = new Object();

    public static SerializerStrategy get(int type) {
        SerializerStrategy serializerStrategy;
        if ((serializerStrategy = MAP.get(type)) == null) {
            synchronized (lock) {
                if ((serializerStrategy = MAP.get(type)) == null) {
                    if (type == SerializerStrategy.JDK_SERIALIZER) {
                        MAP.put(type, new JdkSerializer());
                    } else if (type == SerializerStrategy.JSON_SERIALIZER) {
                        MAP.put(type, new JsonSerializer());
                    } else if (type == SerializerStrategy.HESSIAN_SERIALIZER) {
                        MAP.put(type, new HessianSerializer());
                    }
                    serializerStrategy = MAP.get(type);
                }
            }
        }
        return serializerStrategy;
    }

}
