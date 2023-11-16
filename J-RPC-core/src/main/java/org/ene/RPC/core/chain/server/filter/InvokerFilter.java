package org.ene.RPC.core.chain.server.filter;


import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.Filter;
import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.chain.server.InvocationWrapper;

/**
 * 服务端filter，处理服务端的入口流量
 */
public interface InvokerFilter extends Filter {
    Object stream(ChainNode nextNode, InvocationWrapper invocationWrapper);

    default Object filter(ChainNode nextNode, Flow flow) {
        return stream(nextNode, (InvocationWrapper) flow);
    }

}
