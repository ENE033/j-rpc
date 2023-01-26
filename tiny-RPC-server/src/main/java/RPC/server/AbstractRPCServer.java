package RPC.server;

import RPC.core.config.server.writeBack.WriteBackStrategy;
import RPC.core.config.server.writeBack.impl.WriteBackInAsync;
import RPC.core.config.server.writeBack.impl.WriteBackInSync;

public abstract class AbstractRPCServer {
    protected WriteBackStrategy writeBackStrategy;
    private Integer writeBack = 0;

    private void createWriteBackStrategy() {
        switch (writeBack) {
            case 0:
                writeBackStrategy = new WriteBackInSync();
                break;
            case 1:
                writeBackStrategy = new WriteBackInAsync();
                break;
        }
    }

    public void setWriteBack(Integer writeBack) {
        this.writeBack = writeBack;
    }

    void init(){
        createWriteBackStrategy();
    }
}
