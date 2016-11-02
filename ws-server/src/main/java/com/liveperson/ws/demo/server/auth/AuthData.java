/*
 * 12/27/15
 */
package com.liveperson.ws.demo.server.auth;

/**
 *
 */
public class AuthData {
    public String userName = null;
    public String token = null;
    public final Long expireTime = System.currentTimeMillis() + 60000;

    public AuthData(String userName, String token) {
        this.token = token;
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "userName='" + userName + '\'' +
                ", token='" + token + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
