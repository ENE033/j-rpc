package RPC.client;

import RPC.core.config.client.loadBanlance.LoadBalanceStrategy;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

public class ClientTest1 {
    public static void main(String[] args) {
        TestService testService = RPCClientProxyFactory.getProxy(TestService.class);
        System.out.println(testService.getAnswer("ewqrqwt"));
        System.out.println(testService.getAnswer("ewqrqwt"));
        System.out.println(testService.getAnswer("ewqrqwt"));
        System.out.println(testService.getAnswer("ewqrqwt"));
    }
}
