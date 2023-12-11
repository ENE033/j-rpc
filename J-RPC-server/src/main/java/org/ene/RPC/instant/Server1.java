package org.ene.RPC.instant;

import org.ene.RPC.core.annotation.ServiceScan;
import org.ene.RPC.core.config.ServerRPCConfig;
import org.ene.RPC.core.server.JRPCServer;

@ServiceScan(basePackages = "org.ene.RPC.serviceImpl")
public class Server1 {
    public static void main(String[] args) {
        ServerRPCConfig serverRPCConfig = new ServerRPCConfig();
        serverRPCConfig.setExposedHost("localhost");
        serverRPCConfig.setNettyPort(7888);
        serverRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        serverRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        serverRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        serverRPCConfig.setNacosConfigDataId("rpc.properties");

        JRPCServer jRPCServer = new JRPCServer(serverRPCConfig);
        jRPCServer.run();
    }
}
