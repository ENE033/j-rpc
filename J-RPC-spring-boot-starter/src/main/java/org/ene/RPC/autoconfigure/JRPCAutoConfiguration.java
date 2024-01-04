package org.ene.RPC.autoconfigure;

import org.ene.RPC.core.config.ClientRPCConfig;
import org.ene.RPC.core.config.ServerRPCConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@EnableConfigurationProperties({JRPCProperties.class})
@Import(JRPCBeanDefinitionRegistrar.class)
public class JRPCAutoConfiguration implements ApplicationContextAware {

//    ServerRPCConfig serverRPCConfig;
//
//    ClientRPCConfig clientRPCConfig;

    JRPCProperties jrpcProperties;

    ApplicationContext applicationContext;

    DefaultListableBeanFactory registry;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.registry = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }


    // 构造器注入
    public JRPCAutoConfiguration(JRPCProperties jrpcProperties) {
        this.jrpcProperties = jrpcProperties;
    }

    @Bean
    public ServerRPCConfig serverRPCConfig(JRPCProperties jrpcProperties) {
        ServerRPCConfig serverRPCConfig = new ServerRPCConfig();
        if (jrpcProperties.server == null || jrpcProperties.server.nacos == null || jrpcProperties.server.nacos.configuration == null
                || jrpcProperties.server.nacos.registry == null) {
            serverRPCConfig.setSatisfied(false);
            return serverRPCConfig;
        }
        String nacosConfigAddress = jrpcProperties.server.nacos.configuration.address;
        String nacosConfigDataId = jrpcProperties.server.nacos.configuration.dataId;
        String nacosConfigGroup = jrpcProperties.server.nacos.configuration.group;
        String nacosRegistryAddress = jrpcProperties.server.nacos.registry.address;
        String serverHost = jrpcProperties.server.host;
        Integer serverPort = jrpcProperties.server.port;

        if (nacosConfigAddress == null || nacosConfigDataId == null || nacosConfigGroup == null || nacosRegistryAddress == null
                || serverHost == null || serverPort == null) {
            serverRPCConfig.setSatisfied(false);
            return serverRPCConfig;
        }

        serverRPCConfig.setNacosConfigAddress(nacosConfigAddress);
        serverRPCConfig.setNacosConfigDataId(nacosConfigDataId);
        serverRPCConfig.setNacosConfigGroup(nacosConfigGroup);
        serverRPCConfig.setNacosRegistryAddress(nacosRegistryAddress);
        serverRPCConfig.setExposedHost(serverHost);
        serverRPCConfig.setNettyPort(serverPort);
        return serverRPCConfig;
    }

    @Bean
    public ClientRPCConfig clientRPCConfig(JRPCProperties jrpcProperties) {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        if (jrpcProperties.client == null || jrpcProperties.client.nacos == null || jrpcProperties.client.nacos.configuration == null
                || jrpcProperties.client.nacos.registry == null) {
            clientRPCConfig.setSatisfied(false);
            return clientRPCConfig;
        }
        String nacosConfigAddress = jrpcProperties.client.nacos.configuration.address;
        String nacosConfigDataId = jrpcProperties.client.nacos.configuration.dataId;
        String nacosConfigGroup = jrpcProperties.client.nacos.configuration.group;
        String nacosRegistryAddress = jrpcProperties.client.nacos.registry.address;

        if (nacosConfigAddress == null || nacosConfigDataId == null || nacosConfigGroup == null || nacosRegistryAddress == null) {
            clientRPCConfig.setSatisfied(false);
            return clientRPCConfig;
        }
        clientRPCConfig.setNacosConfigAddress(nacosConfigAddress);
        clientRPCConfig.setNacosConfigDataId(nacosConfigDataId);
        clientRPCConfig.setNacosConfigGroup(nacosConfigGroup);
        clientRPCConfig.setNacosRegistryAddress(nacosRegistryAddress);
        return clientRPCConfig;
    }
}
