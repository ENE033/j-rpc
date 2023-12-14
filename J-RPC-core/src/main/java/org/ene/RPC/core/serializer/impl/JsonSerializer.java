package org.ene.RPC.core.serializer.impl;

import org.ene.RPC.core.exception.JRPCException;
import org.ene.RPC.core.protocol.RequestMessage;
import org.ene.RPC.core.serializer.SerializerStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.util.ReflectUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class JsonSerializer implements SerializerStrategy {

    public ObjectMapper objectMapper = new ObjectMapper();

    public JsonSerializer() {
        objectMapper.setTimeZone(TimeZone.getDefault());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        objectMapper.setDateFormat(sdf);
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public <T> byte[] serializer(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new JRPCException(JRPCException.SERIALIZATION_EXCEPTION, "序列化错误", e);
        }
//        JSONObject.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
//        return JSONObject.toJSONBytes(obj, SerializerFeature.WriteDateUseDateFormat);
//        return JSON.toJSON(obj).toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
        try {
            T message = objectMapper.readValue(bytes, clazz);
            if (message != null && message.getClass().isAssignableFrom(RequestMessage.class)) {
                handleRequestMessage((RequestMessage) message);
            }
            return message;
        } catch (IOException e) {
            throw new JRPCException(JRPCException.SERIALIZATION_EXCEPTION, "序列化错误", e);
        }
//        return JSON.parseObject(new String(bytes), clazz);
    }

    private void handleRequestMessage(RequestMessage requestMessage) {
        Class<?>[] classArray = ReflectUtil.strArrayToClassArray(requestMessage.getAT());
        Object[] args = requestMessage.getA();
        for (int i = 0; i < classArray.length; i++) {
            if (args[i] == null) {
                continue;
            }
            Class<?> aClass = classArray[i];
            if (aClass == Date.class) {
                args[i] = new Date((long) args[i]);
            } else if (aClass == LocalDateTime.class) {
                ArrayList<Integer> localDateTimeArgs = (ArrayList<Integer>) args[i];
                args[i] = LocalDateTime.of(localDateTimeArgs.get(0), localDateTimeArgs.get(1), localDateTimeArgs.get(2), localDateTimeArgs.get(3), localDateTimeArgs.get(4), localDateTimeArgs.get(5), localDateTimeArgs.get(6));
            }
        }
    }

}
