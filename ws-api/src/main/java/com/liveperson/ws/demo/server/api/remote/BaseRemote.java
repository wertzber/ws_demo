package com.liveperson.ws.demo.server.api.remote;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * @author Amit Tal
 * @since 10/26/2015
 *
 * A base class for both client and server remote.
 * In general a full symmetric two-way protocl, client and server can share the same remote.
 * Our protocol is not symmetric which leads to two different remotes for client and server.
 *
 * (not symmetric because of the model: i.e. A client can send a {@code com.liveperson.api.server.RequestMsg}
 *  and a server may send {@code com.liveperson.api.server.ResponseMsg})
 */
public interface BaseRemote {

    void close(CloseReason closeReason) throws IOException;

    Map<String, String> getPathParameters();

    String getProtocolVersion();

    Map<String, List<String>> getRequestParameterMap();

    Principal getUserPrincipal();

    Map<String, Object> getUserProperties();

    boolean isOpen();
}
