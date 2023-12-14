package org.ene.RPC.core.server;

public interface ServiceScanner {

    void scanServices(String exposedHost, Integer nettyPort);

}
