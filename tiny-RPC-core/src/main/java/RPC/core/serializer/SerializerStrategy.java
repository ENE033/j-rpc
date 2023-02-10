package RPC.core.serializer;

public interface SerializerStrategy {
    Integer JDK_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 0;

    <T> byte[] serializer(T obj);

    <T> T deSerializer(Class<T> clazz, byte[] bytes);

}
