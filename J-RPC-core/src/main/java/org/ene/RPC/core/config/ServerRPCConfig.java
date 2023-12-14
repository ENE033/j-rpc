package org.ene.RPC.core.config;

import org.ene.RPC.core.nacos.config.NacosConfig;

/**
 * 服务端配置
 */
public class ServerRPCConfig extends NacosConfig {

    /**
     * netty服务器的启动端口
     */
    protected volatile Integer nettyPort;

    /**
     * 服务端提供服务的公网ip
     */
    protected volatile String exposedHost;

    public Integer getNettyPort() {
        return nettyPort;
    }

    public void setNettyPort(Integer nettyPort) {
        this.nettyPort = nettyPort;
    }

    public String getExposedHost() {
        return exposedHost;
    }

    public void setExposedHost(String exposedHost) {
        this.exposedHost = exposedHost;
    }
}
