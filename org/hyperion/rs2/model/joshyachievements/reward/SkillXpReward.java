package org.hyperion.rs2.model.joshyachievements.reward;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

public class SkillXpReward implements Reward{

    private final int skill;
    private final int xp;

    public SkillXpReward(final int skill, final int xp){
        this.skill = skill;
        this.xp = xp;
    }

    public int getSkill(){
        return skill;
    }

    public int getXp(){
        return xp;
    }

    public void apply(final Player player){
        final String name = Skills.SKILL_NAME[skill];
        final int current = player.getSkills().getExperience(skill);
        final int max = Math.max(current + xp, Skills.MAXIMUM_EXP);
        final int gained = max - current;
        if(gained == 0){
            player.sendf("You already have the maximum XP in %s!", name);
            return;
        }
        player.getSkills().setExperience(skill, max);
        player.sendf("You have gained %,d %s XP!", gained, name);
    }

    public String toString(){
        return String.format("SkillXpReward(skill=%d,xp=%,d)", skill, xp);
    }
}
