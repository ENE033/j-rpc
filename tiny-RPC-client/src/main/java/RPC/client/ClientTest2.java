package RPC.client;

import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

public class ClientTest2 {
    public static void main(String[] args) {
        TestService testService = RPCClientProxyFactory.getProxy(TestService.class);
        System.out.println(testService.getAnswer("ewqr"));
    }
}
