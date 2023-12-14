package org.ene.RPC.core.nacos.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.ene.RPC.core.execute.ExecuteStrategy;
import org.ene.RPC.core.loadBanlance.LoadBalanceStrategy;
import org.ene.RPC.core.proxy.ProxyCreateStrategy;
import org.ene.RPC.core.serializer.SerializerStrategy;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * nacos配置中心，获取配置中心的配置信息，监听配置信息的变动
 */
@Slf4j
@Data
public class NacosConfig {
    public final static String SERIALIZE_TYPE = "serializer";
    public final static String LOADBALANCE_TYPE = "loadBalance";
    public final static String EXECUTE_TYPE = "execute";
    public final static String PROXY_MODE = "proxyMode";

    /**
     * 注册中心的地址
     */
    protected volatile String nacosRegistryAddress;

    /**
     * 配置中心的地址
     */
    protected volatile String nacosConfigAddress;

    /**
     * 配置中心的dataId
     */
    protected volatile String nacosConfigDataId;

    /**
     * 配置中心的group
     */
    protected volatile String nacosConfigGroup;

    private final Map<String, String> properties = new ConcurrentHashMap<>();

    protected volatile ConfigService configService;

    public NacosConfig() {
    }

    /**
     * 延迟初始化，为了整合spring
     */
    public void init() {
        if (!StringUtils.hasText(nacosConfigAddress)) {
            return;
        }
        try {
            configService = NacosFactory.createConfigService(nacosConfigAddress);
            String configs = configService.getConfig(nacosConfigDataId, nacosConfigGroup, 10000);
            if (StringUtils.hasText(configs)) {
                for (String config : configs.split("\n")) {
                    String[] entry = config.split("=");
                    properties.put(entry[0], entry[1]);
                }
            }
            // 注册监听器
            configService.addListener(nacosConfigDataId, nacosConfigGroup, new PropertiesListener() {
                @Override
                public void innerReceive(Properties properties) {
                    properties.clear();
                    log.info("收到参数修改通知,修改后的参数为");
                    properties.forEach((k, v) -> {
                        log.info("param:{},value:{}", k, v);
                        properties.put((String) k, (String) v);
                    });
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public String getConfig(String key) {
        if (properties.containsKey(key)) {
            return properties.get(key);
        }
        String defaultValue;
        log.warn("key：{}在配置中心中没有进行配置，取默认值：{}", key, (defaultValue = getDefaultValue(key)));
        return defaultValue;
    }

    public String getDefaultValue(String key) {
        switch (key) {
            case EXECUTE_TYPE:
                return String.valueOf(ExecuteStrategy.SYNC);
            case SERIALIZE_TYPE:
                return String.valueOf(SerializerStrategy.KRYO);
            case LOADBALANCE_TYPE:
                return String.valueOf(LoadBalanceStrategy.RANDOM_BY_WEIGHT);
            case PROXY_MODE:
                return String.valueOf(ProxyCreateStrategy.JDK_MODE);
        }
        throw new RuntimeException("key不存在");
    }


    public int getConfigAsInt(String key) {
        return Integer.parseInt(getConfig(key));
    }

    public byte getConfigAsByte(String key) {
        return Byte.parseByte(getConfig(key));
    }
}
