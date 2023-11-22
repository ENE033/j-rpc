package org.ene.RPC.core.chain.client.filter;

import io.netty.channel.Channel;
import org.ene.RPC.core.ServiceRegistry;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderFilterChain;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.client.JRPCClient;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.protocol.RequestMessage;

import java.net.InetSocketAddress;

@FilterComponent(group = CommonConstant.SENDER, order = 6)
public class LoadBalanceFilter implements SenderFilter {
    @Override
    public Object filter(ChainNode nextNode, SenderWrapper senderWrapper) {
        SenderFilterChain filterChain = (SenderFilterChain) nextNode.getFilterChainContext();
        ServiceRegistry serviceRegistry = filterChain.getServiceRegistry();
        JRPCClient jrpcClient = filterChain.getJrpcClient();

        Class<?> clazz = senderWrapper.getClazz();
        RequestMessage requestMessage = senderWrapper.getRequestMessage();
        // 负载均衡，获取一个实例的地址
        InetSocketAddress serviceAddress = serviceRegistry.getServiceAddress(clazz.getCanonicalName(), requestMessage);
        Channel channel = jrpcClient.getChannel(serviceAddress);
        senderWrapper.setChannel(channel);
        return nextNode.stream(senderWrapper);
    }
}
