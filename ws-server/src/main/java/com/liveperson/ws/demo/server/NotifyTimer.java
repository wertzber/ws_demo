package com.liveperson.ws.demo.server;

import com.liveperson.ws.demo.server.appauth.WsTrackNotifyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * Created by eladw on 11/10/16.
 */
public class NotifyTimer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyTimer.class);
    private String userId = null;


    Timer timer;
    public NotifyTimer(String userId) {
        this.userId = userId;
    }

    @Override
    public void run() {

        try{
            LOGGER.debug("Executing timer task");

            Session s = WsTrackNotifyServer.sessions.get(userId);
            if(s!=null){
                //Solution
                String personToSend = WsTrackNotifyServer.persons.stream()
                        .filter(p -> p.getAge() > 18)
                        .sorted((p1, p2) -> p1.getAge() - p2.getAge())
                        .map(p -> p.getAge() + ":" + p.getName())
                        .collect(Collectors.joining(", "));
                s.getAsyncRemote().sendText("Notification service: \n persons in system: \n" + personToSend);
            } else {
                LOGGER.debug("cant  send update, no session");
            }
        } catch(Exception e){
            LOGGER.warn("Timer task failed",e);
        }

    }



}
