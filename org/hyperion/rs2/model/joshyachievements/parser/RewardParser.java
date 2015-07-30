package org.hyperion.rs2.model.joshyachievements.parser;

import org.hyperion.rs2.model.joshyachievements.reward.ItemReward;
import org.hyperion.rs2.model.joshyachievements.reward.PointsReward;
import org.hyperion.rs2.model.joshyachievements.reward.Reward;
import org.hyperion.rs2.model.joshyachievements.reward.SkillXpReward;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class RewardParser{

    private RewardParser(){}

    public static Reward parse(final Element e){
        switch(e.getAttribute("type")){
            case "Item":
                return new ItemReward(intAttr(e, "itemId"), intAttr(e, "quantity"), "true".equals(e.getAttribute("preferInventory")));
            case "Points":
                return new PointsReward(PointsReward.Type.valueOf(e.getAttribute("kind")), intAttr(e, "amount"));
            case "SkillXp":
                return new SkillXpReward(intAttr(e, "skill"), intAttr(e, "xp"));
            default:
                throw new IllegalArgumentException("RewardParser - Invalid element type " + e.getAttribute("type"));
        }
    }

    public static void append(final Document doc, final Element root, final Reward reward){
        final Element e = doc.createElement("reward");
        if(reward instanceof ItemReward){
            final ItemReward ir = (ItemReward) reward;
            e.setAttribute("type", "Item");
            e.setAttribute("itemId", Integer.toString(ir.getItemId()));
            e.setAttribute("quantity", Integer.toString(ir.getQuantity()));
            e.setAttribute("preferInventory", Boolean.toString(ir.isPreferInventory()));
        }else if(reward instanceof PointsReward){
            final PointsReward pr = (PointsReward) reward;
            e.setAttribute("type", "Points");
            e.setAttribute("kind", pr.getType().name());
            e.setAttribute("amount", Integer.toString(pr.getAmount()));
        }else if(reward instanceof SkillXpReward){
            final SkillXpReward sxr = (SkillXpReward) reward;
            e.setAttribute("type", "SkillXp");
            e.setAttribute("skill", Integer.toString(sxr.getSkill()));
            e.setAttribute("xp", Integer.toString(sxr.getXp()));
        }else
            throw new IllegalArgumentException("RewardParser - Invalid reward: " + reward.getClass());
        root.appendChild(e);
    }
}
