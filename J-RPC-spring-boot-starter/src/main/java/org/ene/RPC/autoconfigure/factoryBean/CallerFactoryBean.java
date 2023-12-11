package org.ene.RPC.autoconfigure.factoryBean;

import org.ene.RPC.autoconfigure.JRPCAutoConfiguration;
import org.ene.RPC.core.client.proxy.JRPCClientProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CallerFactoryBean<T> implements FactoryBean<T>, InitializingBean, ApplicationContextAware, BeanNameAware {

    JRPCClientProxyFactory jrpcClientProxyFactory;

    Class<?> interfaceClass;

    String interfaceName;

    ApplicationContext applicationContext;

    String beanId;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanName(String name) {
        beanId = name;
    }

    @Override
    public T getObject() throws Exception {
        return (T) jrpcClientProxyFactory.getProxy(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jrpcClientProxyFactory = applicationContext.getBean(JRPCClientProxyFactory.class);
        ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanId);
        this.interfaceClass = (Class<?>) beanDefinition.getAttribute("interfaceClass");
        this.interfaceName = (String) beanDefinition.getAttribute("interfaceName");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
