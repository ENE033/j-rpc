package org.ene.RPC.client.instant;

import org.ene.RPC.core.client.proxy.JRPCClientProxyFactory;
import org.ene.RPC.core.config.ClientRPCConfig;
import org.ene.RPC.service.TestService1;

import java.time.LocalDateTime;

public class ClientTest2 {
    public static void main(String[] args) {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        JRPCClientProxyFactory JRPCClientProxyFactory = new JRPCClientProxyFactory(clientRPCConfig);
//        TestService testService = JRPCClientProxyFactory.getProxy(TestService.class);
//
//        System.out.println(testService.getAnswer("ewqrt"));

        TestService1 proxy = JRPCClientProxyFactory.getProxy(TestService1.class);

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
