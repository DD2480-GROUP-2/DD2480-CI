# A simple Java/Maven Continuous Integration server for GitHub

This is a simple continuous integration server that builds and tests Maven projects and interacts with GitHub to report the status of the build.

## Dependencies

* Apache Maven 3.8.7
* JDK 17

All other dependencies are handled my Maven and are defined in the pom.xml file.

## Install and run the server

To install and run the server, you simply need to clone this repository, compile the code and run which can be done using these commands:  
```
git clone git@github.com:DD2480-GROUP-2/DD2480-CI.git
cd DD2480-CI
mvn clean compile exec:java
```

Now the server should be up and running on port 8002.  

Finally you can go to your GitHub repository and navigate to Settings -> Webhooks and add a new webhook with the URL to your server. The content type should be chosen as application/json and currently, the server only handles push requests so if any other type of requests are sent to the server they will be ignored. 

### Server set up

If your system sits behind a firewall blocking incomming requests we recommend using ngrok to tunnel requests through to the server.

## Statement of Contributions
