package RPC.core.chain;

public interface Filter {
    void invoke(ChainNode nextNode, InvocationWrapper inv);
}
