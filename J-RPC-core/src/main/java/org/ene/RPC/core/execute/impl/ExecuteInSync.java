package org.ene.RPC.core.execute.impl;

import org.ene.RPC.core.execute.ExecuteStrategy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecuteInSync implements ExecuteStrategy {

    private final static ThreadPoolExecutor SINGLE_EXECUTOR_GROUP =
            new ThreadPoolExecutor(
                    1,
                    1,
                    0,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public void execute(Runnable task) {
        SINGLE_EXECUTOR_GROUP.execute(task);
    }

    @Override
    public String toString() {
        return "Sync";
    }
}
