package org.hyperion.rs2.model.joshyachievements.parser;

import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.hyperion.rs2.model.joshyachievements.requirement.KillStreakRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.NpcKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.PlayerKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillRequirement;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class RequirementParser{

    private RequirementParser(){}

    public static Requirement parse(final Element e){
        switch(e.getAttribute("type")){
            case "killStreak":
                return new KillStreakRequirement(intAttr(e, "kills"));
            case "npcKill":
                return new NpcKillRequirement(intAttr(e, "npc"), intAttr(e, "kills"));
            case "playerKill":
                return new PlayerKillRequirement(intAttr(e, "kills"));
            case "skill":
                return new SkillRequirement(intAttr(e, "skill"), intAttr(e, "xp"));
            default:
                return null;
        }
    }
}
