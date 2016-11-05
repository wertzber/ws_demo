package com.liveperson.ws.demo.server.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eladw on 10/30/16.
 */
public class JacksonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonUtils.class);

    public static ObjectMapper createObjectMapper(){
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); //can add fields for later versions
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);//don\t send null fields in order to save badwidth
        om.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,true); //in case of wrong enum put null and don't fail req
        return om;
    }


}
