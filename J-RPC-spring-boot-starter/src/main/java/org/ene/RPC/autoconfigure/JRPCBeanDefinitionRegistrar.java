package org.ene.RPC.autoconfigure;

import org.ene.RPC.autoconfigure.beanFactoryPostProcessor.JRPCCallerAnnotationPostProcessor;
import org.ene.RPC.core.client.proxy.JRPCClientProxyFactory;
import org.ene.RPC.core.server.JRPCServer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class JRPCBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerBeanDefinition(registry, JRPCCallerAnnotationPostProcessor.class.getCanonicalName(), JRPCCallerAnnotationPostProcessor.class, true);
        registerBeanDefinition(registry, JRPCClientProxyFactory.class.getCanonicalName(), JRPCClientProxyFactory.class, false);
        registerBeanDefinition(registry, JRPCServer.class.getCanonicalName(), JRPCServer.class, false);
    }

    static void registerBeanDefinition(BeanDefinitionRegistry registry,
                                       String beanName,
                                       Class<?> beanClass,
                                       boolean infrastructure) {
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        if (infrastructure) {
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

}
