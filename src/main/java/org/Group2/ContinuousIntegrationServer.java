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


    /**
     * Recursively deletes all files in the repo
     * @param file
     */
    public static void deleteDirectory(File file) {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
    }
    /**
     * Clones a github repository to the folder ./clonedRepo that can later be built and tested.
     * @param repoURL the destination of the repository to be cloned
     * @throws Exception if cloning failed
    */
    public static void cloneRepo(String repoURL) throws Exception {
        try {
            File file = new File("./clonedRepo");
            if(file.isDirectory()){
                deleteDirectory(file);
                file.delete();
            }
            

            Process process=Runtime.getRuntime().exec("git clone "+repoURL+ " ./clonedRepo");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new Exception("Failed to clone "+repoURL);
        }

    }

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