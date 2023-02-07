package org.Group2; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.File;

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

    /**
     * Executes a given "sh" shell command in a specified directory.
     * @param command a string containing the shell command
     * @param path a string specifying the directory
     * @return exit value/code of the process
     */
    public int runCommand(String command, String path){
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        pb.directory(new File(path));

        try {
            Process process = pb.start();
            int exitValue = process.waitFor();
            return exitValue;
        } catch (Exception e){
            e.printStackTrace();
            return 1;
        }

    }

    /**
     * Compiles a Maven project located in a specified directory.
     * @param path a string specifying the directory
     * @return a boolean value which is true if the compile was successfil, and false otherwise
     */
    public boolean compileMvnProject(String path) {
        int compileStatus = this.runCommand("mvn clean compile", path);
        return compileStatus == 0;
    }

    // TODO!
    public void sendResponse(){}


    public static void main(String[] args) throws Exception {
        Server server = new Server(8002);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }

}