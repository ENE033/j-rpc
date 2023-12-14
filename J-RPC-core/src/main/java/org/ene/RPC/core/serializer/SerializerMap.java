package org.ene.RPC.core.serializer;

import org.ene.RPC.core.serializer.impl.HessianSerializer;
import org.ene.RPC.core.serializer.impl.JdkSerializer;
import org.ene.RPC.core.serializer.impl.JsonSerializer;
import org.ene.RPC.core.serializer.impl.KryoSerializer;

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
                    switch (type) {
                        case SerializerStrategy.JDK:
                            MAP.put(type, new JdkSerializer());
                        case SerializerStrategy.JSON:
                            MAP.put(type, new JsonSerializer());
                        case SerializerStrategy.HESSIAN:
                            MAP.put(type, new HessianSerializer());
                        case SerializerStrategy.KRYO:
                            MAP.put(type, new KryoSerializer());
                    }
                    serializerStrategy = MAP.get(type);
                }
            }
        }
        return serializerStrategy;
    }

}
