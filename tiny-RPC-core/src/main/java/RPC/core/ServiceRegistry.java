package RPC.core;

import RPC.core.config.client.loadBanlance.LoadBalanceStrategy;
import RPC.core.config.client.loadBanlance.impl.ConsistHashLoadBalance;
import RPC.core.config.client.loadBanlance.impl.RandomByWeightLoadBalance;
import RPC.core.protocol.RequestMessage;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceRegistry {

    private static final NamingService NAMING_SERVICE;

    private static LoadBalanceStrategy loadBalanceStrategy = new RandomByWeightLoadBalance();

    private static final ConcurrentHashMap<Integer, LoadBalanceStrategy> LB_MAP = new ConcurrentHashMap<>();

    static {
        LB_MAP.put(0, new RandomByWeightLoadBalance());
        LB_MAP.put(1, new ConsistHashLoadBalance());
        try {
            NAMING_SERVICE = NamingFactory.createNamingService("1.12.233.55:8848");
        } catch (NacosException e) {
            throw new RuntimeException("注册中心初始化时出现异常:" + e);
        }
    }

    public static void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NAMING_SERVICE.registerInstance(serviceName, inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    public static InetSocketAddress getServiceAddress(String serviceName, RequestMessage requestMessage) {
        List<Instance> allInstances;
        try {
            allInstances = NAMING_SERVICE.getAllInstances(serviceName);
        } catch (NacosException e) {
            throw new RuntimeException("获取实例出现异常", e);
        }
//        Instance instance = allInstances.get(0);
        Instance instance = loadBalanceStrategy.selectInstance(allInstances, requestMessage);
        return new InetSocketAddress(instance.getIp(), instance.getPort());
    }

    public static void chooseLoadBalance(Integer mode) {
        loadBalanceStrategy = LB_MAP.get(mode);
    }

}
