package RPC.core.config;

import RPC.core.config.nacos.NacosConfig;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;

import java.util.Properties;

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
