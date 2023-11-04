package RPC.core.proxy;

@FunctionalInterface
public interface ProxyCreateStrategy {
    Integer JDK_MODE = 0;
    Integer CGLIB_MODE = 1;


    <T> T doGetProxyObject(Class<T> clazz, ProxyCreatorAdapter proxyCreatorAdapter);
}
