package org.hyperion.rs2.model.joshyachievements.parser;

import java.util.Optional;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.joshyachievements.AchievementContext;
import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.elements;
import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.first;
import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.firstText;
import static org.hyperion.rs2.model.joshyachievements.parser.ParserUtils.intAttr;

public final class AchievementContextParser{

    private AchievementContextParser(){}

    public static AchievementContext parse(final Element e){
        final int id = intAttr(e, "id");
        final AchievementContext.Difficulty difficulty = AchievementContext.Difficulty.valueOf(e.getAttribute("difficulty"));
        final Rank forRank = Optional.ofNullable(e.getAttribute("forRank"))
                .filter(s -> s != null && !s.isEmpty())
                .map(Rank::valueOf)
                .orElse(Rank.PLAYER);
        final String title = firstText(e, "title");
        final Requirement requirement = RequirementParser.parse(first(e, "requirement"));
        final AchievementContext ctx = new AchievementContext(id, difficulty, forRank, title, requirement);
        elements(first(e, "instructions"), "line")
                .map(Element::getTextContent)
                .forEach(ctx::addInstruction);
        elements(first(e, "rewards"), "reward")
                .map(RewardParser::parse)
                .forEach(ctx::addReward);
        return ctx;
    }

    public static void append(final Document doc, final Element root, final AchievementContext ctx){
        final Element e = doc.createElement("achievement");
        e.setAttribute("id", Integer.toString(ctx.getId()));
        e.setAttribute("difficulty", ctx.getDifficulty().name());
        e.setAttribute("forRank", ctx.getForRank().name());
        final Element title = doc.createElement("title");
        title.setTextContent(ctx.getTitle());
        final Element instructions = doc.createElement("instructions");
        ctx.getInstructions().stream()
                .map(line -> {
                    final Element l = doc.createElement("line");
                    l.setTextContent(line);
                    return l;
                }).forEach(instructions::appendChild);
        final Element rewards = doc.createElement("rewards");
        ctx.getRewards().stream()
                .forEach(r -> RewardParser.append(doc, rewards, r));
        e.appendChild(title);
        RequirementParser.append(doc, e, ctx.getRequirement());
        e.appendChild(instructions);
        e.appendChild(rewards);
        root.appendChild(e);
    }
}
