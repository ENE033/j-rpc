package org.ene.RPC.core.exception;

public class RPCParamException extends RPCBaseException {
    public RPCParamException() {
        super();
    }

    public RPCParamException(String message) {
        super(message);
    }

    public RPCParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCParamException(Throwable cause) {
        super(cause);
    }

    protected RPCParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
