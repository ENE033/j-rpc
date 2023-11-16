package org.ene.RPC.core.chain;

import lombok.Data;

@Data
public class ChainNode {

    ChainNode next;

    Filter filter;

    public ChainNode(Filter filter) {
        this.filter = filter;
    }

    public ChainNode(ChainNode next, Filter filter) {
        this.next = next;
        this.filter = filter;
    }

    public Object stream(Flow flow) {
        return filter.filter(next, flow);
    }
}
