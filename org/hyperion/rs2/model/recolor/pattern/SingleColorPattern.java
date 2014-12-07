package org.hyperion.rs2.model.recolor.pattern;

import org.hyperion.rs2.net.PacketBuilder;

public class SingleColorPattern extends Pattern{

    private int color;

    protected SingleColorPattern(final Type type, final int color){
        super(type);
        this.color = color;
    }

    public int getColor(){
        return color;
    }

    public void setColor(final int color){
        this.color = color;
    }

    public void append(final PacketBuilder bldr){
        super.append(bldr);
        bldr.putInt(color);
    }

    public String toString(){
        return String.format("%s %d", super.toString(), color);
    }
}
