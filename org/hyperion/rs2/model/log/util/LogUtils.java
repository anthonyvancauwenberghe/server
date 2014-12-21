package org.hyperion.rs2.model.log.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringJoiner;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;

public final class LogUtils {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a");
    public static final String NEW_LINE = "-NEWLINE-";

    private LogUtils(){}

    public static String format(final Date date){
        return DATE_FORMAT.format(date);
    }

    public static String toString(final Item[] items){
        final StringJoiner joiner = new StringJoiner(", ");
        for(final Item i : items)
            if(i != null)
                joiner.add(toString(i));
        return joiner.toString();
    }

    public static String toString(final Item i){
        return String.format("%s (%d) x (%,d)", i.getDefinition().getName(), i.getId(), i.getCount());
    }

    public static void send(final Player player, final String string){
        for(final String text : string.split(NEW_LINE))
            send(player, text, 80);
    }

    private static void send(final Player player, final String text, final int perLine){
        int i = 0;
        while(i < text.length()){
            player.sendf(text.substring(i, Math.min(i+perLine, text.length())));
            i += perLine;
        }
    }
}
