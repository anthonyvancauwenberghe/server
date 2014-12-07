package org.hyperion.rs2.model.recolor;

import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.recolor.pattern.Pattern;
import org.hyperion.rs2.net.PacketBuilder;

public class Recolor {

    private final int id;
    private Pattern pattern;

    public Recolor(final int id, final Pattern pattern){
        this.id = id;
        this.pattern = pattern;
    }

    public ItemDefinition getItemDefinition(){
        return ItemDefinition.forId(id);
    }

    public int getId(){
        return id;
    }

    public Pattern getPattern(){
        return pattern;
    }

    public void setPattern(final Pattern pattern){
        this.pattern = pattern;
    }

    public void append(final PacketBuilder bldr){
        bldr.putShort(id);
        pattern.append(bldr);
    }

    public String toString(){
        return String.format("%d %s", id, pattern);
    }

    public String toReadableString(){
        return String.format("%s (%d): %s", getItemDefinition().getName(), id, pattern);
    }

    public static Recolor parse(final String line){
        final int i = line.indexOf(' ');
        final int id = Integer.parseInt(line.substring(0, i).trim());
        final Pattern pattern = Pattern.parse(line.substring(i+1).trim());
        return pattern != null ? new Recolor(id, pattern) : null;
    }
}
