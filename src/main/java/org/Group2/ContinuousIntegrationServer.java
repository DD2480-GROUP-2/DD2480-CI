package org.Group2; 

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.io.FileWriter;
import java.io.BufferedWriter;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONException;
import org.json.JSONObject;
import com.roxstudio.utils.CUrl;  


/**
 * A small lightweight handler for a server which implements core CI server functionality including webhook communication over http, 
 * cloning, building and testing of a github repository and then returning responses to the original Github commit.
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    static String token = "YOUR-TOKEN";

    /**
     * Main handle function.
     * Is called for every incoming http request. The function makes sure that the request is from github webhooks and then 
     * continues calling every step of the CI routine.
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        //Check if request is from webhook
        if (request.getHeader("User-Agent").split("/")[0].equals("GitHub-Hookshot"))
        {
          String req = request.getReader().readLine();
          JSONObject jsonRequest = new JSONObject(req);
          String repoHTTPSURL = getRepoURL(jsonRequest);
          String branchName = getPushedBranch(jsonRequest);
          try {cloneRepo(branchName + " " + repoHTTPSURL);
          } catch (Exception e) {
              throw new ServletException("Clone not successful");
          }

          sendResponse(compileMvnProject("./clonedRepo"),testMvnProject("./clonedRepo"), jsonRequest);
        }
    }

    /**
     * Take a request from a git webhook and return a string with the branch the action took place on.
     * @param jsonRequest A request from a github webhook parsed as a JSONObject.
     * @return A string with the name of the branch.
     */
    public String getPushedBranch(JSONObject jsonRequest) {
        try {
            var branchRef = jsonRequest.get("ref").toString().split("/");
            return branchRef[branchRef.length - 1];
        } catch (JSONException je) {
            System.err.println("Branch name of the event not found");
            return null;
        }

    }

    /**
     * Takes a JSON object from a github webhook request and returns the ssh url to the repository.
     * @param jsonRequest A request parsed as a JSONObject containing an object with a repository which has an ssh_url key.
     * @return A string containing the ssh-url to the repository
     */
    public String getRepoURL(JSONObject jsonRequest){
        try {
            String httpsURL = jsonRequest.getJSONObject("repository").get("clone_url").toString();
            return httpsURL;
        } catch (JSONException je) {
            System.err.println("clone_url to repository does not exist.");
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
     * @return a boolean value which is true if the compile was successful, and false otherwise
     */
    public boolean compileMvnProject(String path) {
        int compileStatus = this.runCommand("mvn clean compile", path);
        return compileStatus == 0;
    }

    /**
     * Runs a Maven project test-suite located in a specified directory. If the project is not yet compiled, the function will also compile.
     * @param path a string specifying the directory
     * @return a boolean value which is true if the tests were successful, and false otherwise
     */
    public boolean testMvnProject(String path) {
        int testStatus = this.runCommand("mvn clean test", path);
        return testStatus == 0;
    }

        /**
     * Appends a string to a specified file.
     * @param file the File to write to
     * @param stringToWrite the string to write
     */
    public void writeToFile(File file, String stringToWrite) {
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(stringToWrite);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates headers of build log-file, including git-commit sha, git-pusher email, and date/time
     * @param logInfo a JSONObject acquired from a http request 
     * @return the created File
     */
    public File createBuildLogFile(JSONObject logInfo) {
        if (logInfo.has("after")) {
            File newLogFile = new File("./builds/" + logInfo.getString("after") + ".log");
            LocalDateTime timeObj = LocalDateTime.now();
            String logHeader = "Commit ID: " + logInfo.getString("after") + "\nCommitter: " + logInfo.getJSONObject("pusher").getString("email") + "\nDate: " + timeObj + "\nBuild log:\n";
            writeToFile(newLogFile, logHeader);
            return newLogFile;
        } else {
            return null;
        }
    }


    /**
     * Sends a post-request to the github api setting the check-status of the commit.
     * @param build status of the build (success/failure)
     * @param tests status of the tests (success/failure)
     * @param jObject the json-data acquired from the request
    */
    public void sendResponse(boolean build, boolean tests, JSONObject jObject) {
        String state = build && tests ? "success" : "failure";
        String description;
        if (build && tests) {description = "Everything succeeded";}
        else if (build && (!tests)) {description = "Tests failed";}
        else {description = "Build and tests failed";}

        String sha = jObject.getString("after");
        String ownerRepo = jObject.getJSONObject("repository").getString("full_name");
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