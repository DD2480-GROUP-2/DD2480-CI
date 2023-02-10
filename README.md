# DD2480-CI

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


## How compilation and testing has been implemented and unit-tested
The CI server handles compilation and testing with the use of Maven. This is done by starting a process and running the command "mvn clean compile" for compilation, and "mvn clean test" for testing. It is unit-tested by running either the compiling function, or testing function, on a sample project (which we know should or shouldn't compile and test correctly). The sample projects are located in the testBuilds-folder.

## How notifications have been implemented
In order to notify the user of the status of the commit, a status check is added next to the commit from "Our CI Tool". The status check is added by sending a post request to the github API by means of curl.

## Statement of Contributions  

Collectively we are proud of our work, especially considering that we were one member short, as she left the group before she could start working. We are very happy with how the project turned out and feel a great amount of pride, despite ecountering hurdles.

#### Felix Qvarfordt  
* Created a couple of helper methods to parse data from a request (and corresponding tests)  
* Set up the file structure in the beginning with a basic maven configuration
* Added an external CI tool to the project 
* Wrote the readme file and a paragraph on SEMAT
* Actively discussed and experimented to come up with ways of solving the task (together with everyone else)  

#### Arami Alfarhani
* Wrote the contents of the handle method
* Set up and handled webhooks and ngrok
* Handled direct communication with github API and webhooks
* Set up the eartly maven dependencies
* Actively discussed and experimented to come up with ways of solving the task (together with everyone else)  

#### Oskar Svanström
* Created the initial server class shell.
* Wrote the cloneRepo function along with helper functions and tests.
* Created some dummy tests.
* Wrote some of the documentation.
* Actively discussed and experimented to come up with ways of solving the task (together with everyone else)  

#### Jakob Ewaldsson
* Wrote code for the server's compiling and testing functions, along with helper methods.
* Wrote code for creating logs, and code for sending builds, along with helper methods.
* Wrote tests for these functions.
* Actively discussed and experimented to come up with ways of solving the task (together with everyone else) 
