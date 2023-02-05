package RPC.core.config.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.client.config.listener.impl.PropertiesListener;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NacosConfig {
    public static String SERIALIZE_TYPE = "serializer";
    public static String LOADBALANCE_TYPE = "loadBalance";
    public static String WRITEBACK_TYPE = "writeBack";

    private static final Map<String, String> PROPERTIES = new ConcurrentHashMap<>();

    static {
        String server = "1.12.233.55:8848";
        String dataId = "rpc.properties";
        String group = "DEFAULT_GROUP";
        ConfigService configService;
        try {
            configService = NacosFactory.createConfigService(server);
            String configs = configService.getConfig(dataId, group, 10000);
            for (String config : configs.split("\n")) {
                String[] entry = config.split("=");
                PROPERTIES.put(entry[0], entry[1]);
            }
            configService.addListener(dataId, group, new PropertiesListener() {
                @Override
                public void innerReceive(Properties properties) {
                    PROPERTIES.clear();
                    properties.forEach((k, v) -> {
                        PROPERTIES.put((String) k, (String) v);
                    });
                }
            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public static String getConfig(String key) {
        if (PROPERTIES.containsKey(key)) {
            return PROPERTIES.get(key);
        }
        throw new RuntimeException("该属性不存在");
    }

    public static Integer getConfigAsInt(String key) {
        String value = getConfig(key);
        return Integer.parseInt(value);
    }


}
