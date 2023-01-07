package RPC.core.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerMap {

    public static final Map<Integer, Serializer> MAP;

    static {
        MAP = new ConcurrentHashMap<>();
    }

    public static Serializer get(int type) {
        Serializer serializer = MAP.get(type);
        if (serializer == null) {
            if (type == 1) {
                MAP.put(type, new JDKSerializer());
            }
            serializer = MAP.get(type);
        }
        return serializer;
    }


}
