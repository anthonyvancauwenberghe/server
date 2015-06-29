package org.hyperion.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/10/14
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ArrayUtils {

    private static final Comparator<?> DEFAULT_COMPARATOR = (o1, o2) -> o1.equals(o2) ? 0 : 1;

    public static <T> boolean contains(T needle, T... array) {
        if(array == null) return false;
        for(final T general : array)
            if(general != null && general.equals(needle))
                return true;
        return false;
    }

    public static <T> boolean contains(final Comparator<? super T> comparator, T needle, T... array) {
        if(array == null) return false;
        for(final T general : array)
            if(general != null && comparator.compare(general, needle) == 0)
                return true;
        return false;
    }

    public static int[] fromInteger(final Integer[] integers) {
        final int[] values = new int[integers.length];
        for(int index = 0; index < values.length; index++)
            values[index] = integers[index];
        return values;
    }

    public static int[] fromList(final List<Integer> list) {
        return fromInteger(list.toArray(new Integer[list.size()]));
    }

    public static boolean contains(final int i, final int... ints) {
        for(final int integer : ints)
            if(i == integer)
                return true;
        return false;
    }

}
