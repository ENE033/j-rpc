package org.ene.RPC.core.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 字段说明：
 * t->type：消息类型
 * seq：消息的序列号
 */
@Data
public abstract class Message implements Serializable {
    private byte t;
    public int seq;

    public static final int REQUEST = 1;
    public static final int RESPONSE = 2;

//    public static final Map<Integer, Class<?>> mapper;
//
//    static {
//        mapper = new ConcurrentHashMap<>();
//        mapper.put(REQUEST, RequestMessage.class);
//        mapper.put(RESPONSE, ResponseMessage.class);
//    }

    public static Class<?> getClassType(int type) {
        return type == REQUEST ? RequestMessage.class : ResponseMessage.class;
//        return mapper.get(type);
    }

    public abstract byte getT();

//    public abstract Class<?> getClassType();

}
