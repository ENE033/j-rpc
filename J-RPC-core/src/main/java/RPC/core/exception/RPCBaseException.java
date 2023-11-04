package RPC.core.exception;

public class RPCBaseException extends RuntimeException{
    public RPCBaseException() {
        super();
    }

    public RPCBaseException(String message) {
        super(message);
    }

    public RPCBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RPCBaseException(Throwable cause) {
        super(cause);
    }

    protected RPCBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
