package Group2compileTest;

/**
 * Main class for decision problem in Launch Interceptor Program.
 * Contains launch boolean, decide function and a helper function for checking final unlocking vector (FUV).
 */
public class DECIDE {
    /**
     * Boolean value representing decision made by program. True=launch interceptor, False=Ignore
     */
    public static boolean launch;

    /**
     * Decide function. Includes call to CMV LIC calculation, calculates PUM from LCM and CMV. 
     * Then calculates FUV from PUM and PUV according to spec. 
     * If all values in FUV is true, launch is set to true and function prints "Yes" to console. Otherwise "No" is outputed.
     */
    public static void decide() throws IllegalArgumentException {
        validateInput();
        CMV.calculate();
        boolean[][] PUM = new boolean[15][15];
        boolean[] FUV = new boolean[15];
        for (int i = 0; i < 15; i++) {
            for (int j = i; j < 15; j++) {
                PUM[i][j] = switch (Input.LCM[i][j]) {
                    case ANDD:
                        yield CMV.cmv[i] && CMV.cmv[j];
                    case ORR:
                        yield CMV.cmv[i] || CMV.cmv[j];
                    case NOTUSED:
                        yield true;
                    default:
                        yield true;
                };
                PUM[j][i] = PUM[i][j];
            }
            FUV[i] = !Input.PUV[i] || allIsTrue(PUM[i]);
        }

        launch = allIsTrue(FUV);

        // String yN = launch ? "Yes" : "No";
        // System.out.println(yN);
    }
    
    /**
     * Helper function to check if all boolean values in array is true.
     * @param array
     * @return True or false
     */
    private static boolean allIsTrue(boolean[] array) {
        for(boolean b : array) if(!b) return false;
        return true;
    }

    /**
     * Helper function to check if input parameters are valid
     * @throws IllegalArgumentException
     */

    private static void validateInput() throws IllegalArgumentException {
            isTrue(Input.NUMPOINTS >= 2 && Input.NUMPOINTS <= 100);
            isTrue(Input.Parameters.LENGTH1 >= 0 && Input.Parameters.LENGTH2 >= 0 );
            isTrue(Input.Parameters.EPSILON >= 0 && Input.Parameters.EPSILON < Math.PI);
            isTrue(Input.Parameters.AREA1 >= 0 && Input.Parameters.AREA2 >= 0);
            isTrue(Input.Parameters.RADIUS1 >= 0 && Input.Parameters.RADIUS2 >= 0);
            isTrue(Input.Parameters.QPTS >= 2 && Input.Parameters.QPTS <= Input.NUMPOINTS);
            isTrue(Input.Parameters.QUADS >= 1 && Input.Parameters.QUADS <= 3);
            isTrue(Input.Parameters.NPTS >= 3 && Input.Parameters.NPTS <= Input.NUMPOINTS);
            isTrue(Input.Parameters.KPTS >= 1 && Input.Parameters.KPTS <= Input.NUMPOINTS-2);
            isTrue(Input.Parameters.APTS >= 1 && Input.Parameters.BPTS >= 1 && Input.Parameters.APTS + Input.Parameters.BPTS <= Input.NUMPOINTS - 3);
            isTrue(Input.Parameters.CPTS >= 1 && Input.Parameters.DPTS >= 1 && Input.Parameters.CPTS + Input.Parameters.DPTS <= Input.NUMPOINTS - 3);
            isTrue(Input.Parameters.EPTS >= 1 && Input.Parameters.FPTS >= 1 && Input.Parameters.EPTS + Input.Parameters.FPTS <= Input.NUMPOINTS - 3);
            isTrue(Input.Parameters.GPTS >= 1 && Input.Parameters.GPTS <= Input.NUMPOINTS-2);
            isTrue(Input.Parameters.DIST >= 0);
    }

    /**
     * Helper function to check if boolean expression is true
     * @throws IllegalArgumentException
     */
    private static void isTrue(boolean b) throws IllegalArgumentException {if (!b) {throw new IllegalArgumentException("INVALID INPUT");}}
}
