import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.Group2.ContinuousIntegrationServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;

public class ContinousIntegrationServerTests {
    @Test
    public void TestCloneRepo() {
        try{
            ContinuousIntegrationServer.cloneRepo("main https://github.com/DD2480-GROUP-2/DD2480-CI.git");
            File pom = new File("./clonedRepo/pom.xml");
            File readme = new File("./clonedRepo/README.md");

            assertTrue(pom.exists(), "Error! pom.xml should exist!");
            assertTrue(readme.exists(), "Error! README.md should exist!");


        } catch(Exception e) {
            assertTrue(false, "Error! Failed to clone repository");
        }
        
    }
}

