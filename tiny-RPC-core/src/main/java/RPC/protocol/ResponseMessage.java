package RPC.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseMessage extends Message implements Serializable {

    public ResponseStatus responseStatus;

    public Object result;

    @Override
    public Integer getType() {
        return RESPONSE;
    }

    @Override
    public Class<?> getClassType() {
        return ResponseMessage.class;
    }
}
