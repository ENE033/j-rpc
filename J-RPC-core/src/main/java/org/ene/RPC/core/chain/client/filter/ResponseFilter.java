package org.ene.RPC.core.chain.client.filter;


import io.netty.util.concurrent.Promise;
import org.ene.RPC.core.annotation.FilterComponent;
import org.ene.RPC.core.chain.ChainNode;
import org.ene.RPC.core.chain.client.ReceiverWrapper;
import org.ene.RPC.core.constants.CommonConstant;
import org.ene.RPC.core.exception.JRPCException;
import org.ene.RPC.core.promise.ResponsePromiseMap;
import org.ene.RPC.core.protocol.ResponseMessage;
import org.ene.RPC.core.protocol.ResponseStatus;
import org.ene.RPC.core.util.SeqUtil;

/**
 * 响应处理器
 * 负责获取结果，并唤醒Promise
 */
@FilterComponent(group = CommonConstant.RECEIVER, order = 1)
public class ResponseFilter implements ReceiverFilter {
    @Override
    public Object filter(ChainNode nextNode, ReceiverWrapper receiverWrapper) {
        ResponseMessage responseMessage = receiverWrapper.getResponseMessage();
        ResponseStatus responseStatus = responseMessage.getS();
        int seq = responseMessage.getSeq();
        receiverWrapper.setMethod(SeqUtil.getMethod(seq));
        SeqUtil.removeSeq(seq);
        Promise<Object> promise = ResponsePromiseMap.getAndRemove(seq);
        receiverWrapper.setPromise(promise);
        switch (responseStatus) {
            case S:
                Object result = responseMessage.getR();
                promise.setSuccess(result);
                break;
            case F:
                String errMsg = (String) responseMessage.getR();
                promise.setFailure(new JRPCException(JRPCException.BIZ_EXCEPTION, errMsg));
                break;
        }
        return nextNode.stream(receiverWrapper);
    }
}
