package org.ene.RPC.core.protocol;

import lombok.Data;

/**
 * 字段说明：
 * ifN->interfaceName：接口全限定名
 * mN->mN：方法名
 * aT->aT：参数类型数组
 * a->a：参数数组
 * to->timeOut：超时时间
 * re->retry：是否重试
 * reT->retryTime：重试次数
 */
@Data
public class RequestMessage extends Message {
    /**
     * 调用的服务接口的全限定名
     */
    public String ifN;
    /**
     * 调用的方法名
     */
    public String mN;
    /**
     * 参数类型数组
     */
    public String[] aT;
    /**
     * 参数数组
     */
    public Object[] a;
    /**
     * 超时时间，单位ms，默认1000ms
     */
    public int to = 1000;
    /**
     * 是否重试，默认重试
     */
    public boolean re = true;
    /**
     * 重试次数
     */
    public int reT = 3;

    @Override
    public byte getT() {
        return REQUEST;
    }

    public RequestMessage() {

    }

//    @Override
//    public Class<?> getClassType() {
//        return RequestMessage.class;
//    }
}
