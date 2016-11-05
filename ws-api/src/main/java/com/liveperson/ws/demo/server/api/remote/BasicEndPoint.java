package com.liveperson.ws.demo.server.api.remote;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ws.demo.server.api.AbstractMsg;
import com.liveperson.ws.demo.server.api.RequestHandler;
import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.req.RequestMsg;
import com.liveperson.ws.demo.server.api.req.WsRequestMsg;
import com.liveperson.ws.demo.server.api.resp.RespBody;
import com.liveperson.ws.demo.server.api.resp.ResponseMsg;
import com.liveperson.ws.demo.server.api.types.GetClock;
import com.liveperson.ws.demo.server.api.utils.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.websocket.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by eladw on 11/5/2016.
 */
public class BasicEndPoint extends Endpoint implements AbstractEndPoint {

    private ObjectMapper om = JacksonUtils.createObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicEndPoint.class);
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();


    private WebSocketRemote remote;

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.remote = new WebSocketRemote(session) {
            @Override
            protected void send(AbstractMsg t) {
                super.send(t);
            }
        };


        // DO NOT convert to lambda. Jetty current implementation does not work with lamda handlers
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String m) {
                LOGGER.debug("onMessage: {} , session: {}", m, session.getId());

                try {
                    WsRequestMsg msg = om.readValue(m, WsRequestMsg.class);
                    Set<ConstraintViolation<WsRequestMsg>> violations = VALIDATOR.validate(msg);
                    if (!violations.isEmpty()) {
                        LOGGER.warn("fields in session: {}", session.getId());
                        LOGGER.debug("fields in violations: {}", violations.stream().map(v -> v.toString()).collect(Collectors.joining(",")));
                        session.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "Message violations in fields: " + violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.joining(","))));
                        return;
                    }
                    try {
                        handleRequest(remote, msg);
                    } catch (Exception ex) {
                        LOGGER.error("Exception while processing message", ex);
                    }
                } catch (IOException ex) {
                    try {
                        LOGGER.warn("problem deserializing message from session {}", session.getId());
                        LOGGER.debug("problematic msg {}", m);
                        session.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "problem desserilizing message"));
                    } catch (IOException ex1) {
                    }
                }

            }
        });

        remote.addRequestHandler(GetClock.class, req -> {
            remote.send(req.response(Response.Status.OK, new GetClock.Response(System.currentTimeMillis())));
        });

        onOpen(remote);
    }

    /**
     * For now handles only concrete types.
     *
     * @param remote The connection
     * @param req    The request
     * @throws JsonProcessingException exception
     */
    // TODO @throws IllegalStateException if there is already a MessageHandler registered for the same native websocket message type as this handler. (simulate ws duplicate endpoints?)
    final public void handleRequest(final WebSocketRemote remote, WsRequestMsg req) throws JsonProcessingException {
        final RequestHandler requestHandler = remote.reqHandlers.get(req.body.getClass());
        if (requestHandler != null) {
            requestHandler.handle(wrap(req));
        } else {
            final String msg = "request " + req.body.getClass().getSimpleName() + " is not allowed for "
                    + remote.getPathParameters().get("user");
            LOGGER.warn(msg);
            remote.send((ResponseMsg) req.response(Response.Status.BAD_REQUEST, new ReqBody.StringResp(msg)));
        }

    }

    private RequestMsg wrap(WsRequestMsg req) {
        return new RequestMsg() {

            @Override
            public ReqBody getBody() {
                return req.body;
            }

            @Override
            public Collection getHeaders(Class cls) {
                if (req.headers != null) {
                    return req.headers.stream().filter(hc -> hc.getClass().isAssignableFrom(cls)).collect(Collectors.toList());
                }

                return Collections.EMPTY_LIST;
            }

            @Override
            public ResponseMsg response(Response.Status code, RespBody respBody) {
                return new ResponseMsg() {

                    @Override
                    public RespBody getBody() {
                        return respBody;
                    }

                    @Override
                    public String getReqId() {
                        return req.id;
                    }

                    @Override
                    public Response.Status getCode() {
                        return code;
                    }
                };
            }

        };
    }
}

