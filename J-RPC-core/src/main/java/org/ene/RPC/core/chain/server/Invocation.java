package org.ene.RPC.core.chain.server;

import lombok.Data;
import org.ene.RPC.core.chain.Flow;

@Data
public class Invocation{

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
