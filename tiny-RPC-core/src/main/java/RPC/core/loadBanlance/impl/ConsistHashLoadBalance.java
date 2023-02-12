package RPC.core.loadBanlance.impl;

import RPC.core.loadBanlance.AbstractLoadBalance;
import RPC.core.protocol.RequestMessage;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConsistHashLoadBalance extends AbstractLoadBalance {
    private final static Map<String, ConsistHashSelector> INSTANCE_MAP = new ConcurrentHashMap<>();

    private final Object lock = new Object();

    @Override
    public Instance doSelectInstance(List<Instance> instances, RequestMessage requestMessage) {
        String serviceName = requestMessage.getServiceName();
        int identityHashCode = System.identityHashCode(instances);
        ConsistHashSelector selector;
        if ((selector = INSTANCE_MAP.get(serviceName)) == null || identityHashCode != selector.identifyHashcode) {
            synchronized (lock) {
                if ((selector = INSTANCE_MAP.get(serviceName)) == null || identityHashCode != selector.identifyHashcode) {
                    INSTANCE_MAP.put(serviceName, new ConsistHashSelector(identityHashCode, instances));
                    selector = INSTANCE_MAP.get(serviceName);
                }
            }
        }
        return selector.select(requestMessage.getMethodName() + Arrays.stream(requestMessage.getArgsType()).collect(Collectors.toList()));
    }

    static class ConsistHashSelector {
        // nginx的权值为1的节点有160个虚拟节点
        private final static int replicaNumber = 160;
        // 哈希环
        private final TreeMap<Long, Instance> virtualInstances;
        // 哈希环的哈希值，用于判断有没有节点变化
        private final int identifyHashcode;

        public ConsistHashSelector(int identifyHashcode, List<Instance> instances) {
            virtualInstances = new TreeMap<>();
            this.identifyHashcode = identifyHashcode;
            for (Instance instance : instances) {
                String address = instance.getIp() + instance.getPort();
                // 带权值
                for (int i = 0; i < instance.getWeight() * replicaNumber / 4; i++) {
                    // 32个16进制数，总共16字节
                    byte[] md5 = md5(address + i);
                    // 每四个字节就可以生成一个32位的key，一个md5值可以生成四个虚拟节点
                    for (int j = 0; j < 4; j++) {
                        long key = hash(md5, j);
                        virtualInstances.put(key, instance);
                    }
                }
            }
        }

        public Instance select(String selectKey) {
            // 对方法和参数进行哈希
            long hash = hash(md5(selectKey), 0);
            // 通过ceiling找到大于等于hash值的最小节点
            Map.Entry<Long, Instance> instanceEntry = virtualInstances.ceilingEntry(hash);
            // 如果hash值过大，那么就从第一个开始找
            if (instanceEntry == null) {
                return virtualInstances.firstEntry().getValue();
            }
            return instanceEntry.getValue();
        }

        // 将字符串转化为MD5字节数组
        static byte[] md5(String serviceAddress) {
            MessageDigest md5;
            try {
                md5 = MessageDigest.getInstance("md5");
                byte[] bytes = serviceAddress.getBytes(StandardCharsets.UTF_8);
                md5.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
            return md5.digest();
        }

        // 对字节数组的每四个一组结合为一个32位整数
        static long hash(byte[] digest, int num) {
            return ((long) digest[3 + num * 4] & 0xff << 24 |
                    (long) digest[2 + num * 4] & 0xff << 16 |
                    (long) digest[1 + num * 4] & 0xff << 8 |
                    (long) digest[num * 4] & 0xff)
                    & 0xfffffffL;
        }
    }

}
