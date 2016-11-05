package com.liveperson.ws.demo.server.api.req;


import com.fasterxml.jackson.annotation.JsonValue;
import com.liveperson.ws.demo.server.api.AbstractBody;
import com.liveperson.ws.demo.server.api.resp.RespBody;


public interface ReqBody extends AbstractBody {
    public static class StringResp implements RespBody<ReqBody> {
        public final String msg;

        @JsonValue
        public String jsonValue() {
            return msg;
        }

        public StringResp(String msg) {
            this.msg = msg;
        }

        public StringResp() {
            this("");
        }
    }
}