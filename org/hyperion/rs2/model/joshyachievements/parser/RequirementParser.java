package org.hyperion.rs2.model.joshyachievements.parser;

import org.hyperion.rs2.model.joshyachievements.requirement.AchievementCompletionRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.ItemOpenRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.KillStreakRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.NpcKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.PlayerKillRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillXpRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.SkillingObjectRequirement;
import org.hyperion.rs2.model.joshyachievements.requirement.VoteRequirement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;
import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.ints;

public final class RequirementParser{

    private RequirementParser(){}

    public static Requirement parse(final Element e){
        switch(e.getAttribute("type")){
            case "KillStreak":
                return new KillStreakRequirement(intAttr(e, "kills"));
            case "NpcKill":
                return new NpcKillRequirement(ints(e, "npcs", "npc", "id"), intAttr(e, "kills"));
            case "PlayerKill":
                return new PlayerKillRequirement(intAttr(e, "kills"));
            case "SkillXp":
                return new SkillXpRequirement(intAttr(e, "skill"), intAttr(e, "xp"));
            case "AchievementCompletion":
                return new AchievementCompletionRequirement(intAttr(e, "value"));
            case "SkillingObject":
                return new SkillingObjectRequirement(intAttr(e, "skill"), ints(e, "items", "item", "id"), intAttr(e, "quantity"));
            case "ItemOpen":
                return new ItemOpenRequirement(ints(e, "items", "item", "id"), intAttr(e, "quantity"));
            case "Vote":
                return new VoteRequirement(intAttr(e, "amount"));
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
            final NpcKillRequirement nkr = (NpcKillRequirement) req;
            e.setAttribute("type", "NpcKill");
            e.setAttribute("kills", req.get().toString());
            final Element npcs = doc.createElement("npcs");
            nkr.getNpcIds().stream()
                    .map(id -> {
                        final Element npc = doc.createElement("npc");
                        npc.setAttribute("id", Integer.toString(id));
                        return npc;
                    }).forEach(npcs::appendChild);
            e.appendChild(npcs);
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
            final SkillingObjectRequirement sor = (SkillingObjectRequirement) req;
            e.setAttribute("type", "SkillingObject");
            e.setAttribute("skill", Integer.toString(sor.getSkill()));
            e.setAttribute("quantity", req.get().toString());
            final Element items = doc.createElement("items");
            sor.getItemIds().stream()
                    .map(id -> {
                        final Element item = doc.createElement("item");
                        item.setAttribute("id", Integer.toString(id));
                        return item;
                    }).forEach(items::appendChild);
            e.appendChild(items);
        }else if(req instanceof ItemOpenRequirement){
            final ItemOpenRequirement ior = (ItemOpenRequirement) req;
            e.setAttribute("type", "ItemOpen");
            e.setAttribute("quantity", ior.get().toString());
            final Element items = doc.createElement("items");
            ior.getItemIds().stream()
                    .map(id -> {
                        final Element item = doc.createElement("item");
                        item.setAttribute("id", Integer.toString(id));
                        return item;
                    }).forEach(items::appendChild);
            e.appendChild(items);
        }else if(req instanceof VoteRequirement){
            e.setAttribute("type", "Vote");
            e.setAttribute("amount", req.get().toString());
        }else
            throw new IllegalArgumentException("RequirementParser - Invalid requirement: " + req.getClass());
        root.appendChild(e);
    }
}
