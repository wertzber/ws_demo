package com.liveperson.ws.demo.server.api.types;

import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.resp.RespBody;

/**
 * Created by eladw on 11/5/2016.
 */
public class GetClock implements ReqBody {

    public static class Response implements RespBody<GetClock> {
        /**
         * Returns currentTime in milliseconds since 1970
         */
        public final long currentTime;

        public Response(long currentTime) {
            this.currentTime = currentTime;
        }

        public Response() {
            this(System.currentTimeMillis());
        }
    }
}
