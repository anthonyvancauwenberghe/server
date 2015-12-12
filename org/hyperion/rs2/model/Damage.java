package org.hyperion.rs2.model;

/**
 * Represents a single instance of damage.
 *
 * @author Graham
 */
public class Damage {

    /**
     * Represents a hit.
     */
    private Hit hit1;
    private Hit hit2;
    private Hit hit3;

    /**
     * Constructor method.
     */
    public Damage() {
        hit1 = null;
        hit2 = null;
        hit3 = null;
    }

    /**
     * Sets a hit.
     *
     * @param hit The hit to clone.
     */
    public void setHit1(final Hit hit) {
        this.hit1 = hit;
    }

    public void setHit2(final Hit hit) {
        this.hit2 = hit;
    }

    /**
     * Gets the hit damage.
     *
     * @return An <code>int</code> of the damage of this hit.
     */
    public int getHitDamage1() {
        if(hit1 == null){
            return 0;
        }
        return hit1.damage;
    }

    public int getHitDamage2() {
        if(hit2 == null){
            return 0;
        }
        return hit2.damage;
    }

    /**
     * Gets the hit type.
     *
     * @return The type of hit.
     */
    public int getHitType1() {
        if(hit1 == null){
            return HitType.NO_DAMAGE.getType();
        }
        return hit1.type.getType();
    }

    public int getHitType2() {
        if(hit2 == null){
            return HitType.NO_DAMAGE.getType();
        }
        return hit2.type.getType();
    }

    public Hit getHit3() {
        return hit3;
    }

    public void setHit3(final Hit hit) {
        this.hit3 = hit;
    }

    /**
     * Destroy this hit.
     */
    public void clear() {
        hit1 = null;
    }

    public int getStyleType1() {
        // TODO Auto-generated method stub
        return hit1.getStyle();
    }

    public int getStyleType2() {
        // TODO Auto-generated method stub
        return hit2.getStyle();
    }

    /**
     * Represents the four types of damage.
     *
     * @author Graham
     */
    public enum HitType {
        NO_DAMAGE(0),            // blue // no damage is recognised by the client
        NORMAL_DAMAGE(1),        // red
        POISON_DAMAGE(2),        // green
        CRITICAL_DAMAGE(0);    // orange critical

        private final int type;

        /**
         * Constructor.
         *
         * @param type The corresponding integer for damage type.
         */
        HitType(final int type) {
            this.type = type;
        }

        /**
         * Get the damage type.
         *
         * @return The damage type, as an <code>int</code>.
         */
        public int getType() {
            return this.type;
        }
    }

    /**
     * Nested class <code>Hit</code>, handling a single hit.
     *
     * @author Graham
     */
    public static class Hit {

        private final HitType type;
        private final int damage;
        private final int style;

        public Hit(final int damage, final HitType type, int style) {
            if(style >= 5){
                style -= 5;
                this.style = style;
                this.type = HitType.CRITICAL_DAMAGE;
                this.damage = damage;
            }else{
                this.type = type;
                this.damage = damage;
                this.style = style;
            }
        }

        public HitType getType() {
            return type;
        }

        public int getDamage() {
            return damage;
        }

        public int getStyle() {
            return style;
        }
    }

}
