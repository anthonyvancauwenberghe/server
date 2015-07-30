package org.hyperion.rs2.model.joshyachievements.parser;

import org.hyperion.rs2.model.joshyachievements.requirement.AchievementCompletionRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.KillStreakRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.NpcKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.PlayerKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillXpRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillingObjectRequirement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class RequirementParser{

    private RequirementParser(){}

    public static Requirement parse(final Element e){
        switch(e.getAttribute("type")){
            case "KillStreak":
                return new KillStreakRequirement(intAttr(e, "kills"));
            case "NpcKill":
                return new NpcKillRequirement(intAttr(e, "npcId"), intAttr(e, "kills"));
            case "PlayerKill":
                return new PlayerKillRequirement(intAttr(e, "kills"));
            case "SkillXp":
                return new SkillXpRequirement(intAttr(e, "skill"), intAttr(e, "xp"));
            case "AchievementCompletion":
                return new AchievementCompletionRequirement(intAttr(e, "value"));
            case "SkillingObject":
                return new SkillingObjectRequirement(intAttr(e, "skill"), intAttr(e, "itemId"), intAttr(e, "quantity"));
            default:
                throw new IllegalArgumentException("RequirementParser - Invalid element type: " + e.getAttribute("type"));
        }
    }

    public static void append(final Document doc, final Element root, final Requirement req){
        final Element e = doc.createElement("requirement");
        if(req instanceof KillStreakRequirement){
            e.setAttribute("type", "KillStreak");
            e.setAttribute("kills", req.get().toString());
        }else if(req instanceof NpcKillRequirement){
            e.setAttribute("type", "NpcKill");
            e.setAttribute("npcId", Integer.toString(((NpcKillRequirement)req).getNpcId()));
            e.setAttribute("kills", req.get().toString());
        }else if(req instanceof PlayerKillRequirement){
            e.setAttribute("type", "PlayerKill");
            e.setAttribute("kills", req.get().toString());
        }else if(req instanceof SkillXpRequirement){
            e.setAttribute("type", "SkillXp");
            e.setAttribute("skill", Integer.toString(((SkillXpRequirement)req).getSkill()));
            e.setAttribute("xp", req.get().toString());
        }else if(req instanceof AchievementCompletionRequirement){
            e.setAttribute("type", "AchievementCompletion");
            e.setAttribute("value", req.get().toString());
        }else if(req instanceof SkillingObjectRequirement){
            e.setAttribute("type", "SkillingObject");
            e.setAttribute("skill", Integer.toString(((SkillingObjectRequirement)req).getSkill()));
            e.setAttribute("itemId", Integer.toString(((SkillingObjectRequirement)req).getItemId()));
            e.setAttribute("quantity", req.get().toString());
        }else
            throw new IllegalArgumentException("RequirementParser - Invalid requirement: " + req.getClass());
        root.appendChild(e);
    }
}
