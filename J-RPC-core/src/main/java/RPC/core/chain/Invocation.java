package RPC.core.chain;

import lombok.Data;

@Data
public class Invocation {

    // 服务名
    String interfaceName;
    // 方法名
    String methodName;
    // 参数类型
    Class<?>[] argsType;
    // 参数
    Object[] args;
    // 消息的序列号
    Integer seq;

}
