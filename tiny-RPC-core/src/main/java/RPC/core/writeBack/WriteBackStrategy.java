package RPC.core.writeBack;


public interface WriteBackStrategy {
    Integer SYNC = 0;
    Integer ASYNC = 1;

    void writeBack(Runnable task);

}
