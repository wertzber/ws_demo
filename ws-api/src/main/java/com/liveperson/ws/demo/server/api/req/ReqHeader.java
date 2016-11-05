package com.liveperson.ws.demo.server.api.req;



import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "type")
public interface ReqHeader {
}
