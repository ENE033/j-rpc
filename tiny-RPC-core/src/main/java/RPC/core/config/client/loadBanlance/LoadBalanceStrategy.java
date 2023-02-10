package RPC.core.config.client.loadBanlance;

import RPC.core.protocol.RequestMessage;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalanceStrategy {
    Integer RANDOM_BY_WEIGHT = 0;
    Integer CONSIST_HASH = 1;

    Instance select(List<Instance> instances, RequestMessage requestMessage);

    Instance doSelectInstance(List<Instance> instances, RequestMessage requestMessage);
}
