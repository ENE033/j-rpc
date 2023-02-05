package RPC.core.serializer.impl;

import RPC.core.exception.SerializerException;
import RPC.core.serializer.SerializerStrategy;

import java.io.*;

public class JdkSerializer implements SerializerStrategy {

    @Override
    public <T> byte[] serializer(T obj) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializerException("序列化错误", e);
        } finally {
            try {
                oos.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deSerializer(Class<T> clazz, byte[] bytes) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializerException("序列化错误", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SerializerException("无法找到类", e);
        } finally {
            try {
                ois.close();
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
