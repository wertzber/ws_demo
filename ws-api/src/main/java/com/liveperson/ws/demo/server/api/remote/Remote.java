package com.liveperson.ws.demo.server.api.remote;


import com.liveperson.ws.demo.server.api.RequestHandler;
import com.liveperson.ws.demo.server.api.remote.BaseRemote;
import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.resp.ResponseMsg;

/**
 * Created by eladw on 11/5/2016.
 */
public interface Remote extends BaseRemote{

    /**
     * Register handler for this type of requests body.
     *
     * @param <T> Type of the body for this request handler
     * @param bodyCls only concrete classes are supported currently.
     * @param requestHandler The handler itself
     * @throws IllegalStateException if handler for supplied class already exists
     */
    <T extends ReqBody> void addRequestHandler(Class<T> bodyCls, RequestHandler<T> requestHandler);
    void send(ResponseMsg resp);

}
