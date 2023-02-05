package org.Group2; 
import java.io.IOException;
 
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class ContinuousIntegrationServer extends AbstractHandler {
    
    public void handle(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {
        // TODO Auto-generated method stub
        
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