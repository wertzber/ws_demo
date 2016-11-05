package com.liveperson.ws.demo.server.api;

import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.req.RequestMsg;

public interface RequestHandler<T extends ReqBody> {

    void handle(RequestMsg<T> req);

}