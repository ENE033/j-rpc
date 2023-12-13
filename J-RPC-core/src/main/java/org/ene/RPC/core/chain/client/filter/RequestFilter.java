package org.ene.RPC.core.chain.client.filter;

import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.protocol.RequestMessage;
import org.ene.RPC.core.util.ReflectUtil;
import org.ene.RPC.core.util.SeqUtil;

import java.lang.reflect.Method;

/**
 * 请求信息处理器
 * 获取序列号
 * 组装requestMessage
 */
@FilterComponent(group = CommonConstant.SENDER, order = 2)
public class RequestFilter implements SenderFilter {
    @Override
    public Object filter(ChainNode nextNode, SenderWrapper senderWrapper) {
        RequestMessage requestMessage = new RequestMessage();
        Class<?> clazz = senderWrapper.getClazz();
        Method method = senderWrapper.getMethod();
        Class<?>[] argsClazz = senderWrapper.getArgsClazz();
        Object[] args = senderWrapper.getArgs();
        Integer seq = SeqUtil.getSeq(method);
        requestMessage.setSeq(seq);
        requestMessage.setIfN(clazz.getCanonicalName());
        requestMessage.setMN(method.getName());
        requestMessage.setAT(ReflectUtil.classArrayToStrArray(argsClazz));
        requestMessage.setA(args);
        senderWrapper.setRequestMessage(requestMessage);
        return nextNode.stream(senderWrapper);
    }
}
