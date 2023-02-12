package RPC.core.loadBanlance;

import RPC.core.protocol.RequestMessage;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public abstract class AbstractLoadBalance implements LoadBalanceStrategy {
    @Override
    public Instance select(List<Instance> instances, RequestMessage requestMessage) {
        if (instances == null || instances.size() == 0) {
            return null;
        }
        return instances.size() == 1 ? instances.get(0) : doSelectInstance(instances, requestMessage);
    }

    public abstract Instance doSelectInstance(List<Instance> instances, RequestMessage requestMessage);
}
