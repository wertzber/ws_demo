/*
 * 12/27/15
 */
package com.liveperson.ws.demo.server;

import com.liveperson.ws.demo.server.auth.AuthData;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = null;
        token = request.getParameter("token");
        LOGGER.info("Token: {}", token);
        if(token==null){
            ((HttpServletResponse) response).sendError(HttpStatus.UNAUTHORIZED_401);
        } else {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String reqUri = httpRequest.getRequestURI();
            String userName = getUsernameFromUri(reqUri);
            // call my 3rd party authenticator
            AuthData authData = new AuthData(userName, token);
            httpRequest.getSession().setAttribute("authData", authData);
            chain.doFilter(request, response);
        }

    }

    private String getUsernameFromUri(String reqUri) {
        int indexStart = reqUri.indexOf("ws-track");
        String reqUriSubStr = reqUri.substring(indexStart + 8);
        int indexSlashStart = reqUriSubStr.indexOf("/");
        int indexSlashEnd = reqUriSubStr.substring(indexStart + 1).indexOf("/ ");
        String userName = "";
        if(indexSlashEnd > -1){
            userName = reqUriSubStr.substring(indexSlashStart + 1,indexSlashEnd - 1);
        } else {
            userName = reqUriSubStr.substring(indexSlashStart + 1);
        }
        LOGGER.info("Input uri {} , Output user {}", reqUri, userName);
        return userName;
    }

    @Override
    public void destroy() {

    }
}
