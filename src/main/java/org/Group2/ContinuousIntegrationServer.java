package org.Group2; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ContinuousIntegrationServer extends AbstractHandler {
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

    }

    // TODO!
    public void parseJSON(){}

    // TODO!
    public void getRepoURL(){}

    // TODO!
    public void cloneRepo(){}

    // TODO!
    public void buildAndTestRepo(){} // Might be two separate methods?

    // TODO!
    public void sendResponse(){}


    public static void main(String[] args) throws Exception {
        Server server = new Server(8002);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }

}