package org.ene.RPC.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractRPCServer implements ApplicationContextAware {
    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // rpc还没启动
    private final static int NEW = 0;
    // rpc正在启动中
    private final static int STARING = 1;
    // rpc已经启动完毕
    private final static int STARED = 2;

    AtomicInteger state = new AtomicInteger(0);

    boolean init() {
        return state.compareAndSet(NEW, STARING);
    }

    void stared() {
        state.set(STARED);
    }

}