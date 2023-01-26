package RPC.core.config.server.writeBack.impl;

import RPC.core.config.server.writeBack.WriteBackStrategy;

public class WriteBackInSync implements WriteBackStrategy {
    @Override
    public void writeBack(Runnable task) {
        task.run();
    }
}
