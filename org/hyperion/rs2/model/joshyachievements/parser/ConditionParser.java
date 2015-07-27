package org.hyperion.rs2.model.joshyachievements.parser;

import org.hyperion.rs2.model.joshyachievements.condition.Condition;
import org.hyperion.rs2.model.joshyachievements.condition.KillStreakCondition;
import org.hyperion.rs2.model.joshyachievements.condition.NpcKillCondition;
import org.hyperion.rs2.model.joshyachievements.condition.PlayerKillCondition;
import org.hyperion.rs2.model.joshyachievements.condition.SkillCondition;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class ConditionParser{

    private ConditionParser(){}

    public static Condition parse(final Element e){
        switch(e.getAttribute("type")){
            case "killStreak":
                return new KillStreakCondition(intAttr(e, "kills"));
            case "npcKill":
                return new NpcKillCondition(intAttr(e, "npc"), intAttr(e, "kills"));
            case "playerKill":
                return new PlayerKillCondition(intAttr(e, "kills"));
            case "skill":
                return new SkillCondition(intAttr(e, "skill"), intAttr(e, "xp"));
            default:
                return null;
        }
    }
}
