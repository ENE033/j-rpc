package org.ene.RPC.client.instant;

import org.ene.RPC.core.annotation.ServiceScan;
import org.ene.RPC.core.client.proxy.JRPCClientProxyFactory;
import org.ene.RPC.core.config.ClientRPCConfig;
import org.ene.RPC.service.TestService;

import java.io.IOException;


@ServiceScan(basePackages = "org.ene.RPC.serviceImpl")
public class ClientTest1 {
    public static void main(String[] args) throws IOException {
        ClientRPCConfig clientRPCConfig = new ClientRPCConfig();
        clientRPCConfig.setNacosConfigAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosRegistryAddress("139.159.207.128:8848");
        clientRPCConfig.setNacosConfigGroup("DEFAULT_GROUP");
        clientRPCConfig.setNacosConfigDataId("rpc.properties");
        JRPCClientProxyFactory jRPCClientProxyFactory = new JRPCClientProxyFactory(clientRPCConfig);
        TestService testService = jRPCClientProxyFactory.getProxy(TestService.class);
//        System.out.println(testService.getCount());
        try {
//            System.out.println(testService.getNow());
            System.out.println(testService.getAnswer("我爱果汁3124"));
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
