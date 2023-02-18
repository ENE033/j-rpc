package RPC.client;

import RPC.core.ServiceRegistry;
import RPC.core.config.ClientRPCConfig;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

public class ClientTest2 {
    public static void main(String[] args) {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("1.12.233.55:8848");
        clientRPCConfig.setNacosRegistryAddress("1.12.233.55:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        RPCClientProxyFactory rpcClientProxyFactory = new RPCClientProxyFactory(clientRPCConfig);
        TestService testService = rpcClientProxyFactory.getProxy(TestService.class);
        for (int i = 0; i < 100; i++) {
            System.out.println(testService.add());
        }
    }
}
