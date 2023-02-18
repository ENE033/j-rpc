package RPC.core.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseMessage extends Message {

    public ResponseStatus responseStatus;

    public Object result;

    @Override
    public Integer getType() {
        return RESPONSE;
    }

    public ResponseMessage() {

    }

//    @Override
//    public Class<?> getClassType() {
//        return ResponseMessage.class;
//    }
}
