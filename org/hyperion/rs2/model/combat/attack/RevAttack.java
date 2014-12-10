package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatAssistant;
import org.hyperion.rs2.model.combat.CombatEntity;
import org.hyperion.rs2.model.combat.SpiritShields;
import org.hyperion.rs2.model.combat.pvp.PvPArmourStorage;
import org.hyperion.rs2.model.content.bounty.rewards.BHDrop;
import org.hyperion.util.ArrayUtils;
import org.hyperion.util.Misc;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/10/14
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class RevAttack implements Attack {

    public String getName() {
        return "";
    }

    static List<NPCDrop> drops = new ArrayList<>();

    private static final Map<Integer, NPCDefinition> revs;
    private static final int[] revIds;
    static {
        final int[] bonus = new int[10];
        Arrays.fill(bonus, 400);
        revs = new HashMap<>();
        int id = 6692;
        revs.put(id, NPCDefinition.create(id--, 500, 126, bonus,7442, 7443, new int[]{7441, 7508, 7522},1, "Revenant Knight", 50));
        revs.put(id, NPCDefinition.create(id--, 500, 120, bonus, 7468, 7469, new int[]{7467, 7515, 7514}, 1, "Revenant Dark Beast", 49));
        revs.put(id, NPCDefinition.create(id--, 450, 105, bonus, 7412, 7413, new int[]{7411, 7505, 7518}, 2, "Revenant Ork", 48));
        revIds = ArrayUtils.fromInteger(revs.keySet().toArray(new Integer[revs.keySet().size()]));

        for(final NPCDefinition def : revs.values()) {
            if(def != null) {
                for(final int i : PvPArmourStorage.getArmours())
                    def.getDrops().add(NPCDrop.create(i, 1, 1, def.combat() / 10));
                def.getDrops().add(NPCDrop.create(13895, 1, 1, def.combat() / 10));
                def.getDrops().add(NPCDrop.create(13889, 1, 1, def.combat()/10));

            }
        }
    }

    public static int[] getRevs() {
        return revIds;
    }

    public static boolean isRev(final int id) {
        return ArrayUtils.contains(id, revIds);
    }

    public static NPCDefinition loadDefinition(final int id) {
        return revs.get(id);
    }


    @Override
    public int[] npcIds() {
        return revIds;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int handleAttack(NPC n, CombatEntity attack) {
        if(n.cE.predictedAtk > System.currentTimeMillis()) {
            //System.out.println("Predicted attack waiting.");
            return 6;//we dont want to reset attack but just wait another 500ms or so...
        }
        final int distance = n.getLocation().distance(attack.getEntity().getLocation());

        if(attack.getEntity() instanceof NPC)
            return 0;
        if(Misc.random(3) == 1 && n.health < n.maxHealth/2) {
            n.health += 25;
            return 5;
        }
        final Player player = attack.getPlayer();
        final boolean hasPrayMagic = player.getPrayers().isEnabled(Prayers.CURSE_DEFLECT_MAGIC) || player.getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_MAGE);
        final boolean hasPrayMelee = player.getPrayers().isEnabled(Prayers.CURSE_DEFLECT_MELEE) || player.getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_MELEE);
        final boolean hasPrayRange = player.getPrayers().isEnabled(Prayers.CURSE_DEFLECT_RANGED) || player.getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_RANGE);
        if(distance > 10)
            return 1;
        else if(distance > 7)
            return 0;
        else if(distance > 3)
        {
            if(hasPrayMagic)
                handleRangeAttack(n, player);
            else
                handleMagicAttack(n, player);
        } else {
        if(hasPrayMelee) {
            if(Misc.random(1) == 0) {
                handleRangeAttack(n, player);
            } else {
                handleMagicAttack(n, player);
            }
        } else if(hasPrayMagic) {
            if(Misc.random(1) == 0) {
                handleRangeAttack(n, player);
            } else {
                handleMeleeAttack(n, player);
            }
        } else handleMeleeAttack(n, player);
        }
        return 5;
    }

    public void handleMagicAttack(NPC n, Player attack) {
        n.cE.doAnim(n.getDefinition().getAtkEmote(1));
        final int maxHit = n.getDefinition().combat()/4;
        int damage = attack.getInflictDamage(Combat.random(maxHit), n, false, Constants.MAGE);
        damage -= Math.round(Misc.random(CombatAssistant.calculateMageDef(attack) - n.getDefinition().combat())/5F);
        damage = SpiritShields.applyEffects(attack.cE, damage);
        if(damage <= 0)
            damage = 0;
        attack.cE.hit(Combat.random(maxHit), n, false, Constants.MAGE);
        n.cE.predictedAtk = System.currentTimeMillis() + 1800;

    }
    public void handleRangeAttack(NPC n, Player attack) {

        n.cE.doAnim(n.getDefinition().getAtkEmote(2));
        final int maxHit = (int)(n.getDefinition().combat()/3.5);
        int damage = attack.getInflictDamage(Combat.random(maxHit), n, false, Constants.RANGE);
        damage -= Math.round(Misc.random(CombatAssistant.calculateMeleeDefence(attack) - n.getDefinition().combat())/10F);
        damage = SpiritShields.applyEffects(attack.cE, damage);
        if(damage <= 0)
            damage = 0;
        attack.cE.hit(Combat.random(maxHit), n, false, Constants.RANGE);
        n.cE.predictedAtk = System.currentTimeMillis() + 2400;

    }
    public void handleMeleeAttack(NPC n, Player attack) {
        n.cE.doAnim(n.getDefinition().getAtkEmote(0));
        final int maxHit = n.getDefinition().combat()/5;
        int damage = attack.getInflictDamage(Combat.random(maxHit), n, false, Constants.MELEE);
        damage -= Math.round(Misc.random(CombatAssistant.calculateMeleeDefence(attack) - n.getDefinition().combat())/10F);
        damage = SpiritShields.applyEffects(attack.cE, damage);
        if(damage <= 0)
            damage = 0;
        attack.cE.hit(Combat.random(maxHit), n, false, Constants.MELEE);
        n.cE.predictedAtk = System.currentTimeMillis() + 1200;
    }
}
