package com.liveperson.ws.demo.server.appauth;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.liveperson.ws.demo.server.AuthenticationFilter;
import com.liveperson.ws.demo.server.NotifyTimer;
import com.liveperson.ws.demo.server.WsTrackConfigurator;
import com.liveperson.ws.demo.server.auth.AuthData;
import com.liveperson.ws.demo.server.dto.AppResponse;
import com.liveperson.ws.demo.server.dto.ErrorResponse;
import com.liveperson.ws.demo.server.dto.Person;
import com.liveperson.ws.demo.server.dto.PersonResponse;
import com.liveperson.ws.demo.server.utils.JacksonUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

@ServerEndpoint(value = "/ws-track/{username}", configurator = WsTrackConfigurator.class)
public class WsTrackNotifyServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WsTrackNotifyServer.class);
    private static final String INPUT_FILE = "D:\\git\\wertzber_ws_demo\\ws-server\\src\\main\\resources\\input.txt";
    public static final int MAX_IDLE_TIMEOUT = 30000;
    public static final int MAX_MESSAGE_BUFFER_SIZE = 60000;
    public static ObjectMapper om = JacksonUtils.createObjectMapper();
    public static ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    public static List<Person> persons = new ArrayList<>();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
    private boolean isStartTimer = false;
    ScheduledFuture<?> scheduledFuture;
    public static Random convGenerator = new Random();


    public static void main(String[] args) throws Exception {
        Server server = new Server(9090);

        final ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addFilter(AuthenticationFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        WebSocketServerContainerInitializer.configureContext(context).addEndpoint(WsTrackNotifyServer.class);
        LOGGER.info("Starting server..");
        server.start();
    }

    @OnOpen
    public void onOpen(final Session session, final @PathParam("username") String userName) throws Exception {
        // do your thing
        final AuthData authData = (AuthData) session.getUserProperties().get("authData");
        if (authData == null ) {
            LOGGER.error("Auth data is null");
            session.getAsyncRemote().sendText(om.writeValueAsString(new ErrorResponse("you are disconnected, close reason: missing Authentication" +
                    " info error")));
            session.close(new CloseReason(() -> 4401, "Authentication error")); //example 4401,4402,4403
            return;
        } else  if(authData.expireTime < System.currentTimeMillis() + 1000){
            LOGGER.error("Auth data expired");
            session.getAsyncRemote().sendText(om.writeValueAsString(new ErrorResponse("you are disconnected, " +
                    "close reason:  Authentication expire error")));
            session.close(new CloseReason(() -> 4402, "Authentication error"));
            return;
        } else {

            List<String> list = Files.readAllLines(new File(INPUT_FILE).toPath(), Charset.defaultCharset());
            LOGGER.info("input.txt mapping: ");
            list.stream().forEach(n -> LOGGER.info(n));

            if(!list.contains(authData.token)){
                LOGGER.error("Token not valid in white list");
                //session.getAsyncRemote().sendText(om.writeValueAsString(om.writeValueAsBytes(new ErrorResponse("4403 you are disconnected, close reason: Authentication permission error"))));
                session.close(new CloseReason(() -> 4403, "Authentication error"));
                return;
            }
        }
        session.setMaxTextMessageBufferSize(MAX_MESSAGE_BUFFER_SIZE);
        session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT); //set max idle timeout
        sessions.put(userName, session);
        session.getAsyncRemote().sendText(om.writeValueAsString(new AppResponse("you are connected")));


        printAllSessions();
    }

    private void startTimers(String userName) {
        //  task will be scheduled after 5 sec delay
        scheduledFuture = executor.scheduleAtFixedRate(new NotifyTimer(userName), 0, 2, TimeUnit.SECONDS);
    }

    @OnMessage
    public void onMessage(final @PathParam("username") String userName, final String msg) throws Exception {
        final Session s = sessions.get(userName);
        try{

            LOGGER.info("recv msg {}", msg);
            Person person = om.readValue(msg, Person.class);
            persons.add(person);
            LOGGER.info("msg after jackson serialize {}", person);
            if (s != null) {
                s.getAsyncRemote().sendText(om.writeValueAsString(new PersonResponse(person)));
            } else {
                LOGGER.warn("Can't echo msg, user {} not connected ", userName);
            }
            if(!isStartTimer) startTimers(userName);

        } catch(Exception e){
            if(s!=null){
                s.getAsyncRemote().sendText(om.writeValueAsString(new ErrorResponse(userName + " send unsupported message " + msg)));
                LOGGER.error("Failed parse msg " + msg, e);
            }
            LOGGER.info("un support msg {}", msg);
        }
    }

    @OnClose
    public void onClose(final Session session) throws IOException {
        String userName = ((AuthData)session.getUserProperties().get("authData")).userName;
        sessions.remove(userName);
        scheduledFuture.cancel(true);
        printAllSessions();
    }

    private void printAllSessions(){
        LOGGER.info("Print all Sessions: /n ==============================/n");
        sessions.forEach((key, val) -> LOGGER.info(key));
    }
}
