package RPC.core.config.server.writeBack.impl;


import RPC.core.config.server.writeBack.WriteBackStrategy;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class WriteBackInAsync implements WriteBackStrategy {
    private static final EventExecutorGroup EXECUTOR_GROUP = new DefaultEventExecutorGroup(16);
//    private static final ExecutorService EXECUTOR_GROUP = Executors.newFixedThreadPool(16);

    @Override
    public void writeBack(Runnable task) {
        EXECUTOR_GROUP.execute(task);
    }
}
