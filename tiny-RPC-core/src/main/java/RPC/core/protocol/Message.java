package RPC.core.protocol;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class Message {
    public Integer type;
    public Integer seq;

    public static final Integer REQUEST = 1;
    public static final Integer RESPONSE = 2;

    public static final Map<Integer, Class<?>> mapper;

    static {
        mapper = new ConcurrentHashMap<>();
        mapper.put(REQUEST, RequestMessage.class);
        mapper.put(RESPONSE, ResponseMessage.class);
    }

    public static Class<?> getClassType(int type) {
        return mapper.get(type);
    }

    public abstract Integer getType();

    public abstract Class<?> getClassType();

}
