package org.ene.RPC.core.chain.server;

import org.ene.RPC.core.chain.AbstractFilterChain;
import org.ene.RPC.core.constants.CommonConstant;

public class InvokerFilterChain extends AbstractFilterChain {
    public InvokerFilterChain() {
        super(CommonConstant.INVOKER);
    }
}

