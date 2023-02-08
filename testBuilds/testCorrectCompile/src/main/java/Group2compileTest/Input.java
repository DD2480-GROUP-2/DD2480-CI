package Group2compileTest;

import java.awt.*;

/**
 * The input class. Stores variables (global and static) for all inputs to Launch Interceptor Program.
 * Can be modified for various test purposes etc. doubles and int variables will have to be initialised before calling the decide routine.
 */
public class Input {
    /**
     * Enum values used for LCM. Includes;
     * ANDD,
     * ORR,
     * NOTUSED
     */
    public enum Connector {
        ANDD, ORR, NOTUSED
    }

    /** The number of planar data points. */
    public static int NUMPOINTS;
    /** Array containing the coordinates of data points. setLocation(x,y) will need to be called to initialize actual coordinates */
    public static Point[] Coordinates = new Point[100];

    /** Logical Connector Matrix. */
    public static Connector[][] LCM = new Connector[15][15];
    /** Preliminary Unlocking Vector. */
    public static boolean[] PUV = new boolean[15];

    /** Class holding parameters for LIC */
    public static class Parameters {
        /** Length used in LICs 0, 7, 12 */
        public static double LENGTH1;
        /** Radius used in LICs 1, 8, 13 */
        public static double RADIUS1;
        /** Angle deviation from PI used in LICs 2, 9 */
        public static double EPSILON;
        /** Area used in LICs 3, 10, 14 */
        public static double AREA1;
        /** Number of consecutive points used in LIC 4 */
        public static int QPTS;
        /** Number of quadrant used in LIC 4 */
        public static int QUADS;
        /** Distance used in LIC 6 */
        public static double DIST;
        /** Number of consecutive points used in LIC 6  */
        public static int NPTS;
        /** Number of intervening points used in LIC 7, 12  */
        public static int KPTS;
        /** Number of intervening points used in LIC 8, 13  */
        public static int APTS;
        /** Number of intervening points used in LIC 8, 13  */
        public static int BPTS;
        /** Number of intervening points used in LIC 9  */
        public static int CPTS;
        /** Number of intervening points used in LIC 9  */
        public static int DPTS;
        /** Number of intervening points used in LIC 10, 14*/
        public static int EPTS;
        /** Number of intervening points used in LIC 10, 14*/
        public static int FPTS;
        /** Number of intervening points used in LIC 11  */
        public static int GPTS;
        /** Maximum length used in LIC 12 */
        public static double LENGTH2;
        /** Maximum radius used in LIC 13 */
        public static double RADIUS2;
        /** Maximum area used in LIC 14 */
        public static double AREA2;
    }
}
