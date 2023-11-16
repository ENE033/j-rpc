package org.ene.RPC.core.chain.client;

import org.ene.RPC.core.chain.AbstractFilterChain;
import org.ene.RPC.core.constants.CommonConstant;


public class SenderFilterChain extends AbstractFilterChain {
    public SenderFilterChain() {
        super(CommonConstant.SENDER);
    }
}
