package org.hyperion.rs2.model;

/**
 * Represents a single game object.
 *
 * @author Graham Edgecombe
 */
public class GameObject {

    public final boolean onAllHeights;
    /**
     * The location.
     */
    private final Location location;
    /**
     * The definition.
     */
    private final GameObjectDefinition definition;
    /**
     * The type.
     */
    private final int type;
    /**
     * The rotation.
     */
    private final int rotation;

    public GameObject(final GameObjectDefinition definition, final Location location, final int type, final int rotation) {
        this(definition, location, type, rotation, true);
    }

    /**
     * Creates the game object.
     *
     * @param definition The definition.
     * @param location   The location.
     * @param type       The type.
     * @param rotation   The rotation.
     */
    public GameObject(final GameObjectDefinition definition, final Location location, final int type, final int rotation, final boolean onAllHeights) {
        this.definition = definition;
        this.location = location;
        this.type = type;
        this.rotation = rotation;
        this.onAllHeights = onAllHeights;
    }

    /**
     * Gets the location.
     *
     * @return The location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the definition.
     *
     * @return The definition.
     */
    public GameObjectDefinition getDefinition() {
        return definition;
    }

    /**
     * Gets the type.
     *
     * @return The type.
     */
    public int getType() {
        return type;
    }

    /**
     * Gets the rotation.
     *
     * @return The rotation.
     */
    public int getRotation() {
        return rotation;
    }


    /**
     * Chec if the object is at
     *
     * @param loc
     * @return
     */
    public boolean isAt(final Location loc) {
        if(!onAllHeights)
            return this.location.equals(loc);
        else
            return location.equalsIgnoreHeight(loc);
    }

    /**
     * Check if the object is visible from the other location
     *
     * @param loc
     * @return
     */

    public boolean isVisible(final Location loc) {
        if(!onAllHeights)
            return loc.isWithinDistance(location, 64);
        else
            return location.distance(loc) < 64 && loc.getZ() % 4 == location.getZ() % 4;
    }

}
