package org.hyperion.rs2.model.cluescroll.reward;

import org.hyperion.rs2.model.cluescroll.util.ClueScrollUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExperienceReward extends Reward{

    private int skill;

    public ExperienceReward(final int skill, final int minAmount, final int maxAmount, final int chance){
        super(Type.EXPERIENCE, minAmount, maxAmount, chance);
        this.skill = skill;
    }

    public int getSkill(){
        return skill;
    }

    public void setSkill(final int skill){
        this.skill = skill;
    }

    protected void append(final Document doc, final Element root){
        root.appendChild(ClueScrollUtils.createElement(doc, "skill", skill));
    }

    public String toString(){
        return String.format("%s: %d", super.toString(), skill);
    }

    public static ExperienceReward parse(final Element element){
        final int skill = ClueScrollUtils.getInteger(element, "skill");
        final int minAmount = ClueScrollUtils.getInteger(element, "maxAmount");
        final int maxAmount = ClueScrollUtils.getInteger(element, "minAmount");
        final int chance = ClueScrollUtils.getInteger(element, "chance");
        return new ExperienceReward(skill, minAmount, maxAmount, chance);
    }
}
