package com.liveperson.ws.demo.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.liveperson.test.common.json.JSON;
import com.liveperson.test.websocket.impl.JsonWsTester;
import com.liveperson.test.websocket.impl.WsTester;
import com.liveperson.ws.demo.server.dto.Person;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.liveperson.test.common.json.JSON.Matchers.any;
import static com.liveperson.test.common.json.JSON.Matchers.withProperty;
import static com.liveperson.ws.demo.server.utils.JacksonUtils.createPersonJson;

/**
 * Created by eladw on 10/25/2016.
 */
public class ConnectTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectTest.class);
    private static final String CONSUMER_URI = "ws://localhost:9090/ws-track/eladw?token=1234";



    @Test
    public void testSyncRecv(){
        try {
            WsTester<JsonNode> client = JsonWsTester.connect(CONSUMER_URI);
            Assert.assertTrue("Connection success", client!=null);
            // In order to create json msg fluently
            JsonNode json1 = JSON.object().put("name", "elad").put("age", 22);

            // sending msgs is easy
            client.send(json1);

            // wait until getting one msg
            JsonNode result = client.waitForMsgs(withProperty("name", "elad"));

            LOGGER.info("return val: " + result);
        }
        catch (Exception e) {
            Assert.fail("Failed start a client to url " + CONSUMER_URI);
        }

    }

    @Test
    public void testAsyncRecv(){
        try {
            WsTester<JsonNode> client = JsonWsTester.connect(CONSUMER_URI);
            Assert.assertTrue("Connection success", client!=null);
            // In order to create json msg fluently
            JsonNode json1 = JSON.object().put("name", "elad").put("age", 22);

            client.register(withProperty("name", "elad"), m -> {
                LOGGER.info("**** recv" + m.toString());
                return true;
            });

            // sending msgs is easy
            client.send(json1);

        }
        catch (Exception e) {
            Assert.fail("Failed start a client to url " + CONSUMER_URI);
        }

    }






    public String createPerson(){
        try {
            Person p = new Person();
            p.setAge(22);
            p.setName("eladw");
            List<String> msgs = new ArrayList<>();
            msgs.add("msg1");
            msgs.add("msg2");
            p.setMsgs(msgs);
            return createPersonJson(p);
        } catch (Exception e) {
            LOGGER.error("Failed creating person str");
            e.printStackTrace();
            return null;
        }

    }

}
