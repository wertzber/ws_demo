package com.liveperson.ws.demo.server.api.resp;

import com.liveperson.ws.demo.server.api.AbstractBody;

/**
 * @param <T> The type of the RequestBody which this response refers to. In some cases
 * T might not be requestBody, for example when T is superClass for a few requestBodies.
 */
public interface RespBody<T> extends AbstractBody {

}
