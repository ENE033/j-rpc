package RPC.core.serializer.impl;

import RPC.core.serializer.SerializerStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

public class JsonSerializer implements SerializerStrategy {

    public ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerializer() {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(sdf);
    }

    @Override
    public <T> byte[] serializer(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
//        JSONObject.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
//        return JSONObject.toJSONBytes(obj, SerializerFeature.WriteDateUseDateFormat);
//        return JSON.toJSON(obj).toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return JSON.parseObject(new String(bytes), clazz);
    }
}
