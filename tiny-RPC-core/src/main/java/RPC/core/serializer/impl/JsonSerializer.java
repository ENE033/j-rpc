package RPC.core.serializer.impl;

import RPC.core.serializer.SerializerStrategy;
import com.alibaba.fastjson.JSON;

import java.nio.charset.StandardCharsets;

public class JsonSerializer implements SerializerStrategy {
    @Override
    public <T> byte[] serializer(T obj) {
        return JSON.toJSON(obj).toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(new String(bytes), clazz);
    }
}
