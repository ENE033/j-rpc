package org.ene.RPC.core.chain.client;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.AbstractFilterChain;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.Filter;
import org.ene.RPC.core.chain.Flow;
import org.ene.RPC.core.constants.CommonConstant;

import java.util.Objects;

public class ReceiverFilterChain extends AbstractFilterChain {
    public ReceiverFilterChain() {
        super(CommonConstant.RECEIVER);
    }
}
