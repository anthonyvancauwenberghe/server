package org.hyperion.rs2.model.recolor.pattern;

import org.hyperion.rs2.net.PacketBuilder;

public class DualColorPattern extends Pattern{

    private int color1;
    private int color2;

    protected DualColorPattern(final Type type, final int color1, final int color2){
        super(type);
        this.color1 = color1;
        this.color2 = color2;
    }

    public int getColor1(){
        return color1;
    }

    public void setColor1(final int color1){
        this.color1 = color1;
    }

    public int getColor2(){
        return color2;
    }

    public void setColor2(final int color2){
        this.color2 = color2;
    }

    public void append(final PacketBuilder bldr){
        super.append(bldr);
        bldr.putInt(color1).putInt(color2);
    }

    public String toString(){
        return String.format("%s %d %d", super.toString(), color1, color2);
    }
}
