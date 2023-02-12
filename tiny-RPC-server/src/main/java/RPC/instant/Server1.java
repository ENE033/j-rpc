package RPC.instant;

import RPC.core.annotation.ServiceScan;
import RPC.core.config.ServerRPCConfig;
import RPC.server.RPCServer;

@ServiceScan(basePackage = "RPC.serviceImpl")
public class Server1 {
    public static void main(String[] args) {
        ServerRPCConfig serverRPCConfig = new ServerRPCConfig();
        serverRPCConfig.setExposedHost("localhost");
        serverRPCConfig.setNettyPort(7888);
        serverRPCConfig.setNacosConfigAddress("1.12.233.55:8848");
        serverRPCConfig.setNacosRegistryAddress("1.12.233.55:8848");
        serverRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        serverRPCConfig.setNacosConfigDataId("rpc.properties");

        RPCServer rpcServer = new RPCServer(serverRPCConfig);
        rpcServer.run();
    }
}
