package org.ene.RPC.autoconfigure.beanFactoryPostProcessor;

import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.autoconfigure.factoryBean.CallerFactoryBean;
import org.ene.RPC.core.annotation.JRPCCaller;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class JRPCCallerAnnotationPostProcessor implements
        BeanFactoryPostProcessor, InstantiationAwareBeanPostProcessor, ApplicationContextAware, BeanClassLoaderAware {


    ClassLoader classLoader;

    BeanDefinitionRegistry beanDefinitionRegistry;

    ApplicationContext applicationContext;

    HashMap<String, List<JRPCCallerFieldElement>> JRPCCallerFieldElementMap = new HashMap<>();

    HashSet<String> factoryBeanCache = new HashSet<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
    }


    public BeanFactory getBeanFactory() {
        return applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> beanType = null;
            if (beanFactory.isFactoryBean(beanName)) {
                BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
                if (CallerFactoryBean.class.getName().equals(beanDefinition.getBeanClassName())) {
                    continue;
                }

                String beanClassName = beanDefinition.getBeanClassName();
                try {
                    beanType = classLoader.loadClass(beanClassName);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                beanType = beanFactory.getType(beanName);
            }
            if (beanType != null) {
                try {
                    registerFactoryBean(beanName, findJRPCCaller(beanType));
                } catch (Exception e) {
                    throw new RuntimeException("JRPCCaller扫描失败", e);
                }
            }
        }
    }

    private void registerFactoryBean(String beanName, List<JRPCCallerFieldElement> metadata) {
        if (CollectionUtils.isEmpty(metadata)) {
            return;
        }
        for (JRPCCallerFieldElement fieldElement : metadata) {
            Field field = (Field) fieldElement.getMember();
            Class<?> interfaceClass = field.getType();
            String interfaceClassName = interfaceClass.getName();
            String factoryBeanClassName = "JRPCCallerFactoryBean-" + interfaceClassName;
            fieldElement.factoryBeanName = factoryBeanClassName;

            if (factoryBeanCache.contains(factoryBeanClassName)) {
                continue;
            }
            RootBeanDefinition beanDefinition = new RootBeanDefinition();
            beanDefinition.setBeanClassName(CallerFactoryBean.class.getName());
            beanDefinition.setAttribute("interfaceClass", interfaceClass);
            GenericBeanDefinition targetDefinition = new GenericBeanDefinition();
            targetDefinition.setBeanClass(interfaceClass);
            beanDefinition.setDecoratedDefinition(new BeanDefinitionHolder(targetDefinition, interfaceClassName + "_decorated"));
            beanDefinitionRegistry.registerBeanDefinition(factoryBeanClassName, beanDefinition);
            factoryBeanCache.add(factoryBeanClassName);
        }
        JRPCCallerFieldElementMap.put(beanName, metadata);
    }

    private List<JRPCCallerFieldElement> findJRPCCaller(Class<?> beanType) {
        final List<JRPCCallerFieldElement> elements = new LinkedList<>();
        ReflectionUtils.doWithFields(beanType, field -> {
            if (!field.isAnnotationPresent(JRPCCaller.class)) {
                return;
            }
//            if (Modifier.isStatic(field.getModifiers())) {
//                return;
//            }
            elements.add(new JRPCCallerFieldElement(field));
        });

        return elements;

    }

    protected class JRPCCallerFieldElement extends InjectionMetadata.InjectedElement {

        protected volatile String factoryBeanName;

        protected JRPCCallerFieldElement(Field field) {
            super(field, null);
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
            if (member instanceof Field) {
                Field field = (Field) member;
                ReflectionUtils.makeAccessible(field);
                field.set(bean, getProxyObject());
            }
        }

        private Object getProxyObject() {
            if (factoryBeanName == null) {
                throw new IllegalStateException("没有找到相应的JRPCCaller");
            }
            return getBeanFactory().getBean(factoryBeanName);
        }
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        try {
            List<JRPCCallerFieldElement> metadata = JRPCCallerFieldElementMap.get(beanName);
            if (!CollectionUtils.isEmpty(metadata)) {
                for (JRPCCallerFieldElement fieldElement : metadata) {
                    fieldElement.inject(bean, beanName, pvs);
                }
            }
        } catch (BeansException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, ex);
        }
        return pvs;
    }
}
