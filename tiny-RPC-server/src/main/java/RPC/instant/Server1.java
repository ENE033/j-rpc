package RPC.instant;

import RPC.core.annotation.ServiceScan;
import RPC.core.config.server.writeBack.WriteBackStrategy;
import RPC.server.RPCServer;

@ServiceScan(basePackage = "RPC.serviceImpl")
public class Server1 {
    public static void main(String[] args) {
        RPCServer rpcServer = new RPCServer("localhost", 4555);
        rpcServer.run();
    }
}
