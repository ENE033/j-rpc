package RPC.core.execute.impl;


import RPC.core.execute.ExecuteStrategy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecuteInAsync implements ExecuteStrategy {
//    private static final EventExecutorGroup EXECUTOR_GROUP = new DefaultEventExecutorGroup(16);
//    private static final ExecutorService EXECUTOR_GROUP = Executors.newFixedThreadPool(16);

    public static final ThreadPoolExecutor EXECUTOR_GROUP = new ThreadPoolExecutor(
            8,
            16,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(500000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void writeBack(Runnable task) {
        EXECUTOR_GROUP.execute(task);
    }

    @Override
    public String toString() {
        return "Async";
    }
}