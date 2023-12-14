package org.ene.RPC.core.exception;

public class JRPCException extends RuntimeException {

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int FORBIDDEN_EXCEPTION = 4;
    public static final int SERIALIZATION_EXCEPTION = 5;
    public static final int NO_INVOKER_AVAILABLE_AFTER_FILTER = 6;
    public static final int LIMIT_EXCEEDED_EXCEPTION = 7;
    public static final int TIMEOUT_TERMINATE = 8;
    public static final int REGISTRY_EXCEPTION = 9;
    public static final int ROUTER_CACHE_NOT_BUILD = 10;
    public static final int METHOD_NOT_FOUND = 11;
    public static final int CLASS_NOT_FOUND = 12;
    public static final int VALIDATION_EXCEPTION = 13;
    public static final int AUTHORIZATION_EXCEPTION = 14;
    public static final int CIRCUIT_BREAK_EXCEPTION = 15;
    public static final int SERVICE_NOT_FOUND = 16;
    public static final int STRATEGY_NOT_FOUND = 17;

    private static final long serialVersionUID = 1L;

    int type;

    public JRPCException(int type) {
        super();
        this.type = type;
    }

    public JRPCException(int type, String message) {
        super(message);
        this.type = type;
    }

    public JRPCException(int type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    public JRPCException(int type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    protected JRPCException(int type, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.type = type;
    }
}
