package RPC.instant;

import RPC.core.ServiceRegistry;
import RPC.core.annotation.ServiceScan;
import RPC.core.config.ServerRPCConfig;
import RPC.server.RPCServer;

import java.lang.invoke.MethodHandle;

@ServiceScan(basePackage = "RPC.serviceImpl")
public class Server {
    public static void main(String[] args) {
        ServerRPCConfig serverRPCConfig = new ServerRPCConfig();
        serverRPCConfig.setExposedHost("139.159.207.128");
        serverRPCConfig.setNettyPort(7888);
        serverRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        serverRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        serverRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        serverRPCConfig.setNacosConfigDataId("rpc.properties");
        RPCServer rpcServer = new RPCServer(serverRPCConfig);
        rpcServer.run();
    }
}
