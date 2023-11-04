package org.ene.RPC.core.exception;

public class RPCInvokeException extends RPCBaseException {
    public RPCInvokeException() {
        super();
    }

    public RPCInvokeException(String message) {
        super(message);
    }

    public RPCInvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCInvokeException(Throwable cause) {
        super(cause);
    }

    protected RPCInvokeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
