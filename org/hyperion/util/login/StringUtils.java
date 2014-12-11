package org.hyperion.util.login;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/10/14
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {

    public static String substring(final String original, final String start, final String end) {
        try {
            return original.substring(original.indexOf(start) + start.length(), original.indexOf(end));
        } catch (Exception e){
            e.printStackTrace();
            return original;
        }
    }

}
