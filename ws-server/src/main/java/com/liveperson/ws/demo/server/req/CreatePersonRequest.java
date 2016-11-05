package com.liveperson.ws.demo.server.req;

import java.util.List;

/**
 * Created by eladw on 10/30/16.
 */
public class CreatePersonRequest extends BasicReuqestMessgae {

    private String name;
    private int age;
    private List<String> msgs;

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
        return "CreatePersonRequest{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", msgs=" + msgs +
                '}';
    }
}
