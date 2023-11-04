package RPC.core.protocol;


public enum ResponseStatus {

    S("成功", 200),

    F("失败", 400);


    private String msg;
    private int code;


    ResponseStatus(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
