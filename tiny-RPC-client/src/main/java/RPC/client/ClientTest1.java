package RPC.client;

import RPC.core.config.ClientRPCConfig;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

public class ClientTest1 {
    public static void main(String[] args) {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("1.12.233.55:8848");
        clientRPCConfig.setNacosRegistryAddress("1.12.233.55:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        RPCClientProxyFactory rpcClientProxyFactory = new RPCClientProxyFactory(clientRPCConfig);
        TestService testService = rpcClientProxyFactory.getProxy(TestService.class);
        System.out.println(testService.getAnswer("恐怕就ewqrqwt"));
        System.out.println(testService.hashCode());
        System.out.println(testService);
        System.out.println(testService.getAnswer("ewqrqwt"));
        System.out.println(testService.getNow());
    }
}
