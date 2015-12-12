package org.hyperion.rs2.model;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.net.LoginDebugger;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents a character in the game world, i.e. a <code>Player</code> or
 * an <code>NPC</code>.
 *
 * @author Graham Edgecombe
 */
public abstract class Entity {

    /**
     * Gets the list of local npcs.
     * @return The list of local npcs.
     */
    //public List<NPC> getLocalNPCs() {
    //	return localNpcs;
    //}

    static {
        CommandHandler.submit(new Command("getlocalplayers", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) {
                player.getActionSender().sendMessage("Localplayers: " + player.getLocalPlayers().size());
                return true;
            }
        });
    }

    /**
     * The update flags.
     */
    private final UpdateFlags updateFlags = new UpdateFlags();
    /**
     * The entity's cooldowns.
     */
    private final EntityCooldowns cooldowns = new EntityCooldowns();
    /**
     * The list of local players.
     */
    private final Set<Player> localPlayers = new LinkedHashSet<Player>();
    /**
     * The list of local npcs.
     */
    private final Set<NPC> localNpcs = new LinkedHashSet<NPC>();
    /**
     * The walking queue.
     */
    private final WalkingQueue walkingQueue = new WalkingQueue(this);
    /**
     * The sprites i.e. walk directions.
     */
    private final Sprites sprites = new Sprites();
    /**
     * The entity's first stored hit for updates.
     */
    private final transient Damage damage = new Damage();
    public int combatLevel = 3;
    public CombatEntity cE = new CombatEntity((Entity) this);
    public String forcedMessage = "";

    /**
     * The list of local players.
     */
    //private final List<Player> localPlayers = new LinkedList<Player>();

    /**
     * The list of local npcs.
     */
    //private final List<NPC> localNpcs = new LinkedList<NPC>();
    /**
     * The index in the <code>EntityList</code>.
     */
    private int index;
    /**
     * The current location.
     */
    private Location location;
    /**
     * The entity's state of life.
     */
    private boolean isDead;
    /**
     * The entity's combat state.
     */
    private boolean isInCombat = false;
    /**
     * Auto-retaliation setting.
     */
    private boolean isAutoRetaliating;
    /**
     * The teleportation target.
     */
    private Location teleportTarget = null;
    /**
     * The teleporting flag.
     */
    private boolean teleporting = false;
    /**
     * The last known map region.
     */
    private Location lastKnownRegion = this.getLocation();
    /**
     * Map region changing flag.
     */
    private boolean mapRegionChanging = false;

    /**
     * The current animation.
     */
    private Animation currentAnimation;

    /**
     * The current graphic.
     */
    private Graphic currentGraphic;

    /**
     * The current region.
     */
    private Region currentRegion;

    /**
     * The interacting entity.
     */
    private Entity interactingEntity;

    /**
     * The face location.
     */
    private Location face;

    /**
     * Entity's combat aggressor state.
     */
    private boolean isAggressor;
    private boolean isHidden = true;

    /**
     * Creates the entity.
     */
    public Entity() {
        final boolean player = this instanceof Player;
        setLocation(getDefaultLocation(player ? "human" : "npc"));
        this.lastKnownRegion = location;
        if(player)
            LoginDebugger.getDebugger().log("1.Made entity:");
    }

    /**
     */
    //public static final Location DEFAULT_LOCATION = Location.create(2900 + ((int)Math.random()*100), 3300 + ((int)Math.random()*100), 0);//Location.create(3433, 2892, 0);
    public static Location getDefaultLocation(final String type) {
        if(type.toLowerCase().contains("npc"))
            return Location.create(0, 0, 0);
        return Location.create(2795, 3321, 0);
    }

    public CombatEntity getCombat() {
        return cE;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void isHidden(final boolean bool) {
        isHidden = bool;
    }

    /**
     * Returns the combat state of this entity.
     *
     * @return <code>boolean</code> The entity's combat state.
     */
    public boolean isInCombat() {
        return isInCombat;
    }

    /**
     * Set the entity's combat state.
     *
     * @param isInCombat This entity's combat state.
     */
    public void setInCombat(final boolean isInCombat) {
        this.isInCombat = isInCombat;
    }

    /**
     * Gets the entity's aggressor state.
     *
     * @return boolean The entity's aggressor state.
     */
    public boolean getAggressorState() {
        return isAggressor;
    }

    /**
     * Sets the aggressor state for this entity.
     */
    public void setAggressorState(final boolean b) {
        isAggressor = b;
    }

    /**
     * Get this entity's autoretaliation setting.
     *
     * @return <code>true</code> if autoretaliation is on, <code>false</code> if not.
     */
    public boolean isAutoRetaliating() {
        return isAutoRetaliating;
    }

    /**
     * Set the entity's autoretaliation setting.
     *
     * @param b <code>true/false</code> Whether or not this entity will autoretaliate when attacked.
     */
    public void setAutoRetaliating(final boolean b) {
        this.isAutoRetaliating = b;
    }

    /**
     * Is the entity dead?
     *
     * @return
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Set the entity's state of life.
     *
     * @param isDead Boolean
     */
    public void setDead(final boolean isDead) {
        this.isDead = isDead;
    }

    /**
     * Makes this entity face a location.
     *
     * @param location The location to face.
     */
    public void face(final Location location) {
        this.face = location;
        this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
    }

    /**
     * Checks if this entity is facing a location.
     *
     * @return The entity face flag.
     */
    public boolean isFacing() {
        return face != null;
    }

    /**
     * Resets the facing location.
     */
    public void resetFace() {
        this.face = null;
        this.updateFlags.flag(UpdateFlag.FACE_COORDINATE);
    }

    /**
     * Gets the face location.
     *
     * @return The face location, or <code>null</code> if the entity is not
     * facing.
     */
    public Location getFaceLocation() {
        return face;
    }

    /**
     * Checks if this entity is interacting with another entity.
     *
     * @return The entity interaction flag.
     */
    public boolean isInteracting() {
        return interactingEntity != null;
    }

    /**
     * Resets the interacting entity.
     */
    public void resetInteractingEntity() {
        this.interactingEntity = null;
        this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
    }

    public void forceMessage(final String s) {
        forcedMessage = s;
        getUpdateFlags().flag(UpdateFlags.UpdateFlag.FORCED_CHAT);
    }

    /**
     * Gets the interacting entity.
     *
     * @return The entity to interact with.
     */
    public Entity getInteractingEntity() {
        return interactingEntity;
    }

    /**
     * Sets the interacting entity.
     *
     * @param entity The new entity to interact with.
     */
    public void setInteractingEntity(final Entity entity) {
        this.interactingEntity = entity;
        this.updateFlags.flag(UpdateFlag.FACE_ENTITY);
    }

    /**
     * Gets the current region.
     *
     * @return The current region.
     */
    public Region getRegion() {
        return currentRegion;
    }

    /**
     * Gets the current animation.
     *
     * @return The current animation;
     */
    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Gets the current graphic.
     *
     * @return The current graphic.
     */
    public Graphic getCurrentGraphic() {
        return currentGraphic;
    }

    /**
     * Resets attributes after an update cycle.
     */
    public void reset() {
        this.currentAnimation = null;
        this.currentGraphic = null;
    }

    /**
     * Animates the entity.
     *
     * @param animation The animation.
     */
    public void playAnimation(final Animation animation) {
        this.currentAnimation = animation;
        this.getUpdateFlags().flag(UpdateFlag.ANIMATION);
        if(this instanceof Player){
            ((Player) this).debugMessage("Animation: " + animation.getId());
        }
    }

    /**
     * Plays graphics.
     *
     * @param graphic The graphics.
     */
    public void playGraphics(final Graphic graphic) {
        this.currentGraphic = graphic;
        this.getUpdateFlags().flag(UpdateFlag.GRAPHICS);
    }

    public void vacateSquare() {
        getWalkingQueue().reset();
        if(World.getWorld().isWalkAble(location.getZ(), location.getX(), location.getY(), location.getX() - 1, location.getY(), 0)){
            getWalkingQueue().addStep(location.getX() - 1, location.getY());
        }else if(World.getWorld().isWalkAble(location.getZ(), location.getX(), location.getY(), location.getX() + 1, location.getY(), 0)){
            getWalkingQueue().addStep(location.getX() + 1, location.getY());
        }else if(World.getWorld().isWalkAble(location.getZ(), location.getX(), location.getY(), location.getX(), location.getY() - 1, 0)){
            getWalkingQueue().addStep(location.getX(), location.getY() - 1);
        }else if(World.getWorld().isWalkAble(location.getZ(), location.getX(), location.getY(), location.getX(), location.getY() + 1, 0)){
            getWalkingQueue().addStep(location.getX(), location.getY() + 1);
        }
        getWalkingQueue().finish();
    }

    /**
     * Gets the walking queue.
     *
     * @return The walking queue.
     */
    public WalkingQueue getWalkingQueue() {
        return walkingQueue;
    }

    /**
     * Gets the last known map region.
     *
     * @return The last known map region.
     */
    public Location getLastKnownRegion() {
        return lastKnownRegion;
    }

    /**
     * Sets the last known map region.
     *
     * @param lastKnownRegion The last known map region.
     */
    public void setLastKnownRegion(final Location lastKnownRegion) {
        this.lastKnownRegion = lastKnownRegion;
    }

    /**
     * Checks if the map region has changed in this cycle.
     *
     * @return The map region changed flag.
     */
    public boolean isMapRegionChanging() {
        return mapRegionChanging;
    }

    /**
     * Sets the map region changing flag.
     *
     * @param mapRegionChanging The map region changing flag.
     */
    public void setMapRegionChanging(final boolean mapRegionChanging) {
        this.mapRegionChanging = mapRegionChanging;
    }

    /**
     * Checks if this entity has a target to teleport to.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean hasTeleportTarget() {
        return teleportTarget != null;
    }

    /**
     * Gets the teleport target.
     *
     * @return The teleport target.
     */
    public Location getTeleportTarget() {
        return teleportTarget;
    }

    public void setTeleportTarget(final Location teleportTarget) {
        setTeleportTarget(teleportTarget, true, true);
    }

    public void setTeleportTarget(final Location teleportTarget, final boolean deathCheck) {
        setTeleportTarget(teleportTarget, true, deathCheck);
    }

    /**
     * Sets the teleport target.
     *
     * @param teleportTarget The target location.
     */
    public void setTeleportTarget(final Location teleportTarget, final boolean resetDuel, final boolean deathCheck) {
        this.teleportTarget = teleportTarget;
        if(this instanceof Player){
            final Player player = (Player) this;
            if(deathCheck){
                if(player.getSkills().getLevel(Skills.HITPOINTS) == 0 || player.isDead()){
                    resetTeleportTarget();
                    player.getActionSender().sendMessage("You cannot teleport, you're currently dead!");
                    return;
                }
            }
            player.fightCavesWave = 0;
            getWalkingQueue().reset();
            if(resetDuel)
                Trade.declineTrade(player);
            if(cE.getAbsX() >= 2814 && cE.getAbsX() <= 2942 && cE.getAbsY() >= 5250 && cE.getAbsY() <= 5373){
                player.getActionSender().showInterfaceWalkable(-1);
            }
            World.getWorld().resetPlayersNpcs(player);
        }
    }

    /**
     * Resets the teleport target.
     */
    public void resetTeleportTarget() {
        this.teleportTarget = null;
    }

    /**
     * Gets the sprites.
     *
     * @return The sprites.
     */
    public Sprites getSprites() {
        return sprites;
    }

    /**
     * Checks if this player is teleporting.
     *
     * @return <code>true</code> if so, <code>false</code> if not.
     */
    public boolean isTeleporting() {
        return teleporting;
    }

    /**
     * Gets the list of local players.
     * @return The list of local players.
     */
    //public List<Player> getLocalPlayers() {
    //	return localPlayers;
    //}

    /**
     * Sets the teleporting flag.
     *
     * @param teleporting The teleporting flag.
     */
    public void setTeleporting(final boolean teleporting) {
        this.teleporting = teleporting;
    }

    /**
     * Gets the set of local players.
     *
     * @return The set of local players.
     */
    public Set<Player> getLocalPlayers() {
        return localPlayers;
    }

    public void addLocalPlayer(final Player player) {
        //synchronized(localPlayers) {
        localPlayers.add(player);
        //}
    }

    /**
     * Gets the set of local npcs.
     *
     * @return The set of local npcs.
     */
    public Set<NPC> getLocalNPCs() {
        return localNpcs;
    }

    /**
     * Gets the entity's index.
     *
     * @return The index.
     */
    public int getIndex() {
        if(index > Constants.MAX_PLAYERS && this instanceof Player)
            System.out.println("Player get Index over max: " + index);
        else if(index > Constants.MAX_NPCS && this instanceof NPC)
            System.out.println("Npc get index over max: " + index);
        return index;
    }

    /**
     * Sets the entity's index.
     *
     * @param index The index.
     */
    public void setIndex(final int index) {
        this.index = index;
    }

    /**
     * Destroys this entity.
     */
    public void destroy() {
        removeFromRegion(currentRegion);
        localPlayers.clear();
        localNpcs.clear();
        cE.nullShit();
        //walkingQueue.destroy();

        //cE = null;
    }

    /**
     * Deal a hit to the entity.
     *
     * @param damage The damage to be done.
     * @param type   The type of damage we are inflicting.
     */
    public abstract void inflictDamage(int damage, Damage.HitType type);

    /**
     * Removes this entity from the specified region.
     *
     * @param region The region.
     */
    public abstract void removeFromRegion(Region region);

    /**
     * Adds this entity to the specified region.
     *
     * @param region The region.
     */
    public abstract void addToRegion(Region region);

    /**
     * Gets the current location.
     *
     * @return The current location.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets the current location.
     *
     * @param location The current location.
     */
    public void setLocation(final Location location) {
        this.location = location;

        final Region newRegion = World.getWorld().getRegionManager().getRegionByLocation(location);
        if(newRegion != currentRegion){
            if(currentRegion != null){
                removeFromRegion(currentRegion);
            }
            currentRegion = newRegion;
            addToRegion(currentRegion);
        }
    }

    /**
     * Gets the update flags.
     *
     * @return The update flags.
     */
    public UpdateFlags getUpdateFlags() {
        return updateFlags;
    }

    /**
     * Gets the cooldown flags.
     *
     * @return The cooldown flags.
     */
    public EntityCooldowns getEntityCooldowns() {
        return cooldowns;
    }

    /**
     * Get this entity's hit1.
     *
     * @return The entity's hits as <code>Hit</code> type.
     */
    public Damage getDamage() {
        return damage;
    }

    /**
     * Gets the client-side index of an entity.
     *
     * @return The client-side index.
     */
    public abstract int getClientIndex();


}
