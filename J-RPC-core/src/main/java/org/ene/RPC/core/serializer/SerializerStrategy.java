package org.ene.RPC.core.serializer;

public interface SerializerStrategy {
    int JDK = 0;
    int JSON = 1;
    int HESSIAN = 2;
    int KRYO = 3;

    <T> byte[] serializer(T obj);

    <T> T deSerializer(Class<T> clazz, byte[] bytes);

}
