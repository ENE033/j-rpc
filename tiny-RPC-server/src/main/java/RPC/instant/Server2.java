package RPC.instant;

import RPC.core.ServiceRegistry;
import RPC.core.annotation.ServiceScan;
import RPC.server.RPCServer;

@ServiceScan(basePackage = "RPC.serviceImpl")
public class Server2 {
    public static void main(String[] args) {
        RPCServer rpcServer = new RPCServer(null);
        rpcServer.run();
    }
}