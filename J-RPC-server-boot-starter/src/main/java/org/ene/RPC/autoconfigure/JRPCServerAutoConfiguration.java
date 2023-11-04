package org.ene.RPC.autoconfigure;

import org.ene.RPC.core.config.ServerRPCConfig;
import org.ene.RPC.server.JRPCServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({JRPCServerProperties.class})
public class JRPCServerAutoConfiguration {
    String nacosConfigAddress;

    String nacosConfigDataId;

    String nacosConfigGroup;

    String nacosRegistryAddress;

    Integer serverPort;

    String serverHost;

    JRPCServerProperties jrpcServerProperties;

    ServerRPCConfig serverRPCConfig;

    public JRPCServerAutoConfiguration(JRPCServerProperties jrpcServerProperties) {
        this.jrpcServerProperties = jrpcServerProperties;
        serverRPCConfig = new ServerRPCConfig();
        nacosConfigAddress = jrpcServerProperties.nacos.configuration.address;
        nacosConfigDataId = jrpcServerProperties.nacos.configuration.dataId;
        nacosConfigGroup = jrpcServerProperties.nacos.configuration.group;
        nacosRegistryAddress = jrpcServerProperties.nacos.registry.address;
        serverPort = jrpcServerProperties.server.port;
        serverHost = jrpcServerProperties.server.host;

        serverRPCConfig.setNacosConfigAddress(nacosConfigAddress);
        serverRPCConfig.setNacosConfigDataId(nacosConfigDataId);
        serverRPCConfig.setNacosConfigGroup(nacosConfigGroup);
        serverRPCConfig.setNacosRegistryAddress(nacosRegistryAddress);
        serverRPCConfig.setExposedHost(serverHost);
        serverRPCConfig.setNettyPort(serverPort);
    }

    @Bean
    public JRPCServer jRPCServer() {
        JRPCServer jRPCServer = new JRPCServer(serverRPCConfig);
        return jRPCServer;
    }
}
