package RPC.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestMessage extends Message implements Serializable {
    /**
     * 调用的服务名
     */
    public String serviceName;
    /**
     * 调用的方法名
     */
    public String methodName;
    /**
     * 参数类型数组
     */
    public Class<?>[] argsType;
    /**
     * 参数数组
     */
    public Object[] args;
    /**
     * 超时时间，单位ms，默认1000ms
     */
    public Integer timeOut = 1000;
    /**
     * 是否重试，默认重试
     */
    public boolean retry = true;
    /**
     * 重试次数
     */
    public Integer retryTime = 3;

    @Override
    public Integer getType() {
        return REQUEST;
    }

    @Override
    public Class<?> getClassType() {
        return RequestMessage.class;
    }
}
