package org.ene.RPC.core.chain.client.filter;

import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.Filter;
import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.chain.client.SenderWrapper;

public interface SenderFilter extends Filter {
    Object filter(ChainNode nextNode, SenderWrapper senderWrapper);

    default Object filter(ChainNode nextNode, Flow flow) {
        return filter(nextNode, (SenderWrapper) flow);
    }
}
