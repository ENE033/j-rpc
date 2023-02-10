package RPC.instant;

import RPC.core.annotation.ServiceScan;
import RPC.server.RPCServer;

@ServiceScan(basePackage = "RPC.serviceImpl")
public class Server {
    public static void main(String[] args) {
        RPCServer rpcServer = new RPCServer("1.12.233.55", 4556);
        rpcServer.run();
    }
}
