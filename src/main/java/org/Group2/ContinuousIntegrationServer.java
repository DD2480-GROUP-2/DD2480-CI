package org.Group2; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;

import com.roxstudio.utils.CUrl;  


public class ContinuousIntegrationServer extends AbstractHandler {
    static String token = "YOUR-TOKEN";

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code
        String req = "[" + request.getReader().readLine() + "]";
        JSONArray jArray = new JSONArray(req);  

        getRepoURL(jArray);
        sendResponse(true,true, jArray);
    }

    // TODO!
    public void getRepoURL(JSONArray jArray){}


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


    /**
     * Sends a post-request to the github api setting the check-status of the commit.
     * @param build status of the build (success/failure)
     * @param tests status of the tests (success/failure)
     * @param jArray the json-data acquired from the request
    */
    public void sendResponse(boolean build, boolean tests, JSONArray jArray) {
        String state = build && tests ? "success" : "failure";
        String description;
        if (build && tests) {description = "Everything succeeded";}
        else if (build && (!tests)) {description = "Tests failed";}
        else {description = "Build and tests failed";}

        String sha = jArray.getJSONObject(0).getString("after");
        String ownerRepo = jArray.getJSONObject(0).getJSONObject("repository").getString("full_name");
        CUrl post = new CUrl("https://api.github.com/repos/" + ownerRepo + "/statuses/" + sha)
                            .header("Accept: application/vnd.github+json")
                            .header("Authorization: Bearer " + token)
                            .header("X-GitHub-Api-Version: 2022-11-28")
                            .data("{\"state\":\"" + state + "\",\"description\":\"" + description + "\",\"context\":\"Our CI utility\"");
        post.exec();

    }

    /**
     * Can be modified to trigger test errors.
     * @return for test passed should return 123
     */
    public static int dummyFunction(){
        return 123;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8002);
        server.setHandler(new ContinuousIntegrationServer()); 
        server.start();
        server.join();
    }

}