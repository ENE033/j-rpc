package org.ene.RPC.core.chain.client.filter;

import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.Filter;
import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.chain.client.ReceiverWrapper;

public interface ReceiverFilter extends Filter {
    Object filter(ChainNode nextNode, ReceiverWrapper receiverWrapper);

    default Object filter(ChainNode nextNode, Flow flow) {
        return filter(nextNode, (ReceiverWrapper) flow);
    }
}
