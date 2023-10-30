package RPC.core.execute.impl;

import RPC.core.execute.ExecuteStrategy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecuteInSync implements ExecuteStrategy {

    private final static ThreadPoolExecutor SINGLE_EXECUTOR_GROUP =
            new ThreadPoolExecutor(
                    1,
                    1,
                    0,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(500000),
                    new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void writeBack(Runnable task) {
        SINGLE_EXECUTOR_GROUP.execute(task);
    }

    @Override
    public String toString() {
        return "Sync";
    }
}
