package RPC.client;

import RPC.core.ServiceRegistry;
import RPC.core.config.ClientRPCConfig;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;
import RPC.service.TestService1;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientTest2 {
    public static void main(String[] args) {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        RPCClientProxyFactory rpcClientProxyFactory = new RPCClientProxyFactory(clientRPCConfig);
//        TestService testService = rpcClientProxyFactory.getProxy(TestService.class);
//
//        System.out.println(testService.getAnswer("ewqrt"));

        TestService1 proxy = rpcClientProxyFactory.getProxy(TestService1.class);

//        proxy.doSomething();

//        proxy.timeTest(null, LocalDateTime.now());

        String s = proxy.timeTest1(null, LocalDateTime.now());
        System.out.println(s);

//        ExecutorService service = Executors.newCachedThreadPool();
//
//        for (int i = 0; i < 10000; i++) {
//            service.execute(() -> {
//                System.out.println(testService.CPUTask());
//            });
//        }
    }
}
