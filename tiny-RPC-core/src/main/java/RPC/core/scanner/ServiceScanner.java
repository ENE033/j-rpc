package RPC.core.scanner;

import java.net.InetSocketAddress;

public interface ServiceScanner {

    void scanServices(InetSocketAddress inetSocketAddress);

}
