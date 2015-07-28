package org.hyperion.rs2.model.joshyachievements.parser;

import java.util.Optional;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;
import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.elements;
import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.first;
import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class AchievementContextParser{

    private AchievementContextParser(){}

    public static AchievementContext parse(final Element e){
        final int id = intAttr(e, "id");
        final AchievementContext.Difficulty difficulty = AchievementContext.Difficulty.valueOf(e.getAttribute("difficulty"));
        final Requirement requirement = RequirementParser.parse(first(e, "condition"));
        final String title = Optional.ofNullable(first(e, "title"))
                .map(Element::getTextContent)
                .orElse(null);
        final AchievementContext ctx = new AchievementContext(id, difficulty, title, requirement);
        elements(first(e, "instructions"), "line")
                .map(Element::getTextContent)
                .forEach(ctx::addInstruction);
        elements(first(e, "rewards"), "reward")
                .map(RewardParser::parse)
                .forEach(ctx::addReward);
        return ctx;
    }
}
