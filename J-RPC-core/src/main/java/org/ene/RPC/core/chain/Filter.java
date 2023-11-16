package org.ene.RPC.core.chain;

public interface Filter {
    Object filter(ChainNode nextNode, Flow obj);
}
