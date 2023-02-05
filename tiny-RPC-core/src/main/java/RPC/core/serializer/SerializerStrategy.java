package RPC.core.serializer;

public interface SerializerStrategy {

    <T> byte[] serializer(T obj);

    <T> T deSerializer(Class<T> clazz, byte[] bytes);

}
