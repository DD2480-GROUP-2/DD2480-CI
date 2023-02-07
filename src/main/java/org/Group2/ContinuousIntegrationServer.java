package org.Group2; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;  

public class ContinuousIntegrationServer extends AbstractHandler {
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        String req = request.getReader().readLine();
        JSONObject jsonRequest = new JSONObject(req);
        String repoSSHURL = getRepoURL(jsonRequest);
    }

    /**
     * Takes a JSON object from a github webhook request and returns the ssh url to the repository.
     * @param jsonRequest A request parsed as a JSONObject containing an object with a repository which has an ssh_url key.
     * @return A string containing the ssh-url to the repository
     */
    public String getRepoURL(JSONObject jsonRequest){
        try {
            String sshURL = jsonRequest.getJSONObject("repository").get("ssh_url").toString();
            return sshURL;
        } catch (JSONException je) {
            System.err.println("ssh_url to repository does not exist.");
            return null;
        }
    }


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
            

            Process process=Runtime.getRuntime().exec("git clone -b "+repoURL+ " ./clonedRepo");
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