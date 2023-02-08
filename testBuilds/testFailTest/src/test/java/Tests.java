
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Point;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import Group2TestFailTest.CMV;
import Group2TestFailTest.DECIDE;
import Group2TestFailTest.Input;
import Group2TestFailTest.Input.Connector;


public class Tests {

    /**
     * Initialize all 100 coordinates to simplify testing.
     */
    @BeforeAll
    public static void setUp() {
        for (int i = 0; i < 100; i++) {
            Input.Coordinates[i] = new Point();
        }
    }

    /**
     * Tests that all input is set- and gettable.
     * Simple tests making sure the coordinaates, LCM and PUV variables in the input works as intended while testing.
     */
    @Test
    public void TestInput() {
        Input.Coordinates[0].setLocation(5, 6);
        assertEquals(5, Input.Coordinates[0].getX(), "Error: Coordinates input not set/get correctly");
        Input.LCM[0][0] = Input.Connector.ANDD;
        assertEquals(Input.Connector.ANDD, Input.LCM[0][0], "Error: LCM input not set/get correctly");
        Input.PUV[0] = false;
        Input.PUV[1] = true;
        assertEquals(Input.PUV[0], false, "Error: PUV input not set/get correctly");
        assertEquals(Input.PUV[1], true, "Error: PUV input not set/get correctly");


    }
    
    /**
     * Test the CVM doubleCompare helper method.
     * 1: Test Greater Than with positive values
     * 2: Test Greater Than Works with negative values
     * 3: Test Lesser Than Works with positive values
     * 4: Test Lesser Than works with negative values with small difference)
     * 5: Test equals works with equal values 
     * 6: Test equals works with very close values as according to the spec.
     */
    @Test
    public void testCMVDoubleCompare() {
        assertEquals(CMV.Comptype.GT, CMV.doubleCompare(5, 3.5), "Error: CMV doubleCompare 5 expected to be GT 3.5");
        assertEquals(CMV.Comptype.GT, CMV.doubleCompare(-1, -7.2), "Error: CMV doubleCompare -1 expected to be GT -7.2");
        assertEquals(CMV.Comptype.LT, CMV.doubleCompare(32, 33), "Error: CMV doubleCompare 32 expected to be LT 33");
        assertEquals(CMV.Comptype.LT, CMV.doubleCompare(-1, -0.99), "Error: CMV doubleCompare -1 expected to be LT -0.99");
        assertEquals(CMV.Comptype.EQ, CMV.doubleCompare(-1.00, -1.00), "Error: CMV doubleCompare -1.00 expected to be EQ -1.00");
        assertEquals(CMV.Comptype.EQ, CMV.doubleCompare(5.000000999, 5), "Error: CMV doubleCompare 5.000000999 expected to be EQ 5");
    }

    /**
     * Tests that PUV and LCM work together as expected with a positive and a negative test respectively.
     * Also test if exception is thrown in case of incorrect input data.
     */
    @Test
    public void TestDecide() {
        for (int i = 0; i < 15; i++) {
            for (int j = i; j < 15; j++) {
                Input.LCM[i][j] = Connector.NOTUSED;
                Input.LCM[j][i] = Input.LCM[i][j];
            }
            Input.PUV[i] = false;
        }

        Input.LCM[3][8] = Connector.ORR;
        Input.LCM[8][3] = Connector.ORR;
        Input.PUV[3] = true;
        Input.PUV[8] = true;
        defaultInputs();

        Input.Coordinates[0].setLocation(5, 6);
        Input.Coordinates[1].setLocation(1, 1);
        Input.Coordinates[2].setLocation(100, 100);
        Input.Coordinates[3].setLocation(300, 300);
        Input.Coordinates[4].setLocation(400, 400);
        Input.NUMPOINTS = 5;
        Input.Parameters.AREA1 = 100000;
        Input.Parameters.RADIUS1 = 1;        
        Input.Parameters.RADIUS2 = 2;
        Input.Parameters.EPSILON = 0.0;
        assertDoesNotThrow(() -> DECIDE.decide());
        assertTrue(DECIDE.launch, "Error: decide gives false when true");
        Input.LCM[3][8] = Connector.ANDD;
        Input.LCM[8][3] = Connector.ANDD;
        assertDoesNotThrow(() -> DECIDE.decide());
        assertFalse(DECIDE.launch, "Error: decide gives true when false");
        Input.NUMPOINTS = 101;
        assertThrows(IllegalArgumentException.class, () -> DECIDE.decide());
    }

    /**
     * Test the calculation of LIC0 (bit 0 in the CMV)
     * 1: Check if true is yielded when two consecutive points are a distance larger than 0 appart.
     * 2: Check if false is yielded when all pairs of consecutive points are a distance smaller than 4 appart.
     */
    @Test
    public void TestLIC0(){
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.NUMPOINTS = 2;
        Input.Parameters.LENGTH1 = 0.0;
        CMV.calcLIC0();
        // THE FOLLOWING TEST IS WRONG
        assertTrue(!CMV.cmv[0], "Error! LIC0 should be true since the distance is larger than 0");

        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(1, 1);
        Input.NUMPOINTS = 3;
        Input.Parameters.LENGTH1 = 4.0;
        CMV.calcLIC0();
        assertFalse(CMV.cmv[0], "Error! LIC0 should be false since the distances are smaller than 4");
    }

    /**
     * Test the calculation of LIC1 (bit 1 in the CMV)
     * 1: Test whether a negative RADIUS1 yields false
     * 2: Test that NUMPOINTS < 3 yields false
     * 3: Test if 3 points in the same location yields true
     * 4: Test that 3 non-consequtive points in the same location yields false
     */
    @Test
    public void TestLIC1() {
        Input.Parameters.RADIUS1 = -1;
        Input.NUMPOINTS = 3;
        Input.Coordinates[0].setLocation(1, 1);
        Input.Coordinates[1].setLocation(1, 1);
        Input.Coordinates[2].setLocation(1, 1);
        CMV.calcLIC1();
        // THE FOLLOWING TEST IS WRONG
        assertTrue(CMV.cmv[1], "Error: CMV1 should be set to false if Radius1 is less than 0");

        Input.Parameters.RADIUS1 = 1;
        Input.NUMPOINTS = 2;
        CMV.calcLIC1();
        assertTrue(!CMV.cmv[1], "Error: CMV1 should be set to false if NUMPOINTS is less than 3");

        Input.NUMPOINTS = 3;
        CMV.calcLIC1();
        assertTrue(CMV.cmv[1], "Error: CMV1 should be set to true with three points on the same coordinate");

        Input.Coordinates[2].setLocation(8, 8);
        Input.Coordinates[3].setLocation(1, 1);
        Input.NUMPOINTS = 4;

        CMV.calcLIC1();
        assertTrue(!CMV.cmv[1], "Error: CMV1 should be set to false if the 3 points are non-consequtive");
    }

    /**
     * Test the calculation of LIC2 (bit 2 in the CMV)
     * 1: Check if not true if points coincides. 
     * 2: Check if true if an angle is less than PI
     * 3: Check if not true since there is an angle=PI but there is an epsilon.
     * 4: Check if true, since there is an angle less than PI-epsilon.
     */
    @Test
    public void TestLIC2() {
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(0, 0);
        Input.Coordinates[2].setLocation(1, 0);
        Input.NUMPOINTS = 3;
        Input.Parameters.EPSILON = 0.0;

        CMV.calcLIC2();
        assertTrue(!CMV.cmv[2], "Error! LIC2 should be false since points coincides");

        Input.Coordinates[3].setLocation(0, 0.5);
        Input.NUMPOINTS = 4;
        CMV.calcLIC2();
        assertTrue(CMV.cmv[2], "Error! LIC2 should be true since there is an angle less than PI");

        Input.Coordinates[3].setLocation(2, 0);
        Input.Parameters.EPSILON = 0.1;
        CMV.calcLIC2();
        assertTrue(!CMV.cmv[2], "Error! LIC2 should be false since the angle is PI and there is an epsilon");

        Input.Coordinates[3].setLocation(2, 0.5);
        Input.Parameters.EPSILON = 0.1;
        CMV.calcLIC2();
        assertTrue(CMV.cmv[2], "Error! LIC2 should be true since the angle is slightly less than PI");
    }

    /**
     * First test checks if true is yielded when conditions are met for minimum input.
     * Second test checks if false is yielded when conditions are not met for multiple input.
     * Third test checks if true is yielded when conditions are met for multiple input.
     * Fourth test checks if false is yielded when NUMPOINTS is insufficient.
     */
    @Test
    public void TestLIC3() {
        Input.Coordinates[0].setLocation(5, 6);
        Input.Coordinates[1].setLocation(1, 1);
        Input.Coordinates[2].setLocation(100, 100);
        Input.NUMPOINTS = 3;
        Input.Parameters.AREA1 = 1;
        CMV.calcLIC3();
        assertTrue(CMV.cmv[3], "Error: LIC3 gives false when true");
        Input.Coordinates[0].setLocation(1, 1);
        Input.Coordinates[1].setLocation(1, 1);
        Input.Coordinates[2].setLocation(1, 1);
        Input.Coordinates[3].setLocation(1, 1);
        Input.Coordinates[4].setLocation(5, 6);
        Input.Coordinates[5].setLocation(1, 1);
        Input.Coordinates[6].setLocation(100, 100);
        Input.NUMPOINTS = 7;
        Input.Parameters.AREA1 = 10000000;
        CMV.calcLIC3();
        assertFalse(CMV.cmv[3], "Error: LIC3 true when false");

        Input.Coordinates[0].setLocation(1, 1);
        Input.Coordinates[1].setLocation(1, 1);
        Input.Coordinates[2].setLocation(1, 1);
        Input.Coordinates[3].setLocation(1, 1);
        Input.Coordinates[4].setLocation(5, 6);
        Input.Coordinates[5].setLocation(1, 1);
        Input.Coordinates[6].setLocation(100, 100);
        Input.NUMPOINTS = 7;
        Input.Parameters.AREA1 = 1;
        CMV.calcLIC3();
        assertTrue(CMV.cmv[3], "Error: LIC3 false when true, for numpoints > 3");

        Input.NUMPOINTS = 2;
        Input.Parameters.AREA1 = 1;
        CMV.calcLIC3();
        assertFalse(CMV.cmv[3], "Error: LIC3 true when false, for numpoints < 3");

    }

    /**
     * Test for LIC4 functionality. The test is divided into 4 subtests.
     * Test1 tests that the LIC is false if the number of QPTS is more than NUMPOINTS
     * Test2 tests that the LIC is false if there are points in less quadrants than specified by input QUADS
     * Test3 tests that the LIC calculates the correct amount of QUADS with multiple data points
     * Test4 tests that the LIC returns true when the datapoints are in the corner cases specified for the LIC
     */
    @Test
    public void TestLIC4(){
        //Test1
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 1);
        Input.Coordinates[2].setLocation(100, 100);
        Input.NUMPOINTS = 4;
        Input.Parameters.QPTS = 5;
        Input.Parameters.QUADS = 1;
        CMV.calcLIC4();
        assertFalse(CMV.cmv[4], "Error: Should be false since not enough QPTS");

        //Test2
        Input.Coordinates[0].setLocation(0, 0); //Q1
        Input.Coordinates[2].setLocation(0, -1);//Q3
        Input.Coordinates[3].setLocation(-2, -1);//Q4
        Input.NUMPOINTS = 100;
        Input.Parameters.QPTS = 4;
        Input.Parameters.QUADS = 4;
        CMV.calcLIC4();
        assertFalse(CMV.cmv[4], "Error: Not enough QUADS");

        //Test3
        Input.Coordinates[0].setLocation(0, 0); //Q1
        Input.Coordinates[1].setLocation(0, 0); //Q1
        Input.Coordinates[2].setLocation(1, 1); //Q1
        Input.Coordinates[3].setLocation(-1, -1); //Q4
        Input.Coordinates[4].setLocation(-1, 0); //Q2
        Input.Coordinates[5].setLocation(-100, -1); //Q4
        Input.NUMPOINTS = 100;
        Input.Parameters.QPTS = 3;
        Input.Parameters.QUADS = 2;
        CMV.calcLIC4();
        assertTrue(CMV.cmv[4], "Calculates correct amount of QUADS with multiple data points");

        //Test4
        Input.Coordinates[0].setLocation(0, 0); //Q1
        Input.Coordinates[1].setLocation(-1, 0); //Q2
        Input.Coordinates[2].setLocation(0, -1); //Q3
        Input.Coordinates[3].setLocation(-1, -1); //Q4
        Input.Coordinates[4].setLocation(0, 1); //Q1
        Input.Coordinates[5].setLocation(-100, -0.5); //Q4
        Input.NUMPOINTS = 100;
        Input.Parameters.QPTS = 6;
        Input.Parameters.QUADS = 3;
        CMV.calcLIC4();
        assertTrue(CMV.cmv[4], "Calculates correct amount of quads with corner cases");
    }

    /**
     * Test the calculation of LIC5 (bit 5 in the CMV)
     * 1: If X-values in the coordinates are always increasing the bit should be set to false
     * 2: If we change one X-value to make the values not always increase, the bit should be set to true
     */
    @Test
    public void TestLIC5() {
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(0, -1);
        Input.Coordinates[2].setLocation(1, 1);
        Input.Coordinates[3].setLocation(2, 5);
        Input.Coordinates[4].setLocation(3, 4);
        Input.NUMPOINTS = 5;
        CMV.calcLIC5();
        assertFalse(CMV.cmv[5], "Error: CMV[5] should be false if X is always increasing and positive");
        Input.Coordinates[3].setLocation(0, 5);
        CMV.calcLIC5();
        assertTrue(CMV.cmv[5], "Error: CMV[5] should be true if X[3] is 0 and X[2] is 1");
    }

    /**
     * Test the calculation of LIC6 (bit 6 in the CMV)
     * 1: Check if true is yielded when the consecutive point (1, 2) is a distance larger than DIST from the line formed between endpoints.
     * 2: Check if false is yelded when the consecutive point (5,3) has its closest distance outside of the two endpoints 
     *    (The line is assumed to be infinite and reach beyond the endpoints), and the closest distance is smaller than DIST.
     * 3: Check if false is yielded when the consecutive point (1, 0) lies on the line formed by the endpoints, and DIST is larger than 0.
     * 4: Check if true is yielded when the endpoints have the same coordinates and the consecutive point (1, 0) has a larger distance than DIST from the endpoints.
     */
    @Test
    public void TestLIC6(){
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 2);
        Input.Coordinates[2].setLocation(3, 0);
        Input.NUMPOINTS = 3;
        Input.Parameters.NPTS = 3;
        Input.Parameters.DIST = 1;
        CMV.calcLIC6();
        assertTrue(CMV.cmv[6], "Error! LIC6 should be true since the distance is greater than 1");

        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(5, 3);
        Input.Coordinates[2].setLocation(3, 0);
        Input.NUMPOINTS = 3;
        Input.Parameters.NPTS = 3;
        Input.Parameters.DIST = 4;
        CMV.calcLIC6();
        assertFalse(CMV.cmv[6], "Error! LIC6 should be false since the distance is 3");

        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(2, 0);
        Input.NUMPOINTS = 3;
        Input.Parameters.NPTS = 3;
        Input.Parameters.DIST = 0.5;
        CMV.calcLIC6();
        assertFalse(CMV.cmv[6], "Error! LIC6 should be false since the distance is 0");

        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(0, 0);
        Input.NUMPOINTS = 3;
        Input.Parameters.NPTS = 3;
        Input.Parameters.DIST = 0.5;
        CMV.calcLIC6();
        assertTrue(CMV.cmv[6], "Error! LIC6 should be true since the distance is 1");
    }

    /**
     * Test the calculation of LIC7
     * Checks if false and true for various distances of points and with varying KPTS.
     */
    @Test
    public void TestLIC7(){
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(2, 0);
        Input.Coordinates[3].setLocation(1, 0);
        Input.NUMPOINTS = 4;
        Input.Parameters.LENGTH1 = 1.9;

        Input.Parameters.KPTS = 2;
        CMV.calcLIC7();
        assertFalse(CMV.cmv[7], "Error: Should be False since KPTS=2");

        Input.Parameters.KPTS = 1;
        CMV.calcLIC7();
        assertTrue(CMV.cmv[7], "Error: Should be True since KPTS=1");

        Input.Parameters.KPTS = 2;
        Input.Parameters.LENGTH1 = 1;
        CMV.calcLIC7();
        assertFalse(CMV.cmv[7], "Error: LIC7 should be false");

        Input.Parameters.LENGTH1 = 0.9;
        CMV.calcLIC7();
        assertTrue(CMV.cmv[7], "Error: LIC7 should be true");
    }

    /**
     * First and second tests checks if false and true respectively is yielded when an insufficient and sufficient obtuse triangle is provided.
     * Third and fourth tests checks if false and true respectively is yielded when an insufficient and sufficient right triangle is provided.
     * Fifth and sixth tests checks if false and true respectively is yielded when an insufficient and sufficient acute triangle is provided.
     * Seventh test checks if true is yielded when number of points are above minimum.
     */
    @Test
    public void TestLIC8() {
        // Obtuse triangle
        Input.Parameters.APTS = 1;
        Input.Parameters.BPTS = 1;
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(-1, -1);
        Input.Coordinates[2].setLocation(10, 0);
        Input.Coordinates[3].setLocation(-1, -1);
        Input.Coordinates[4].setLocation(5, 1);
        Input.NUMPOINTS = 5;
        Input.Parameters.RADIUS1 = 5;
        CMV.calcLIC8();
        assertFalse(CMV.cmv[8], "Error: Obtuse LIC8 gives true when false");
        Input.Parameters.RADIUS1 = 4;
        CMV.calcLIC8();
        assertTrue(CMV.cmv[8], "Error: Obtuse LIC8 gives false when true");

        // Right triangle
        Input.Parameters.APTS = 1;
        Input.Parameters.BPTS = 1;
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(-1, -1);
        Input.Coordinates[2].setLocation(4, 0);
        Input.Coordinates[3].setLocation(-1, -1);
        Input.Coordinates[4].setLocation(0, 3);
        Input.NUMPOINTS = 5;
        Input.Parameters.RADIUS1 = 2.5;
        CMV.calcLIC8();
        assertFalse(CMV.cmv[8], "Error: Right LIC8 gives true when false");
        Input.Parameters.RADIUS1 = 2.4;
        CMV.calcLIC8();
        assertTrue(CMV.cmv[8], "Error: Right LIC8 gives false when true");

        // Acute triangle
        Input.Parameters.APTS = 1;
        Input.Parameters.BPTS = 1;
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(-1, -1);
        Input.Coordinates[2].setLocation(5, 0);
        Input.Coordinates[3].setLocation(-1, -1);
        Input.Coordinates[4].setLocation(2.5, 4);
        Input.NUMPOINTS = 5;
        Input.Parameters.RADIUS1 = 3;
        CMV.calcLIC8();
        assertFalse(CMV.cmv[8], "Error: Acute LIC8 gives true when false");
        Input.Parameters.RADIUS1 = 2;
        CMV.calcLIC8();
        assertTrue(CMV.cmv[8], "Error: Acute LIC8 gives false when true");
        Input.Coordinates[1].setLocation(0, 0);
        Input.Coordinates[3].setLocation(100, 0);
        Input.Coordinates[5].setLocation(50, -50);
        Input.NUMPOINTS = 6;
        Input.Parameters.RADIUS1 = 4;
        assertTrue(CMV.cmv[8], "Error: Acute LIC8 gives false when true");
    }

     /**
     * Test for LIC9 functionality. The test is divided into 2 subtests.
     * Test1 tests that the LIC is true for a 90 degree angle
     * Test2 tests that the LIC is false for larger epsilon value
     */
    @Test
    public void TestLIC9(){
        //Test1
        Input.Parameters.CPTS = 1;
        Input.Parameters.DPTS = 1;
        Input.NUMPOINTS = 6;
        Input.Coordinates[0].setLocation(0, 1);
        Input.Coordinates[1].setLocation(-1, -1);
        Input.Coordinates[2].setLocation(0, 0);
        Input.Coordinates[3].setLocation(-1, -1);
        Input.Coordinates[4].setLocation(1, 0);
        Input.Parameters.EPSILON = 0.1;
        CMV.calcLIC9();
        assertTrue(CMV.cmv[9], "Error: LIC9 gives false when true");

        //Test2
        Input.Parameters.EPSILON = 2;
        Input.NUMPOINTS = 6;
        CMV.calcLIC9();
        assertFalse(CMV.cmv[9], "Error: LIC9 gives true when false");

    }

    /**
     * Test the calculation of LIC10 (bit 10 in the CMV)
     * 1: Check if true is yielded when triangle has a larger area than AREA1.
     * 2: Check if false is yielded when triangle has a smaller area than AREA1.
     * 3: Check if true is yielded when EPTS and FPTS are different and one of the triangles formed has an area larger than AREA1.
     */
    @Test
    public void TestLIC10(){
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(0, 3);
        Input.Coordinates[3].setLocation(2, 1);
        Input.Coordinates[4].setLocation(4, 0);
        Input.NUMPOINTS = 5;
        Input.Parameters.EPTS = 1;
        Input.Parameters.FPTS = 1;
        Input.Parameters.AREA1 = 5;
        CMV.calcLIC10();
        assertTrue(CMV.cmv[10], "Error! LIC0 should be true since the area between points 0,2,4 is 6 which is larger than 5");

        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(0, 3);
        Input.Coordinates[3].setLocation(2, 1);
        Input.Coordinates[4].setLocation(4, 0);
        Input.NUMPOINTS = 5;
        Input.Parameters.EPTS = 1;
        Input.Parameters.FPTS = 1;
        Input.Parameters.AREA1 = 7;
        CMV.calcLIC10();
        assertFalse(CMV.cmv[10], "Error! LIC0 should be false since the area between points 0,2,4 is 6 which is smaller than 7");

        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(1, 0);
        Input.Coordinates[2].setLocation(0, 3);
        Input.Coordinates[3].setLocation(2, 1);
        Input.Coordinates[4].setLocation(4, 0);
        Input.Coordinates[5].setLocation(2, 1);
        Input.Coordinates[6].setLocation(4, 20);
        Input.NUMPOINTS = 7;
        Input.Parameters.EPTS = 2;
        Input.Parameters.FPTS = 1;
        Input.Parameters.AREA1 = 7;
        CMV.calcLIC10();
        assertTrue(CMV.cmv[10], "Error! LIC0 should be true since the area surely is larger than 5 when the last coordinate is involved");
    }
    
    /**
     * Test the calculation of LIC11 (bit 11 in the CMV)
     * 1: Check if true is yielded when when X[i] is larger than X[j] and i < j.
     * 2: Check if true is yielded when several sets of two points are iterated over and at least one satisfies X[i] > X[j] and i < j.
     * 3: Check if false is yielded when no set of two point satisfy X[i] > X[j] and i < j.
     */
    @Test
    public void TestLIC11() {
        Input.Coordinates[0].setLocation(2, 0);
        Input.Coordinates[1].setLocation(0, 0);
        Input.Coordinates[2].setLocation(1, 1);
        Input.NUMPOINTS = 3;
        Input.Parameters.GPTS = 1;
        CMV.calcLIC11();
        assertTrue(CMV.cmv[11], "Error: LIC11 should be true since X[0] is larger than X[2]");

        Input.Coordinates[0].setLocation(2, 0);
        Input.Coordinates[1].setLocation(0, 0);
        Input.Coordinates[2].setLocation(1, 1);
        Input.Coordinates[3].setLocation(2, 0);
        Input.Coordinates[4].setLocation(0, 0);
        Input.Coordinates[5].setLocation(0, 1);
        Input.NUMPOINTS = 6;
        Input.Parameters.GPTS = 2;
        CMV.calcLIC11();
        assertTrue(CMV.cmv[11], "Error: LIC11 should be true since X[2] is larger than X[5]");

        Input.Coordinates[0].setLocation(2, 0);
        Input.Coordinates[1].setLocation(0, 0);
        Input.Coordinates[2].setLocation(1, 1);
        Input.Coordinates[3].setLocation(2, 0);
        Input.Coordinates[4].setLocation(0, 0);
        Input.Coordinates[5].setLocation(2, 0);
        Input.NUMPOINTS = 6;
        Input.Parameters.GPTS = 2;
        CMV.calcLIC11();
        assertFalse(CMV.cmv[11], "Error: LIC11 should be false");
    }

    /**
     * Test the calculation of LIC12
     * Checks if false and true for various distances.
     */
    @Test
    public void TestLIC12(){
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(0.5, 0);
        Input.Coordinates[2].setLocation(2, 0);
        Input.Coordinates[3].setLocation(1, 0);
        Input.NUMPOINTS = 4;
        Input.Parameters.LENGTH1 = 0.9;
        Input.Parameters.LENGTH2 = 0.9;
        Input.Parameters.KPTS = 2;
        CMV.calcLIC12();
        assertFalse(CMV.cmv[12], "Error: Should be False");

        Input.Parameters.LENGTH2 = 1.1;
        CMV.calcLIC12();
        assertTrue(CMV.cmv[12], "Error: Should be True");

        Input.Parameters.LENGTH1 = 1.9;
        Input.Parameters.LENGTH2 = 0.6;
        Input.Parameters.KPTS = 1;
        CMV.calcLIC12();
        assertTrue(CMV.cmv[12], "Error: Should be true");
    }


    /**
     * First and second tests checks if false and true respectively is yielded when an insufficient and sufficient obtuse triangle is provided.
     */
    @Test
    public void TestLIC13() {
        // Obtuse triangle
        Input.Parameters.APTS = 1;
        Input.Parameters.BPTS = 1;
        Input.Coordinates[0].setLocation(0, 0);
        Input.Coordinates[1].setLocation(-1, -1);
        Input.Coordinates[2].setLocation(10, 0);
        Input.Coordinates[3].setLocation(-1, -1);
        Input.Coordinates[4].setLocation(5, 1);
        Input.NUMPOINTS = 5;
        Input.Parameters.RADIUS1 = 5;
        Input.Parameters.RADIUS1 = 4;
        CMV.calcLIC13();
        assertFalse(CMV.cmv[13], "Error: Obtuse LIC8 gives true when false");
        Input.Parameters.RADIUS1 = 4;
        Input.Parameters.RADIUS2 = 5;
        CMV.calcLIC13();
        assertTrue(CMV.cmv[13], "Error: Obtuse LIC8 gives false when true");
    }

    /**
     * Test for LIC14 functionality. The test is divided into 2 subtests.
     * Test1 tests that the LIC is false when no set of points fullfil both area requirements.
     * Test2 tests that the LIC is true any set of points (within spec) fullfil both area requirements.
     */
    @Test
    public void TestLIC14() {
        //Test1
        Input.Coordinates[0].setLocation(-1, 1);
        Input.Coordinates[1].setLocation(-1,-1);
        Input.Coordinates[2].setLocation(-1,-1);
        Input.Coordinates[3].setLocation(1, 1);
        Input.Coordinates[4].setLocation(5, 6);
        Input.Coordinates[5].setLocation(1,-1);
        Input.Coordinates[6].setLocation(3,-1);
        Input.NUMPOINTS = 7;
        Input.Parameters.EPTS = 1;
        Input.Parameters.FPTS = 2;
        Input.Parameters.AREA1 = 4;
        Input.Parameters.AREA2 = 4;
        CMV.calcLIC14();
        assertFalse(CMV.cmv[14], "Error: LIC14 true when false");

        //Test2
        Input.Parameters.AREA1 = 3;
        CMV.calcLIC14();
        assertTrue(CMV.cmv[14], "Error: LIC14 false when true");
    }

    public void defaultInputs() {
        Input.Parameters.AREA1 = 1;
        Input.Parameters.RADIUS1 = 1;
        Input.Parameters.AREA2 = 1;
        Input.Parameters.RADIUS2 = 1;
        Input.Parameters.EPSILON = 0.0;
        Input.Parameters.LENGTH1 = 0.0;
        Input.Parameters.LENGTH2 = 0.0;
        Input.Parameters.DIST = 0.0;
        Input.Parameters.APTS = 1;
        Input.Parameters.BPTS = 1;
        Input.Parameters.CPTS = 1;
        Input.Parameters.DPTS = 1;
        Input.Parameters.EPTS = 1;
        Input.Parameters.FPTS = 1;
        Input.Parameters.GPTS = 1;
        Input.Parameters.NPTS = 3;
        Input.Parameters.KPTS = 1;
        Input.Parameters.QPTS = 2;
        Input.Parameters.QUADS = 1;
    }

}
