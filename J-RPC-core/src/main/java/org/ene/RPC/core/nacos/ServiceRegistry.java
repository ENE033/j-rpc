package org.ene.RPC.core.nacos;

import org.ene.RPC.core.config.ServerRPCConfig;
import org.ene.RPC.core.loadBanlance.LoadBalanceMap;
import org.ene.RPC.core.loadBanlance.LoadBalanceStrategy;
import org.ene.RPC.core.nacos.config.NacosConfig;
import org.ene.RPC.core.protocol.RequestMessage;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;

public class ServiceRegistry {
    private final NacosConfig nacosConfig;
    private final NamingService NAMING_SERVICE;

//    @Value("spring.cloud.nacos.discovery.server-addr")
//    private String nacosServerAddress;

    public ServiceRegistry(NacosConfig nacosConfig) {
        // for test
//        if (serverRpcConfig.getNacosRegistryAddress() == null) {
//            serverRpcConfig.setNacosRegistryAddress("139.159.207.128:8848");
//        }
        this.nacosConfig = nacosConfig;
        try {
            NAMING_SERVICE = NamingFactory.createNamingService(nacosConfig.getNacosRegistryAddress());
        } catch (NacosException e) {
            throw new RuntimeException("注册中心初始化时出现异常:" + e);
        }
    }

    /**
     * 将服务接口注册到nacos
     *
     * @param interfaceName 服务接口的全限定名
     */
    public void registryServiceToNacos(String interfaceName) {
        if (!(nacosConfig instanceof ServerRPCConfig)) {
            throw new RuntimeException("出现未知异常");
        }
        ServerRPCConfig serverRPCConfig = (ServerRPCConfig) this.nacosConfig;
        try {
            NAMING_SERVICE.registerInstance(
                    interfaceName, serverRPCConfig.getExposedHost(), serverRPCConfig.getNettyPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public InetSocketAddress getServiceAddress(String serviceName, RequestMessage requestMessage) {
        List<Instance> allInstances;
        try {
            allInstances = NAMING_SERVICE.getAllInstances(serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("获取实例出现异常", e);
        }
//        Instance instance = allInstances.get(0);
        LoadBalanceStrategy loadBalanceStrategy = LoadBalanceMap.get(nacosConfig.getConfigAsInt(NacosConfig.LOADBALANCE_TYPE));
        Instance instance = loadBalanceStrategy.select(allInstances, requestMessage);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }
}
