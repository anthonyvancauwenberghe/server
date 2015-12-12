package org.hyperion.rs2.model.combat;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Graphic;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.attack.BulwarkBeast;
import org.hyperion.rs2.model.combat.attack.CorporealBeast;
import org.hyperion.rs2.model.combat.attack.TormentedDemon;
import org.hyperion.rs2.model.content.bounty.BountyPerkHandler;
import org.hyperion.rs2.model.content.minigame.FightPits;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CombatEntity {

    private final List<CombatEntity> attackers = new LinkedList<CombatEntity>();
    private final Entity entity;
    private final Map<String, Integer> damageDealt = new HashMap<String, Integer>();
    public Attack attack = null;
    public NPC summonedNpc = null;
    public long lastHit = System.currentTimeMillis() - 30000;
    public long predictedAtk = System.currentTimeMillis();
    public int trainSkill = 0;
    public int specialArmour = -1;
    public int bowType = 0;//accurate, rapid, long
    public int morrigansLeft = 0;
    public boolean isDoingAtk = false;
    //public long predictedAtk3 = System.currentTimeMillis();
    public boolean vacating = false;
    private Player player = null;
    private NPC n = null;
    private boolean isPoisoned = false;
    //public long predictedAtk2 = System.currentTimeMillis();
    private int atkSpeed = 2000;
    private long freezeTimer = 0;//can move after this time
    private int autoCastId = -1;
    private int weaponPoisons = 0;
    private int atkEmote = 422;
    private int defEmote = 404;
    private int atkType = 2;//start with str
    private CombatEntity opponent = null;
    //private int[] magicAttacks = new int[2];//you can change this to queue up magic attacks so they are executed in the right order
    private int magicAttackNext = 0;

    public CombatEntity(final Entity e) {
        entity = e;
        if(getEntity() instanceof Player){
            player = (Player) getEntity();
        }else{
            n = (NPC) getEntity();
        }
    }

    //use this for end effect from now on, don't crowd the inflictdamg
    public static int endEffect(final NPC n, final Entity attacker, int damage, final int style) {
        final int id = n.getDefinition().getId();
        if(attacker instanceof Player)
            return TormentedDemon.getReduction(n, (Player) attacker, damage);
        if(id == 8133){
            if(attacker instanceof Player){
                final Player atk = (Player) attacker;
                damage = CorporealBeast.reduceDamage(atk, damage, style);
            }
        }
        if(id == 10106 && attacker instanceof Player){
            final Player atk = (Player) attacker;
            BulwarkBeast.handleRecoil(atk, damage);
        }
        if(id == 8596){
            if(style == Constants.MELEE)
                damage = 0;
        }
        return damage;
    }

    public void nullShit() {
        opponent = null;
        attackers.clear();
        damageDealt.clear();
        /*entity = null;
        p = null;
		n = null;
		summonedNpc = null;*/
    }

    public Map<String, Integer> getDamageDealt() {
        return damageDealt;
    }

    public NPC getFamiliar() {
        return summonedNpc;
    }

    public boolean isFrozen() {
        if(System.currentTimeMillis() < freezeTimer){
            if(player != null){
                if(Rank.hasAbility(player, Rank.ADMINISTRATOR)){
                    player.debugMessage("You are frozen for another: " + (freezeTimer - System.currentTimeMillis()) + "MS");
                }
            }
            return true;
        }
        return false;
    }

    public boolean canBeFrozen() {
        return !(System.currentTimeMillis() < (freezeTimer + 5000L));
    }

    public void setFreezeTimer(final long time) {
        freezeTimer = System.currentTimeMillis() + time;
    }

    public boolean isNpcAttackAble() {
        if(getEntity() instanceof Player)
            return true;
        else if(n.maxHealth <= 0){
            //System.out.println("MaxHealth =  " + n.maxHealth + " Name " + n.getDefinition().getName()  + " id " + n.getDefinition().getId());
            return false;
        }
        return true;
    }

    public int getNextMagicAtk() {
        return magicAttackNext;
    }

    public void addSpellAttack(final int j) {
        //getPlayer().getActionSender().sendMessage("Add Spell Atk : " + j);
        magicAttackNext = j;
    }

    public void deleteSpellAttack() {
        magicAttackNext = 0;
    }

    public CombatEntity getOpponent() {
        return opponent;
    }

    public void setOpponent(final CombatEntity e) {

        opponent = e;
    }

    public List<CombatEntity> getAttackers() {
        return attackers;
    }

    public Player getPlayer() {
        return player;
    }

    public NPC getNPC() {
        return n;
    }

    public Optional<NPC> _getNPC() {
        return Optional.ofNullable(n);
    }

    public Optional<Player> _getPlayer() {
        return Optional.ofNullable(player);
    }

    public boolean isPoisoned() {
        return isPoisoned;
    }

    public void setPoisoned(final boolean b) {
        isPoisoned = b;
    }

    public int getAtkSpeed() {
        return atkSpeed;
    }

    public void setAtkSpeed(final int a) {
        //System.out.println("Setting Attack speed: " + a);
        atkSpeed = a;
    }

    public int getAutoCastId() {
        return autoCastId;
    }

    public void setAutoCastId(final int a) {
        autoCastId = a;
    }

    public int getAtkEmote() {
        return atkEmote;
    }

    public void setAtkEmote(final int a) {
        atkEmote = a;
    }

    public int getDefEmote() {
        return defEmote;
    }

    public void setDefEmote(final int a) {
        defEmote = a;
    }

    public int getWeaponPoison() {
        return weaponPoisons;
    }

    public void setWeaponPoison(final int a) {
        weaponPoisons = a;
    }

    public int getAtkType() {
        return atkType;
    }

    public void setAtkType(final int i) {
        //getPlayer().getActionSender().sendMessage("Your atkType is now " + i);
        atkType = i;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean canMove() {
        return !isFrozen();
    }

    public int getFightType() {
        return 1;
    }

    /**
     * Gets the player who has caused most damage to the CombatEntity.
     *
     * @return
     */
    public Player getKiller() {
        String winner = null;
        int max = 0;
        for(final Map.Entry<String, Integer> entry : damageDealt.entrySet()){
            final int damage = entry.getValue();
            if(damage > max){
                winner = entry.getKey();
                max = damage;
            }
            //System.out.println("killer: "+m+" : "+entry.getKey());
        }
        damageDealt.clear();
        if(winner == null)
            return null;
        return World.getWorld().getPlayer(winner);
    }

    public int hit(int damage, final Entity attacker, final boolean poison, final int style) {
        try{
            if(this == null)
                return 0;
            /**
             * Since this seems to be the root of hitting (returns method {@link #inflictDamage})
             * and that decrements player's HP, if inflictDamage is not called if the attacker is dead
             * Then the glitch will not occur - hopefully ^.^
             */
            if(player != null && attacker != null && attacker.isDead() && attacker instanceof Player){
                return 0;
            }
            if(attacker == null && player != null){
                return player.inflictDamage(damage, attacker, poison, style);
            }
            //now that poison etc has been dealt
            /*if(attacker == null)
				return 0;*/
            //Update last attacker
            if(attacker != null && getEntity() instanceof Player && attacker instanceof Player){
                getPlayer().getLastAttack().updateLastAttacker(attacker.cE.getPlayer().getName());
            }
            int lastDamage = 0;
            //Load last damage dealt by attacker
            if(damageDealt != null && attacker != null){
                if(attacker instanceof Player){
                    if(damageDealt.containsKey(((Player) attacker).getName())){
                        lastDamage = damageDealt.get(((Player) attacker).getName());
                    }
                    //Update damage dealt
                    if(attacker instanceof Player){
                        damageDealt.put(((Player) attacker).getName(), lastDamage + damage);
                    }
                }
            }
            if(getEntity() instanceof Player){
                if(attacker instanceof Player){
                    final Player atk = (Player) attacker;
                    if(FightPits.inGame(atk)){
                        atk.increasePitsDamage(damage);
                    }
                    BountyPerkHandler.appendPrayerLeechPerk(atk, getPlayer(), damage);
                }
                return player.inflictDamage(damage, attacker, poison, style);
            }else{
                damage = endEffect(n, attacker, damage, style);
                return n.inflictDamage(damage, attacker, poison, style);
            }
        }catch(final Exception e){
            e.printStackTrace();
            System.out.println("Error hitting!");
            return 0;
        }
    }

    public CombatEntity getCurrentAtker() {
        final Object[] o = attackers.toArray();
        if(o != null){
            if(o.length > 0){
                if(System.currentTimeMillis() - lastHit > 10000){
                    attackers.clear();
                    return null;
                }
                return ((CombatEntity) o[0]);
            }
        }
        return null;
    }

    public int getAbsX() {
        return getEntity().getLocation().getX();
    }

    public int getAbsY() {
        return getEntity().getLocation().getY();
    }

    public int getAbsZ() {
        return getEntity().getLocation().getZ();
    }

    public int getCombat() {
        if(getEntity() instanceof Player)
            return player.getSkills().getCombatLevel();
        else
            return n.getDefinition().combat();
    }

    public void doAtkEmote() {
        isDoingAtk = true;
        if(getEntity() instanceof Player)
            doAnim(atkEmote);
        else
            doAnim(n.getDefinition().getAtkEmote(0));
    }

    public int getOffsetX() {
        if(getEntity() instanceof Player)
            return 0;
        return n.getDefinition().sizeX();
    }

    public int getOffsetY() {
        if(getEntity() instanceof Player)
            return 0;
        return n.getDefinition().sizeY();
    }

    public void doDefEmote() {
        if(isDoingAtk)
            return;
        if(getEntity() instanceof Player){
            if(player.getNpcState()){
                final NPCDefinition def = NPCDefinition.forId(player.getNpcId());
                if(def.doesDefEmote())
                    doAnim(def.blockEmote());
                else

                    doAnim(defEmote);
            }else
                doAnim(defEmote);
        }else if(n.getDefinition().blockEmote() > 0)
            doAnim(n.getDefinition().blockEmote());
    }

    public void doAnim(final int id) {
        if(id < 1)
            return;

        getEntity().playAnimation(Animation.create(id, 0));
    }

    public void doGfx(final int id) {
        if(id < 1)
            return;
        doGfx(id, 6553600);
    }

    public void doGfx(final int id, final int delay) {
        getEntity().playGraphics(Graphic.create(id, delay));
    }

    public void face(final int x, final int y) {
        getEntity().face(Location.create(x, y, 0));
    }

    public void face(final int x, final int y, final boolean test) {
        getEntity().face(Location.create(x, y, 0));
    }

    public int getSlotId(final Entity e) {
        if(e instanceof Player){
            if(getEntity() instanceof NPC){
                return (-getEntity().getIndex() - 1);
            }else{
                return (getEntity().getIndex() + 1);
            }
        }else{
            if(getEntity() instanceof Player){
                return (-getEntity().getIndex() - 1);
            }else{
                return (getEntity().getIndex() + 1);
            }
        }
    }

    public int meleeDef(final int fightType) {
        if(getEntity() instanceof Player)
            return Defence.meleeDef((Player) getEntity(), fightType);
        return 10;
    }

    public int rangeDef() {
        if(getEntity() instanceof Player)
            return Defence.rangeDef((Player) getEntity());
        return 10;
    }

    public int mageDef() {
        if(getEntity() instanceof Player)
            return Defence.mageDef((Player) getEntity());
        return 10;
    }

    public int meleeAtk(final boolean bool) {
        if(getEntity() instanceof Player)
            return Defence.meleeAtk((Player) getEntity(), atkType, bool);
        return 10;
    }

    public int rangeAtk(final boolean bool) {
        if(getEntity() instanceof Player)
            return Defence.rangeAtk((Player) getEntity(), bool);
        return 10;
    }

    public int mageAtk(final int spellId) {
        if(getEntity() instanceof Player)
            return Defence.mageAtk((Player) getEntity(), spellId);
        return 10;
    }
}