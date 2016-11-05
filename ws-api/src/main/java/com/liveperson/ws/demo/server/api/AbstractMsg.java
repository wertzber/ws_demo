package com.liveperson.ws.demo.server.api;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.liveperson.ws.demo.server.api.req.WsRequestMsg;
import com.liveperson.ws.demo.server.api.resp.WsResponseMsg;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes({@JsonSubTypes.Type(WsRequestMsg.class), @JsonSubTypes.Type(WsResponseMsg.class)})
public interface AbstractMsg {

}