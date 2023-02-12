package RPC.core.writeBack.impl;

import RPC.core.writeBack.WriteBackStrategy;

public class WriteBackInSync implements WriteBackStrategy {
    @Override
    public void writeBack(Runnable task) {
        task.run();
    }
}
