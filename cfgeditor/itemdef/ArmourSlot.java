package cfgeditor.itemdef;

public enum ArmourSlot {

    HELMET("Helmet", 0),
    CAPE("Cape", 1),
    AMULET("Amulet", 2),
    WEAPON("Weapon", 3),
    CHEST("Chest", 4),
    SHIELD("Shield", 5),
    BOTTOMS("Bottoms", 7),
    GLOVES("Gloves", 9),
    BOOTS("Boots", 10),
    RING("Ring", 12),
    ARROWS("Arrows", 13),
    PLATEBODY("Platebody", 15),
    FULL_HELM("Full Helm", 16),
    FULL_MASK("Full Mask", 17);

    private final String name;
    public final int value;

    private ArmourSlot(final String name, final int value){
        this.name = name;
        this.value = value;
    }

    public String toString(){
        return name;
    }

    public static ArmourSlot byValue(final int value){
        for(final ArmourSlot slot : values())
            if(slot.value == value)
                return slot;
        return null;
    }

}
