package org.ene.RPC.core.chain;

import lombok.Data;

@Data
public class ChainNode {

    FilterChain filterChainContext;

    ChainNode next;

    Filter filter;

    public ChainNode(Filter filter, FilterChain chain) {
        this.filter = filter;
        filterChainContext = chain;
    }

    public ChainNode(ChainNode next, Filter filter, FilterChain chain) {
        this.next = next;
        this.filter = filter;
        filterChainContext = chain;
    }

    public Object stream(Flow flow) {
        return filter.filter(next, flow);
    }
}
