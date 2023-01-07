package RPC.core.protocol;


public enum ResponseStatus {

    SUCCESS("成功", 200),

    FAIL("失败", 400);


    private String msg;
    private Integer code;


    ResponseStatus(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
