# A simple Java/Maven Continuous Integration server for GitHub

This is a simple continuous integration server that builds and tests Maven projects and interacts with GitHub to report the status of the build. Currently only UNIX based systems are supported.

## Dependencies

* Apache Maven 3.8.7
* JDK 17

All other dependencies are handled my Maven and are defined in the pom.xml file.

## Install and run the server

To install and run the server, you first need to clone this repository and add your GitHub authorization token to the server.  
```
git clone git@github.com:DD2480-GROUP-2/DD2480-CI.git
```

Then, navigate to the project folder/src/org.Group2/ and open up the source code for the Continous Integration Server. Here you can add your GitHub authorization token in the static variable named "token". This is needed for the server to be able to update commit statuses on GitHub.  

Now we just need to open up a terminal window and navigate to the project folder and build and run the code with the following command:

```
mvn clean compile exec:java
```

Now the server should be up and running on port 8002.  

Finally you can go to your GitHub repository and navigate to Settings -> Webhooks and add a new webhook with the URL to your server. The content type should be chosen as application/json and currently, the server only handles push requests so if any other type of requests are sent to the server they will be ignored. 

## How to test

To run the servers own tests you simply need to clone the repository, complile the project and run the tests, this can be done with the following commands:
```
git clone git@github.com:DD2480-GROUP-2/DD2480-CI.git
cd DD2480-CI
mvn clean compile test
```

## Server set up

If your system sits behind a firewall blocking incomming requests we recommend using ngrok to tunnel requests through to the server.

## Build logs  
Build and test logs are generated after each commit, to access these you can go to your visit your servers address your browser and you will be presented with a list of all generated log files. Note that the server is only listening to port 8002 so we strongly recommend using ngrok for this feature to work. Otherwise you would have to send a GET request on port 8002 to recieve the index.html file. 

## Documentation  
To access the documentation, open the index file in the folder "apidocs" with a web browser.
