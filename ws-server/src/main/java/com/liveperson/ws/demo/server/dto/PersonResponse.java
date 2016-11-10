package com.liveperson.ws.demo.server.dto;

import java.util.List;

/**
 * Created by eladw on 11/10/16.
 */
public class PersonResponse {

    Person person;
    public PersonResponse(Person person){
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPersonList(List<Person> personList) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "PersonsResponse{" +
                "person=" + person +
                '}';
    }
}
