package com.liveperson.ws.demo.server.req;

import com.liveperson.ws.demo.server.api.req.ReqBody;
import com.liveperson.ws.demo.server.api.resp.RespBody;

import java.util.List;

/**
 * Created by eladw on 10/30/16.
 */
public class CreatePerson implements ReqBody {

    private String name;
    private int age;
    private List<String> msgs;

    public CreatePerson() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getMsgs() {
        return msgs;
    }


    public void setMsgs(List<String> msgs) {
        this.msgs = msgs;
    }

    @Override
    public String toString() {
        return "CreatePerson{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", msgs=" + msgs +
                '}';
    }

    public static class Response implements RespBody<CreatePerson> {
        /**
         * Returns currentTime in milliseconds since 1970
         */
        public final long currentTime;
        public final String userId;

        public Response(String userId) {
            this.currentTime = System.currentTimeMillis();
            this.userId = userId;
        }
        public Response(){this("aaa"); }

    }


}
