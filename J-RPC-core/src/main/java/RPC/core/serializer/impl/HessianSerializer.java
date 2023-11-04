package RPC.core.serializer.impl;

import RPC.core.exception.SerializerException;
import RPC.core.serializer.SerializerStrategy;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements SerializerStrategy {
    @Override
    public <T> byte[] serializer(T obj) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(baos);
        try {
            output.writeObject(obj);
            output.completeMessage();
            output.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializerException("序列化错误", e);
        } finally {
            try {
                output.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Hessian2Input input = new Hessian2Input(bais);
        try {
            return (T) input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializerException("序列化错误", e);
        } finally {
            try {
                input.close();
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
