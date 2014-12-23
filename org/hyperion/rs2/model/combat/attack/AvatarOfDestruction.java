package org.hyperion.rs2.model.combat.attack;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.impl.NpcDeathEvent;
import org.hyperion.rs2.model.Attack;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatCalculation;
import org.hyperion.rs2.model.combat.CombatEntity;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/23/14
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class AvatarOfDestruction implements Attack {

    public static void loadDefinitions() {
        final int[] bonus = new int[10];
        Arrays.fill(bonus, 487);
        NPCDefinition.getDefinitions()[8596] =
                NPCDefinition.create(8597, 5000, 525, bonus, 11199, 11198, new int[]{11197}, 3, "Avatar of Destruction", 120);
    }

    @Override
    public String getName() {
        return "Avatar of Destruction";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] npcIds() {
        return new int[]{8596};  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static final int MAX_DAMAGE = 67;

    @Override
    public int handleAttack(NPC n, CombatEntity attack) {
        if(attack == null)
            return 1;
        if(n.cE.predictedAtk > System.currentTimeMillis()) {
            return 6;
        }
        int distance = attack.getEntity().getLocation().distance(n.getLocation());
        if(distance < 5) {
            n.getCombat().doAtkEmote();
                int tempDamage = CombatCalculation.getCalculatedDamage(n, attack.getEntity(), Combat.random(MAX_DAMAGE), Constants.MELEE);
                Combat.npcAttack(n, attack, tempDamage, 300, Constants.MELEE);
            attack._getPlayer().ifPresent(p -> {
                p.getSkills().detractLevel(Skills.PRAYER, tempDamage);
                p.sendMessage("The avatar drains your prayer");
            });
        } else  if (distance < 10){
            return 0; //follow player
        }
        return 0;
    }
}
