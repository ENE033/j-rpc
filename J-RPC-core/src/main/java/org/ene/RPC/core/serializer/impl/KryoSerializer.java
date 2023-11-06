package org.ene.RPC.core.serializer.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.serializer.SerializerStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class KryoSerializer implements SerializerStrategy {

    private static ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    private static ThreadLocal<Output> outputThreadLocal = ThreadLocal.withInitial(() -> new Output(10, -1));

    private static ThreadLocal<Input> inputThreadLocal = ThreadLocal.withInitial(Input::new);

    @Override
    public <T> byte[] serializer(T obj) {
        Kryo kryo = kryoThreadLocal.get();
        Output output = outputThreadLocal.get();
        // 重置position和total
        output.setBuffer(output.getBuffer(), -1);
        kryo.writeObject(output, obj);
        return output.getBuffer();
    }

    @Override
    public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
        Kryo kryo = kryoThreadLocal.get();
        Input input = inputThreadLocal.get();
        input.setBuffer(bytes);
        T obj = kryo.readObject(input, clazz);
        return obj;
    }


}
