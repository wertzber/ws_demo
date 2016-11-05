package com.liveperson.ws.demo.server.api.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ws.demo.server.api.AbstractMsg;
import com.liveperson.ws.demo.server.api.RequestHandler;
import com.liveperson.ws.demo.server.api.remote.Remote;
import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.resp.ResponseMsg;
import com.liveperson.ws.demo.server.api.resp.WsResponseMsg;
import com.liveperson.ws.demo.server.api.utils.JacksonUtils;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketRemote implements Remote {

    final Session session;
    private ObjectMapper om = JacksonUtils.createObjectMapper();
    public final Map<Class<? extends ReqBody>, RequestHandler> reqHandlers = new ConcurrentHashMap<>();

    public WebSocketRemote(Session session) {
        this.session = session;
    }

    @Override
    public <T extends ReqBody> void addRequestHandler(Class<T> bodyCls, RequestHandler<T> requestHandler) {
        reqHandlers.put(bodyCls, requestHandler);
    }

    @Override
    public void send(ResponseMsg resp) {
        send(new WsResponseMsg(resp.getReqId(),resp.getCode(), resp.getBody()));
    }

    protected void send(AbstractMsg t) {
        try {
            session.getAsyncRemote().sendText(om.writeValueAsString(t));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        session.close(closeReason);
    }

    @Override
    public Map<String, String> getPathParameters() {
        return session.getPathParameters();
    }

    @Override
    public String getProtocolVersion() {
        return session.getProtocolVersion();
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        return session.getRequestParameterMap();
    }

    @Override
    public Principal getUserPrincipal() {
        return session.getUserPrincipal();
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return session.getUserProperties();
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }
}