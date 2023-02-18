package RPC.core.writeBack.impl;


import RPC.core.writeBack.WriteBackStrategy;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WriteBackInAsync implements WriteBackStrategy {
//    private static final EventExecutorGroup EXECUTOR_GROUP = new DefaultEventExecutorGroup(16);
//    private static final ExecutorService EXECUTOR_GROUP = Executors.newFixedThreadPool(16);

    public static final ThreadPoolExecutor EXECUTOR_GROUP = new ThreadPoolExecutor(
            8,
            16,
            5,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500000),
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void writeBack(Runnable task) {
        EXECUTOR_GROUP.execute(task);
    }
}
