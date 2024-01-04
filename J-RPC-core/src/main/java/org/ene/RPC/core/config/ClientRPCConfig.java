package org.ene.RPC.core.config;

import org.ene.RPC.core.nacos.config.NacosConfig;

/**
 * 客户端配置
 */
public class ClientRPCConfig extends NacosConfig {

    protected boolean satisfied = true;

    public boolean isSatisfied() {
        return satisfied;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }
}
