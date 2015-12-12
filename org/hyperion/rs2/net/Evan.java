package org.hyperion.rs2.net;

/*
 * @author Martin
 */

public class Evan {

    public static void main(final String[] args) {
        //using a long would be better in the long run but an int can have a max value of 2147M so an integer will do this time
        final int circleCentreX = 0;
        final int circleCentreY = 0;
        int liesInCircle = 0;
        final int totalPoints = 1000000;
        final int squareLength = 2;
        final int halfSqLength = squareLength / 2;
        for(int i = 0; i < totalPoints; i++){
            final double x = (Math.random() * squareLength) - (halfSqLength);
            final double y = (Math.random() * squareLength) - (halfSqLength);
            if(distance(circleCentreX, circleCentreY, x, y) <= halfSqLength){
                //lies within the circle
                liesInCircle++;
            }
        }
        final double estPi = (liesInCircle * 4 / totalPoints);
        System.out.println("pi: " + (liesInCircle * 4) + " : " + estPi + " plus evan sucks :p " + liesInCircle);

    }

    public static double distance(final double x, final double y, final double destX, final double destY) {
        final double deltaX = destX - x;
        final double deltaY = destY - y;
        return Math.sqrt(Math.pow(deltaX, 2D) + Math.pow(deltaY, 2D));
    }
}
