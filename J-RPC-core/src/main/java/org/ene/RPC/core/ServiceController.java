package org.ene.RPC.core;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.annotation.JRPCService;
import org.ene.RPC.core.exception.JRPCException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务控制器：
 * 用于服务端，主要职责为：
 * 1、缓存service实体（在不用spring的前提下，使用spring之后将直接ioc容器作为实体缓存）
 * 2、缓存service实体的类对象，为服务端提供方法对象
 * 3、客户端请求的适配，客户端只能使用接口全限定名发起调用，由本类负责接口全限定名与beanName之间的适配
 * 4、防止重复加入相同服务
 */
@Slf4j
public class ServiceController {

    private final ApplicationContext applicationContext;

    public ServiceController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 不用spring框架时才使用，用了spring框架之后直接使用ioc容器来作为serviceMap
     * 饿汉式
     */
    public Map<String, Object> serviceMap = new HashMap<>();

    /**
     * 用于服务端放射获取方法对象
     * 饿汉式
     */
    public Map<String, Class<?>> classMap = new HashMap<>();

    /**
     * 用于避免重复加入服务
     * 饿汉式
     */
    public Set<Class<?>> classSet = new HashSet<>();

    /**
     * interfaceName->beanName的映射
     * 1、用于判断有没有使用beanName作为别名
     * 2、用于做客户端请求的适配器，客户端只传入接口的全限定名，由本类的beanNameMap进行适配
     * 饿汉式
     */
    public Map<String, String> beanNameMap = new HashMap<>();

    /**
     * 懒汉式
     */
    private final Map<String, Method> methodMap = new ConcurrentHashMap<>(128);


    /**
     * 添加服务
     *
     * @param interfaceName
     * @param clazz
     * @param beanName
     */
    public void addService(String interfaceName, Class<?> interfaceClazz, Class<?> clazz, String beanName) {
        if (classSet.contains(clazz)) {
            return;
        }
        Object obj = null;

        // beanName优先
        String determinedName = StrUtil.isEmpty(beanName) ? interfaceName : beanName;

        if (applicationContext == null) {
            try {
                obj = clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            // 没有spring的ioc容器，就需要使用serviceMap来缓存service对象
            serviceMap.put(determinedName, obj);
        } else {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(interfaceClazz);
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

            String beanId = null;
            if (beanNamesForType.length > 1) {
                for (String beanNameForType : beanNamesForType) {
                    AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(beanNameForType);
                    if (beanDefinition.hasBeanClass()) {
                        Class<?> beanClass = beanDefinition.getBeanClass();
                        boolean present = beanClass.isAnnotationPresent(JRPCService.class);
                        if (present) {
                            if (beanId == null) {
                                beanId = beanNameForType;
                            } else {
                                throw new JRPCException(JRPCException.VALIDATION_EXCEPTION, "JRPCService装配失败，接口存在多个可装配的实现类");
                            }
                        }
                    }
                }
            } else if (beanNamesForType.length == 1) {
                beanId = beanNamesForType[0];
            }else {
                throw new JRPCException(JRPCException.VALIDATION_EXCEPTION, "JRPCService装配失败，接口不存在可装配的实现类");
            }
            determinedName = StrUtil.isEmpty(beanName) ? beanId : beanName;
        }
        // 使用适配后的服务名为key，缓存类对象
        classMap.put(determinedName, interfaceClazz);
        // 添加接口名和beanName的映射关系作为适配
        beanNameMap.put(interfaceName, determinedName);
        // 防止重复添加
        classSet.add(clazz);
    }

    /**
     * 获取服务的类
     *
     * @param interfaceName
     * @return
     */
    public Class<?> getServiceClass(String interfaceName) {
        String determinedName = beanNameMap.get(interfaceName);
        if (classMap.containsKey(determinedName)) {
            return classMap.get(determinedName);
        }
        throw new JRPCException(JRPCException.SERVICE_NOT_FOUND, "不存在这个服务：" + interfaceName);
    }

    /**
     * 获取服务，并用于适配客户端发送过来的请求
     *
     * @param interfaceName
     * @return
     */
    public Object getService(String interfaceName) {
        String determinedName = beanNameMap.get(interfaceName);
        if (applicationContext != null) {
            if (applicationContext.containsBean(determinedName)) {
                return applicationContext.getBean(determinedName);
            }
        } else {
            if (serviceMap.containsKey(determinedName)) {
                return serviceMap.get(determinedName);
            }
        }
        throw new JRPCException(JRPCException.SERVICE_NOT_FOUND, "不存在这个服务：" + interfaceName);
    }


    public Method getMethod(Class<?> clazz, String methodCanonicalName, Class<?>[] argsType) {
        StringJoiner stringJoiner = new StringJoiner("|");
        stringJoiner.add(methodCanonicalName);
        for (Class<?> aClass : argsType) {
            stringJoiner.add(aClass.getCanonicalName());
        }
        String methodKey = stringJoiner.toString();
        Method method;
        if ((method = methodMap.get(methodKey)) == null) {
            try {
                method = clazz.getMethod(methodCanonicalName, argsType);
                methodMap.putIfAbsent(methodKey, method);
            } catch (NoSuchMethodException e) {
                throw new JRPCException(JRPCException.METHOD_NOT_FOUND, "服务端没有找到对应的方法", e);
            }
        }
        return method;
    }

}
