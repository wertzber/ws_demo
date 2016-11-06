package com.liveperson.ws.demo.server.ep;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ws.demo.server.AuthenticationFilter;
import com.liveperson.ws.demo.server.WsTrackConfigurator;
import com.liveperson.ws.demo.server.api.utils.JacksonUtils;
import com.liveperson.ws.demo.server.auth.AuthData;
import com.liveperson.ws.demo.server.req.CreatePerson;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/ws-track/{username}", configurator = WsTrackConfigurator.class)
public class WsTrackServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsTrackServer.class);
    private static final String INPUT_FILE = "D:\\git\\wertzber_ws_demo\\ws-server\\src\\main\\resources\\input.txt";
    private ObjectMapper om = JacksonUtils.createObjectMapper();
    static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        Server server = new Server(9090);

        final ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addFilter(AuthenticationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        WebSocketServerContainerInitializer.configureContext(context).addEndpoint(WsTrackServer.class);
        LOGGER.info("Starting server..");
        server.start();
    }

    @OnOpen
    public void onOpen(final Session session, final @PathParam("username") String userName) throws Exception {
        // do your thing
        final AuthData authData = (AuthData) session.getUserProperties().get("authData");
        if (authData == null ) {
            LOGGER.error("Auth data is null");
            session.getAsyncRemote().sendText("you are disconnected, close reason: missing Authentication info error");
            session.close(new CloseReason(() -> 4401, "Authentication error")); //example 4401,4402,4403
            return;
        } else  if(authData.expireTime < System.currentTimeMillis() + 1000){
            LOGGER.error("Auth data expired");
            session.getAsyncRemote().sendText("you are disconnected, close reason:  Authentication expire error");
            session.close(new CloseReason(() -> 4402, "Authentication error"));
            return;
        } else {

            List<String> list = Files.readAllLines(new File(INPUT_FILE).toPath(), Charset.defaultCharset());
            LOGGER.info("input.txt mapping: ");
            list.stream().forEach(n -> LOGGER.info(n));

            if(!list.contains(authData.token)){
                LOGGER.error("Token not valid in white list");
                session.getAsyncRemote().sendText("you are disconnected, close reason: Authentication permission error");
                session.close(new CloseReason(() -> 4403, "Authentication error"));
                return;
            }
        }
        sessions.put(userName, session);
        session.getAsyncRemote().sendText("you are connected");
        printAllSessions();
    }

    @OnMessage
    public void onMessage(final @PathParam("username") String userName, final String msg) throws Exception {


        final Session s = sessions.get(userName);
        try{
            LOGGER.info("recv msg {}", msg);
            CreatePerson person = om.readValue(msg, CreatePerson.class);
            LOGGER.info("msg after jackson serialize {}", person);
            if (s != null) {
                s.getAsyncRemote().sendText(/*userName + " echo says: " + */om.writeValueAsString(person));
            } else {
                LOGGER.warn("Can't echo msg, user {} not connected ", userName);
            }
        } catch(Exception e){
            if(s!=null){
                s.getAsyncRemote().sendText(userName + " send unsupported message " + msg );
            }
            LOGGER.info("un support msg {}", msg);
        }
    }

    @OnClose
    public void onClose(final Session session) throws IOException {
        String userName = ((AuthData)session.getUserProperties().get("authData")).userName;
        sessions.remove(userName);
        printAllSessions();
    }

    private void printAllSessions(){
        LOGGER.info("Print all Sessions: /n ==============================/n");
        sessions.forEach((key, val) -> LOGGER.info(key));
    }
}
