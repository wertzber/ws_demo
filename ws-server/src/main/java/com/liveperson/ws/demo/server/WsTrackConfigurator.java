/*
 * 12/29/15
 */
package com.liveperson.ws.demo.server;

import com.liveperson.ws.demo.server.auth.AuthData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Config hand shake
 */
public class WsTrackConfigurator extends ServerEndpointConfig.Configurator {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsTrackConfigurator.class);

    @Override
    public void modifyHandshake(ServerEndpointConfig config,
                                HandshakeRequest request,
                                HandshakeResponse response)
    {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        if (httpSession != null) {
            final AuthData authData = (AuthData) httpSession.getAttribute("authData");
            LOGGER.info("ModifyHandshake auth data: {}", authData);
            config.getUserProperties().put("authData", authData);
        }
    }
}
