package org.ene.RPC.core.chain.client;

import org.ene.RPC.core.nacos.ServiceRegistry;
import org.ene.RPC.core.chain.AbstractFilterChain;
import org.ene.RPC.core.client.JRPCClient;
import org.ene.RPC.core.constants.CommonConstant;


public class SenderFilterChain extends AbstractFilterChain {

    private final ServiceRegistry serviceRegistry;

    private final JRPCClient jrpcClient;


    public SenderFilterChain(ServiceRegistry serviceRegistry, JRPCClient jrpcClient) {
        super(CommonConstant.SENDER);
        this.serviceRegistry = serviceRegistry;
        this.jrpcClient = jrpcClient;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public JRPCClient getJrpcClient() {
        return jrpcClient;
    }
}
