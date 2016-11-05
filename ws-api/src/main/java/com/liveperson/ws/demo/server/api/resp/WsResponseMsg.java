package com.liveperson.ws.demo.server.api.resp;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Response;
import com.liveperson.ws.demo.server.api.AbstractMsg;


@JsonTypeName(value = "resp")
public class WsResponseMsg implements AbstractMsg {

    /**
     * The id of the original request.
     */
    @Size(min = 1)
    public final String reqId;

    /**
     * Response code. According to the HTTP response code.
     */
    public final int code;

    /**
     * Response body. May be null.
     */
    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @Valid
    public final RespBody body;

    public WsResponseMsg(String reqId, Response.Status code, RespBody body) {
        this.body = body;
        this.reqId = reqId;
        this.code = code.getStatusCode();
    }

    private WsResponseMsg() {
        this("", Response.Status.OK, null);
    }
}
