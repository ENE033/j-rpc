package RPC.serializer;

public interface Serializer {

    <T> byte[] serializer(T obj);

    <T> T deSerializer(Class<T> clazz, byte[] bytes);

}
