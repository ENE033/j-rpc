package org.ene.RPC.core.protocol;

import lombok.Data;


/**
 * 字段说明：
 * s->status
 * r->result
 */
@Data
public class ResponseMessage extends Message {

    public ResponseStatus s;

    public Object r;
//
//    public Throwable e;

    @Override
    public byte getT() {
        return RESPONSE;
    }

    public ResponseMessage() {

    }

//    @Override
//    public Class<?> getClassType() {
//        return ResponseMessage.class;
//    }
}
