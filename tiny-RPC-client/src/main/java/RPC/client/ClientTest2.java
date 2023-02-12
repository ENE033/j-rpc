package RPC.client;

import RPC.core.ServiceRegistry;
import RPC.proxy.RPCClientProxyFactory;
import RPC.service.TestService;

public class ClientTest2 {
    public static void main(String[] args) {
        RPCClientProxyFactory rpcClientProxyFactory = new RPCClientProxyFactory(null);
        TestService testService = rpcClientProxyFactory.getProxy(TestService.class);
        System.out.println(testService.getAnswer("ewqr"));
    }
}
