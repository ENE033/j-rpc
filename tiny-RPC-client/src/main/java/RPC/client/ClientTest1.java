package RPC.client;

import RPC.core.config.client.loadBanlance.LoadBalanceStrategy;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

public class ClientTest1 {
    public static void main(String[] args) {
        RPCClientProxyFactory.chooseLoadBalance(LoadBalanceStrategy.CONSIST_HASH);
        TestService testService = RPCClientProxyFactory.getProxy(TestService.class);
        System.out.println(testService.getAnswer("ewqrqwt"));
    }
}
