package org.hyperion.rs2.model.joshyachievements.parser;

import org.hyperion.rs2.model.joshyachievements.reward.ItemReward;
import org.hyperion.rs2.model.joshyachievements.reward.PointsReward;
import org.hyperion.rs2.model.joshyachievements.reward.Reward;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class RewardParser{

    private RewardParser(){}

    public static Reward parse(final Element e){
        switch(e.getAttribute("type")){
            case "item":
                return new ItemReward(intAttr(e, "itemId"), intAttr(e, "quantity"), "true".equals(e.getAttribute("preferInventory")));
            case "points":
                return new PointsReward(PointsReward.Type.valueOf(e.getAttribute("kind")), intAttr(e, "amount"));
            default:
                return null;
        }
    }
}
