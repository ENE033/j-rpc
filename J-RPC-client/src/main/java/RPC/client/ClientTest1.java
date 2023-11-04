package RPC.client;

import RPC.core.config.ClientRPCConfig;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

import java.io.IOException;

public class ClientTest1 {
    public static void main(String[] args) throws IOException {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        RPCClientProxyFactory rpcClientProxyFactory = new RPCClientProxyFactory(clientRPCConfig);
        TestService testService = rpcClientProxyFactory.getProxy(TestService.class);
//        System.out.println(testService.getCount());
        try {
//            System.out.println(testService.getNow());
            System.out.println(testService.getAnswer("eqwrqwrdsa"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(testService.getNowDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
