package org.ene.RPC.core.execute;


/**
 * 串行执行还是并行执行
 */
public interface ExecuteStrategy {
    Integer SYNC = 0;
    Integer ASYNC = 1;

    void writeBack(Runnable task);

}
