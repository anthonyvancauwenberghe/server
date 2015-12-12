package org.hyperion.util;

public class Boundary {

    public final int min;
    public final int max;

    public Boundary(final int min, final int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * @param value
     * @param min
     * @param max
     * @returns the specified value if the value is between min and max,
     * otherwise min
     */
    public static int checkBounds(final int value, final Boundary b) {
        if(value < b.min || value > b.max)
            return b.min;
        return value;
    }

    /**
     * Checks if value is within bounds
     *
     * @param value
     * @returns true if value is greater or equal to the minimum bound and
     * smaller or equal to the maximum bound
     */
    public boolean withinBounds(final int value) {
        return value <= max && value >= min;
    }
}
