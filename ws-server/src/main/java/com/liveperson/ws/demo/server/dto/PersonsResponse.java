package com.liveperson.ws.demo.server.dto;

import java.util.List;

/**
 * Created by eladw on 11/10/16.
 */
public class PersonsResponse {

    List<Person> personList;
    public PersonsResponse(List<Person> personList){
        this.personList = personList;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    @Override
    public String toString() {
        return "PersonsResponse{" +
                "personList=" + personList +
                '}';
    }
}
