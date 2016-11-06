package com.liveperson.ws.demo.server.handler;

import com.liveperson.ws.demo.server.api.remote.Remote;
import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.req.RequestMsg;
import com.liveperson.ws.demo.server.api.resp.RespBody;
import com.liveperson.ws.demo.server.req.CreatePerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.UUID;

/**
 * Created by eladw on 11/5/2016.
 */
public class PersonHandler  {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonHandler.class);

    public static void handle(Remote remote, RequestMsg<CreatePerson> requestMsg){
        if(requestMsg.getBody().getAge()> 100){
           LOGGER.info("######## you are old ##########");
        }
        String uuid = UUID.randomUUID().toString();
        //TODO: impl requestMsg.response(Response.Status.ACCEPTED, "");
    }
}
