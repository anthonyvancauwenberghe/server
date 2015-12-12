package org.hyperion.rs2.model.content.misc;

public class PrayerIcon {
    private final boolean curse = false;
    String name;
    private int prayerId, level, headicon, frame;
    private int[] prayers;
    private double drain;

    public int getId() {
        return prayerId;
    }

    public int getLevel() {
        return level;
    }

    public int getHeadicon() {
        return headicon;
    }

    public int getFrame() {
        return frame;
    }

    public double getDrain() {
        return drain;
    }

    public int[] getPrayers() {
        return prayers;
    }

    public String getName() {
        return name;
    }

    public boolean isCurse() {
        return curse;
    }

}