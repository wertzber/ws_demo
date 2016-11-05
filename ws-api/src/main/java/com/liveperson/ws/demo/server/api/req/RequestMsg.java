package com.liveperson.ws.demo.server.api.req;

import com.liveperson.ws.demo.server.api.resp.RespBody;
import com.liveperson.ws.demo.server.api.resp.ResponseMsg;

import javax.ws.rs.core.Response;
import java.util.Collection;


public interface RequestMsg<T extends ReqBody> {
    T getBody();
    <H extends ReqHeader> Collection<H> getHeaders(Class<H> cls);
    <R extends RespBody<? super T>> ResponseMsg<R> response(Response.Status code, R respBody);
}