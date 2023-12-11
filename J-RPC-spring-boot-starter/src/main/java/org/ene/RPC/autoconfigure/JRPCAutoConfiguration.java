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
        String nacosConfigAddress = jrpcProperties.server.nacos.configuration.address;
        String nacosConfigDataId = jrpcProperties.server.nacos.configuration.dataId;
        String nacosConfigGroup = jrpcProperties.server.nacos.configuration.group;
        String nacosRegistryAddress = jrpcProperties.server.nacos.registry.address;
        String serverHost = jrpcProperties.server.host;
        Integer serverPort = jrpcProperties.server.port;

        serverRPCConfig.setNacosConfigAddress(nacosConfigAddress);
        serverRPCConfig.setNacosConfigDataId(nacosConfigDataId);
        serverRPCConfig.setNacosConfigGroup(nacosConfigGroup);
        serverRPCConfig.setNacosRegistryAddress(nacosRegistryAddress);
        serverRPCConfig.setExposedHost(serverHost);
        serverRPCConfig.setNettyPort(serverPort);
        return serverRPCConfig;
    }

    @Bean
    public ClientRPCConfig clientRPCConfig(JRPCProperties jrpcProperties){
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        String nacosConfigAddress = jrpcProperties.client.nacos.configuration.address;
        String nacosConfigDataId = jrpcProperties.client.nacos.configuration.dataId;
        String nacosConfigGroup = jrpcProperties.client.nacos.configuration.group;
        String nacosRegistryAddress = jrpcProperties.client.nacos.registry.address;

        clientRPCConfig.setNacosConfigAddress(nacosConfigAddress);
        clientRPCConfig.setNacosConfigDataId(nacosConfigDataId);
        clientRPCConfig.setNacosConfigGroup(nacosConfigGroup);
        clientRPCConfig.setNacosRegistryAddress(nacosRegistryAddress);
        return clientRPCConfig;
    }
}
