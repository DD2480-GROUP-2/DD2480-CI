package Group2TestFailTest;

import java.awt.Point;
import java.util.Arrays;


/**
 * Class contains the conditions met vector (cmv) array along with 
 * all LIC calculating functions with helpers.
 */
public class CMV{

    /**
     * Comparison results
     * GT = Greater Than
     * LT = Lesser Than
     * EQ = Equals
     */
    public enum Comptype {
        GT,
        LT,
        EQ;
    }

    /** The Conditions Met Vector. 15 boolean values corresponding to each LIC */
    public static boolean[] cmv = new boolean[15];

    /**
     * Compare double values and return result as Comptype.
     * @param a double to compare
     * @param b double to compare
     * @return Comptype EQ if difference a - b < 0.000001, LT if a < b, GT if a > b
     * </>
     */
    public static Comptype doubleCompare(double a, double b) {
        if (Math.abs(a - b) < 0.000001)
            return Comptype.EQ;
        if (a < b)
            return Comptype.LT;

        return Comptype.GT;
    }
    
    /**
     * This function calls all 15 LIC calculations which updates the entire cmv array.
     */
    public static void calculate(){
        calcLIC0();
        calcLIC1();
        calcLIC2();
        calcLIC3();
        calcLIC4();
        calcLIC5();
        calcLIC6();
        calcLIC7();
        calcLIC8();
        calcLIC9();
        calcLIC10();
        calcLIC11();
        calcLIC12();
        calcLIC13();
        calcLIC14();
    }

    /**
     * Sets cmv[0] to true if there exists at least one set of two consecutive data points that are a distance greater than
     * the length, LENGTH1, apart.
     */
    public static void calcLIC0(){
        cmv[0] = false;

        if (Input.NUMPOINTS < 2) return;

        for (int i = 0; i < Input.NUMPOINTS - 1; i++) {
            if (doubleCompare(Input.Coordinates[i].distance(Input.Coordinates[i+1]), Input.Parameters.LENGTH1) == Comptype.GT) {
                cmv[0] = true;
                return;
            }
        }
    }

    /**
     * Sets cmv[1] to true if there exists at least one set of three consecutive data points that cannot all be contained
     * within or on a circle of radius RADIUS1.
     */
    public static void calcLIC1(){
        cmv[1] = false;
        if (Input.NUMPOINTS < 3 || doubleCompare(Input.Parameters.RADIUS1, 0) == Comptype.LT)
            return;

        for (int i = 0; i < Input.NUMPOINTS - 2; i++) {
            double minimumCircleRadius = minRadiusEnclose(Input.Coordinates[i], Input.Coordinates[i + 1], Input.Coordinates[i + 2]);
            if (doubleCompare(minimumCircleRadius, Input.Parameters.RADIUS1) != Comptype.GT) {
                cmv[1] = true;
                return;
            }
        }
    }

    /**
     * Sets cmv[2] to true if there exists at least one set of three consecutive data points which form an angle such that:
        angle < (PI−EPSILON)
        or
        angle > (PI+EPSILON)
        The second of the three consecutive points is always the vertex of the angle. If either the first
        point or the last point (or both) coincides with the vertex, the angle is undefined and the LIC
        is not satisfied by those three points.

     */
    public static void calcLIC2(){
        cmv[2] = false;

        if(Input.NUMPOINTS < 3){
            return;
        }

        for (int i = 0; i < Input.NUMPOINTS-2; i++) {
            Point p1 = Input.Coordinates[i];
            Point p2 = Input.Coordinates[i+1];
            Point p3 = Input.Coordinates[i+2];


            // If either the first point or the last point (or both) coincides with the vertex, the angle is undefined
            if((doubleCompare(p1.x, p2.x)==Comptype.EQ&&doubleCompare(p1.y, p2.y)==Comptype.EQ)
             ||(doubleCompare(p2.x, p3.x)==Comptype.EQ&&doubleCompare(p2.y, p3.y)==Comptype.EQ)){
                continue;
            }
            
            double angle = Math.acos((Math.pow(p2.distance(p1), 2)+Math.pow(p2.distance(p3), 2)-Math.pow(p1.distance(p3), 2))/
            (2*p2.distance(p1)*p2.distance(p3)));
            
            if(doubleCompare(angle, Math.PI-Input.Parameters.EPSILON)==Comptype.LT || doubleCompare(angle, Math.PI+Input.Parameters.EPSILON)==Comptype.GT){
                cmv[2] = true;
                break;
            }
        }


    }

    /**
     * Sets cmv[3] to true if there exists at least one set of three consecutive data points that are the vertices of a triangle
     * with area greater than AREA1
     */
    public static void calcLIC3(){
        if (Input.NUMPOINTS < 3) {
            cmv[3] = false;
            return;
        }
        for (int i = 0; i < Input.NUMPOINTS-2; i++) {
            Point p1 = Input.Coordinates[i];
            Point p2 = Input.Coordinates[i+1];
            Point p3 = Input.Coordinates[i+2];
            Double area = Math.abs(p1.getX()*p2.getY()+p2.getX()*p3.getY() + p3.getX()*p1.getY() - p1.getY()*p2.getX() - p2.getY()*p3.getX() - p3.getY()*p1.getX())/2;
            if (doubleCompare(area, Input.Parameters.AREA1) == Comptype.GT) {
                cmv[3] = true;
                return;
            }
        }
        cmv[3] = false;
    }

    /**
     *  Sets cmv[4] to true if there exists at least one set of Q PTS consecutive data points that lie in more than QUADS
        quadrants. Where there is ambiguity as to which quadrant contains a given point, priority
        of decision will be by quadrant number, i.e., I, II, III, IV. For example, the data point (0,0)
        is in quadrant I, the point (-l,0) is in quadrant II, the point (0,-l) is in quadrant III, the point
        (0,1) is in quadrant I and the point (1,0) is in quadrant I.
     */
    public static void calcLIC4(){
        cmv[4] = false;

        //Checks that there are more NUMPOINTS than QPTS, as the LIC is only true if a set of QPTS data points have been evaluated.
        if (Input.NUMPOINTS < Input.Parameters.QPTS) {
            return;
        }

        //Checks QPTS consecutive NUMPOINTS to see which quadrant they are in
        for(int i = 0; i < Input.NUMPOINTS-Input.Parameters.QPTS; i++) {

             //Boolean array set to true if any of the coordinates are in the quadrant
            boolean[] quadrants = new boolean[4];
            int count = 0;

            for(int j = 0; j < Input.Parameters.QPTS; j++ ) {
                Point p = Input.Coordinates[i+j];
                double x = p.getX();
                double y = p.getY();

                //Evaluates which quadrant the coordinate is in
                if(x >= 0) {
                    if(y >= 0) {
                        quadrants[0] = true;
                    } else {
                        quadrants[2] = true;
                    } 
                } else {
                    if(y >= 0) {
                        quadrants[1] = true;
                    } else {
                        quadrants[3] = true;
                    }
                }
            }

            //Counts amount of QUADS
            for(boolean q : quadrants) {
                    if(q) {
                        count +=1;
                    }
                }

            //Checks if requirement of more quadrants that QUADS
            if(Input.Parameters.QUADS < count) {
                cmv[4] = true;
                return;
            }
        }
        cmv[4] = false;
    }

    /**
     * Sets cmv[5] to true if There exists at least one set of two consecutive data points, (X[i],Y[i]) and (X[j],Y[j]), such
        that X[j] - X[i] < 0. (where i = j-1)
     */
    public static void calcLIC5(){
        cmv[5] = false;
        for (int i = 0; i < Input.NUMPOINTS - 1; i++) {
            if (doubleCompare(Input.Coordinates[i + 1].getX() - Input.Coordinates[i].getX(), 0) == Comptype.LT) {
                cmv[5] = true;
                return;
            }
        }
    }

    /**
     * Sets cmv[6] to true if there exists at least one set of N PTS consecutive data points such that at least one of the
        points lies a distance greater than DIST from the line joining the first and last of these N PTS
        points. If the first and last points of these N PTS are identical, then the calculated distance
        to compare with DIST will be the distance from the coincident point to all other points of
        the N PTS consecutive points. The condition is not met when NUMPOINTS < 3.
     */
    public static void calcLIC6(){
        cmv[6] = false;

        if (Input.NUMPOINTS < 3 || Input.Parameters.NPTS < 3 || Input.Parameters.NPTS > Input.NUMPOINTS || Input.Parameters.DIST < 0) return;

        for (int i = 0; i < Input.NUMPOINTS-Input.Parameters.NPTS + 1; i++) {
            
            double edges[] = new double[3];
            edges[0] = Input.Coordinates[i].distance(Input.Coordinates[i+Input.Parameters.NPTS-1]);

            for (int j = 1; j < Input.Parameters.NPTS-1; j++) { // Exclude first and last point
                if (edges[0] == 0) {
                    if (doubleCompare(Input.Coordinates[i].distance(Input.Coordinates[i+j]), Input.Parameters.DIST) == Comptype.GT) {
                        cmv[6] = true;
                        return;
                    }
                } else {
                    edges[1] = Input.Coordinates[i].distance(Input.Coordinates[i+j]);
                    edges[2] = Input.Coordinates[i+Input.Parameters.NPTS-1].distance(Input.Coordinates[i+j]);
                    double s = (edges[0] + edges[1] + edges[2]) / 2;
                    double area = Math.sqrt(s*(s-edges[0])*(s-edges[1])*(s-edges[2]));
                    double height = (2 * area) / edges[0]; // height of triangle

                    if (doubleCompare(height, Input.Parameters.DIST) == Comptype.GT) {
                        cmv[6] = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Sets cmv[7] to true if there exists at least one set of two data points separated by exactly K PTS consecutive intervening points that are a distance greater than the length, LENGTH1, apart. The condition
        is not met when NUMPOINTS < 3
     */
    public static void calcLIC7(){
        cmv[7] = false;

        if(Input.NUMPOINTS < 3) return;

        if(Input.Parameters.KPTS < 1 || Input.Parameters.KPTS > (Input.NUMPOINTS - 2)) return;
        
        for (int i = 0; i < Input.NUMPOINTS-Input.Parameters.KPTS-1; i++) {
            Point p1 = Input.Coordinates[i];
            Point p2 = Input.Coordinates[i+Input.Parameters.KPTS+1]; // Point separated by KPTS points

            if(doubleCompare(p1.distance(p2), Input.Parameters.LENGTH1) == Comptype.GT){
                cmv[7] = true;
                break;
            }
        }

    }

    /**
     * Sets cmv[8] to true if there exists at least one set of three data points separated by exactly A PTS and B PTS
        consecutive intervening points, respectively, that cannot be contained within or on a circle of
        radius RADIUS1. The condition is not met when NUMPOINTS < 5.
     */
    public static void calcLIC8(){
        if (Input.NUMPOINTS < 5) {
            cmv[8] = false;
            return;
        }
        // Calculate minimum radius for enclosing triangle
        for (int i = 0; i < Input.NUMPOINTS-2-Input.Parameters.APTS-Input.Parameters.BPTS; i++) {
            double rad = minRadiusEnclose(Input.Coordinates[i], Input.Coordinates[i+Input.Parameters.APTS+1], Input.Coordinates[i+Input.Parameters.APTS+Input.Parameters.BPTS+2]);
            if (doubleCompare(rad, Input.Parameters.RADIUS1) == Comptype.GT) {
                cmv[8] = true;
                return;
            }
        }

        cmv[8] = false;
    }
    
    /**
     * Sets cmv[9] to true if there exists at least one set of three data points separated by exactly C PTS and D PTS
        consecutive intervening points, respectively, that form an angle such that:
        angle < (PI−EPSILON)
        or
        angle > (PI+EPSILON)
        The second point of the set of three points is always the vertex of the angle. If either the first
        point or the last point (or both) coincide with the vertex, the angle is undefined and the LIC
        is not satisfied by those three points. When NUMPOINTS < 5, the condition is not met.
     */
      public static void calcLIC9(){
        cmv[9] = false;

        if(Input.NUMPOINTS < 5){
            return;
        }

        for (int i = 0; i < Input.NUMPOINTS-(Input.Parameters.CPTS+Input.Parameters.DPTS+2); i++) {
            int bn = Input.Parameters.CPTS+1;
            int bn2 = bn + Input.Parameters.DPTS+1;

            Point p1 = Input.Coordinates[i];
            Point p2 = Input.Coordinates[i+bn];
            Point p3 = Input.Coordinates[i+bn2];

            // If either the first point or the last point (or both) coincides with the vertex, the angle is undefined
            if((doubleCompare(p1.x, p2.x)==Comptype.EQ&&doubleCompare(p1.y, p2.y)==Comptype.EQ)
             ||(doubleCompare(p2.x, p3.x)==Comptype.EQ&&doubleCompare(p2.y, p3.y)==Comptype.EQ)){
                continue;
            }
            
            double angle = Math.acos((Math.pow(p2.distance(p1), 2)+Math.pow(p2.distance(p3), 2)-Math.pow(p1.distance(p3), 2))/
            (2*p2.distance(p1)*p2.distance(p3)));
            
            if(doubleCompare(angle, Math.PI-Input.Parameters.EPSILON)==Comptype.LT || doubleCompare(angle, Math.PI+Input.Parameters.EPSILON)==Comptype.GT){
                cmv[9] = true;
                break;
            }
        }
        return;

    }

    /** Sets cmv[10] to true if there exists at least one set of three data points separated by exactly E PTS and F PTS consecutive intervening points, respectively, that are the vertices of a triangle with area greater
     * than AREA1. The condition is not met when NUMPOINTS < 5. */
    public static void calcLIC10(){
        cmv[10] = false;

        if (Input.NUMPOINTS < 5) return;
        if (Input.Parameters.EPTS < 1 || Input.Parameters.FPTS < 1) return;

        for (int i = 0; i < Input.NUMPOINTS-2 - Input.Parameters.EPTS - Input.Parameters.FPTS; i++) {
            double edge1 = Input.Coordinates[i].distance(Input.Coordinates[i+Input.Parameters.EPTS+1]);
            double edge2 = Input.Coordinates[i].distance(Input.Coordinates[i+Input.Parameters.EPTS+Input.Parameters.FPTS+2]);
            double edge3 = Input.Coordinates[i+Input.Parameters.EPTS+1].distance(Input.Coordinates[i+Input.Parameters.EPTS+Input.Parameters.FPTS+2]);
            double s = (edge1 + edge2 + edge3) / 2;
            double area = Math.sqrt(s*(s-edge1)*(s-edge2)*(s-edge3));
            if (doubleCompare(area, Input.Parameters.AREA1) == Comptype.GT) {
                cmv[10] = true;
                return;
            }
        }
    }

    /**
     * Sets cmv[11] to true if there exists at least one set of two data points, (X[i],Y[i]) and (X[j],Y[j]), separated by
        exactly G PTS consecutive intervening points, such that X[j] - X[i] < 0. (where i < j ) The
        condition is not met when NUMPOINTS < 3.

     */
    public static void calcLIC11(){
        cmv[11] = false;

        if (Input.NUMPOINTS < 3 || Input.Parameters.GPTS < 1 || Input.Parameters.GPTS > Input.NUMPOINTS - 2) return;

        for (int i = 0; i < Input.NUMPOINTS - 1 - Input.Parameters.GPTS; i++) {
            if (doubleCompare(Input.Coordinates[i+Input.Parameters.GPTS+1].getX() - Input.Coordinates[i].getX(), 0) == Comptype.LT) {
                cmv[11] = true;
                return;
            }
        }

    }

    /**
     * Sets cmv[12] to true if There exists at least one set of two data points, separated by exactly K PTS consecutive
        intervening points, which are a distance greater than the length, LENGTH1, apart. In addition, there exists at least one set of two data points (which can be the same or different from
        the two data points just mentioned), separated by exactly K PTS consecutive intervening
        points, that are a distance less than the length, LENGTH2, apart. Both parts must be true
        for the LIC to be true. The condition is not met when NUMPOINTS < 3.
     */
    public static void calcLIC12(){
        cmv[12] = false;

        if(Input.NUMPOINTS < 3) return;

        if(Input.Parameters.KPTS < 1 || Input.Parameters.KPTS > (Input.NUMPOINTS - 2)) return;
        
        boolean GT = false;
        boolean LT = false;

        for (int i = 0; i < Input.NUMPOINTS-Input.Parameters.KPTS-1; i++) {
            Point p1 = Input.Coordinates[i];
            Point p2 = Input.Coordinates[i+Input.Parameters.KPTS+1]; // Point separated by KPTS points

            if(doubleCompare(p1.distance(p2), Input.Parameters.LENGTH1) == Comptype.GT) {
                GT = true;
            }

            if(doubleCompare(p1.distance(p2), Input.Parameters.LENGTH2) == Comptype.LT) {
                LT = true;
            }
        }

        if(GT&&LT) cmv[12] = true;
    }

    /** Sets cmv[13] to true if there exists at least one set of three data points, separated by exactly A PTS and B PTS
    consecutive intervening points, respectively, that cannot be contained within or on a circle of
    radius RADIUS1. In addition, there exists at least one set of three data points (which can be
    the same or different from the three data points just mentioned) separated by exactly A PTS
    and B PTS consecutive intervening points, respectively, that can be contained in or on a
    circle of radius RADIUS2. Both parts must be true for the LIC to be true. The condition is
    not met when NUMPOINTS < 5.
    */
    public static void calcLIC13(){
        cmv[13] = false;
        if (Input.NUMPOINTS < 5) {
            return;
        }
        boolean rad1 = false;
        boolean rad2 = false;
        // Calculate minimum radius for enclosing triangle
        for (int i = 0; i < Input.NUMPOINTS-2-Input.Parameters.APTS-Input.Parameters.BPTS; i++) {
            double rad = minRadiusEnclose(Input.Coordinates[i], Input.Coordinates[i+Input.Parameters.APTS+1], Input.Coordinates[i+Input.Parameters.APTS+Input.Parameters.BPTS+2]);
            if (doubleCompare(rad, Input.Parameters.RADIUS1) == Comptype.GT) {
                rad1 = true;
            }
            if (doubleCompare(rad, Input.Parameters.RADIUS2) != Comptype.GT) {
                rad2 = true;
            }
        }

        cmv[13] = rad1 && rad2;

    }

    /**
     * Sets cmv[14] to true if there exists at least one set of three data points, separated by exactly E PTS and F PTS consecutive intervening points, respectively, that are the vertices of a triangle with area greater
        than AREA1. In addition, there exist three data points (which can be the same or different
        from the three data points just mentioned) separated by exactly E PTS and F PTS consecutive intervening points, respectively, that are the vertices of a triangle with area less than
        AREA2. Both parts must be true for the LIC to be true. The condition is not met when
        NUMPOINTS < 5.
     */
    public static void calcLIC14(){

        cmv[14] = false;

        if(Input.NUMPOINTS < 5){
            return;
        }

        boolean GT_area1 = false;
        boolean LT_area2 = false;

        int E_pos = Input.Parameters.EPTS+1;
        int F_pos = E_pos + Input.Parameters.FPTS+1;

        for (int i = 0; i < Input.NUMPOINTS-(F_pos); i++) {

            Point p1 = Input.Coordinates[i];
            Point p2 = Input.Coordinates[i+E_pos];
            Point p3 = Input.Coordinates[i+F_pos];

            Double area = Math.abs(p1.getX()*p2.getY()+p2.getX()*p3.getY() + p3.getX()*p1.getY() - p1.getY()*p2.getX() - p2.getY()*p3.getX() - p3.getY()*p1.getX())/2;
            if (doubleCompare(area, Input.Parameters.AREA1) == Comptype.GT) {
                GT_area1 = true;
            }
            if (doubleCompare(area, Input.Parameters.AREA2) == Comptype.LT) {
                LT_area2 = true;
            }
            if(GT_area1 && LT_area2){
                break;
            }
        }

        if(GT_area1 && LT_area2){
            cmv[14] = true;
        }

        return;

    }


    /**
     * Helper function used in LIC 1, 8 and 13.
     * @param p1
     * @param p2
     * @param p3
     * @return minimum radius to enclose points in circle
     */
    private static double minRadiusEnclose(Point p1, Point p2, Point p3) {
        double a = p1.distance(p2);
        double b = p1.distance(p3);
        double c = p2.distance(p3);
        double sides[] = new double[3];
        sides[0] = a;
        sides[1] = b;
        sides[2] = c;
        
        if (a == 0 && b == 0 && c == 0)
            return 0;

        Arrays.sort(sides);
        // Obtuse or right triangle => longest side is diameter of smallest possible circle
        if (sides[2]*sides[2] >= sides[1]*sides[1] + sides[0]*sides[0]) {
            return sides[2]/2;
        }
        // Otherwise => circumcircle is smallest possible circle
        else {
            return (a*b*c)/Math.sqrt((a+b+c)*(b+c-a)*(c+a-b)*(a+b-c));
        }

    }

}
