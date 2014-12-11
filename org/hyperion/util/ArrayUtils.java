package org.hyperion.util;

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

    public static <T> boolean contains(T t, T... ts) {
        if(ts == null) return false;
        for(final T general : ts)
            if(general != null && general.equals(t))
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
        return Stream.of(ints).anyMatch(Predicate.isEqual(i));
    }
}