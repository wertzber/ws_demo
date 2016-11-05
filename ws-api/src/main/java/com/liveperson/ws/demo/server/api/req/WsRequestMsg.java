package com.liveperson.ws.demo.server.api.req;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Response;
import java.util.Collection;

import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.liveperson.ws.demo.server.api.AbstractMsg;
import com.liveperson.ws.demo.server.api.resp.RespBody;
import com.liveperson.ws.demo.server.api.resp.WsResponseMsg;

@JsonTypeName(value = "req")
public class WsRequestMsg implements AbstractMsg {
    @Size(min = 1)
    public final String id;

    //TODO: ask oded
    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @Valid
    public final ReqBody body;

    @Valid
    public final Collection<ReqHeader> headers;

    public WsRequestMsg(String id, Collection<ReqHeader> headers, ReqBody body) {
        this.body = body;
        this.id = id;
        this.headers = headers;
    }

    private WsRequestMsg() {
        this(null, null, null);
    }

    public WsResponseMsg response(Response.Status code, RespBody respBody) {
        return new WsResponseMsg(id, code, respBody);
    }
}
