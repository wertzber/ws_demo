package com.liveperson.ws.demo.server.api.resp;

import com.liveperson.ws.demo.server.api.resp.RespBody;

import javax.ws.rs.core.Response;

public interface ResponseMsg<T extends RespBody>{
    T getBody();
    String getReqId();
    Response.Status getCode();
}