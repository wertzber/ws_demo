package com.liveperson.ws.demo.server.api.remote;

import com.liveperson.ws.demo.server.api.remote.Remote;

import javax.websocket.CloseReason;

// TODO At some point rename this endpoint to be a ServerEndPoint
public interface AbstractEndPoint {
    default public void onOpen(Remote remote) {

    }

    default public void onClose(Remote remote, CloseReason closeReason) {

    }

    default public void onError(Remote remote, Throwable thr) {

    }
}
