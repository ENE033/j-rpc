package org.ene.RPC.core.chain.client.filter;

import io.netty.channel.Channel;
import io.netty.util.concurrent.DefaultPromise;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.SenderWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.promise.ResponsePromiseMap;
import org.ene.RPC.core.protocol.RequestMessage;

/**
 * 发送处理器
 * 负责将流量发送到channel，并将序列号与Promise关联
 */
@FilterComponent(group = CommonConstant.SENDER, order = 8)
public class FlushFilter implements SenderFilter {
    @Override
    public Object filter(ChainNode nextNode, SenderWrapper senderWrapper) {
        Channel channel = senderWrapper.getChannel();
        RequestMessage requestMessage = senderWrapper.getRequestMessage();
        int seq = requestMessage.getSeq();
        DefaultPromise<Object> promise = new DefaultPromise<>(channel.eventLoop());
        ResponsePromiseMap.put(seq, promise);
        channel.writeAndFlush(requestMessage);
        senderWrapper.setPromise(promise);
        return nextNode.stream(senderWrapper);
    }
}
