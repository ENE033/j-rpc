package RPC.registry;

import java.net.InetSocketAddress;

public interface ServiceScanner {

    void scanServices(InetSocketAddress inetSocketAddress);

}
