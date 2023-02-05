package RPC.core;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProvider {

    public static Map<String, Object> SERVICE_MAP = new ConcurrentHashMap<>();

    public static Map<String, Class<?>> CLASS_MAP = new ConcurrentHashMap<>();

    public static Set<Class<?>> CLASS_SET = new HashSet<>();

//    public static Map<String, Map<String, Method>> METHOD_MAP = new ConcurrentHashMap<>();


    public static void addService(String serviceName, Class<?> clazz) {
        if (CLASS_SET.contains(clazz)) {
            return;
        }
        Object obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        CLASS_SET.add(clazz);
        CLASS_MAP.put(serviceName, clazz);
        SERVICE_MAP.put(serviceName, obj);
    }

    public static Class<?> getClass(String serviceName) {
        if (CLASS_MAP.containsKey(serviceName)) {
            return CLASS_MAP.get(serviceName);
        }
        throw new RuntimeException("不存在这个服务");
    }

    public static Object getService(String serviceName) {
        if (SERVICE_MAP.containsKey(serviceName)) {
            return SERVICE_MAP.get(serviceName);
        }
        throw new RuntimeException("不存在这个服务");
    }

}
