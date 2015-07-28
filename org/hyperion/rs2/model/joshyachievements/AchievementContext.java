package org.hyperion.rs2.model.joshyachievements;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.hyperion.rs2.model.joshyachievements.parser.AchievementContextParser;
import org.hyperion.rs2.model.joshyachievements.parser.ParserUtils;
import org.hyperion.rs2.model.joshyachievements.reward.Reward;
import org.w3c.dom.Document;

public class AchievementContext{

    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD
    }

    private static final Map<Integer, AchievementContext> MAP = new TreeMap<>();

    private final int id;
    private final Difficulty difficulty;
    private final String title;
    private final Requirement requirement;
    private final List<String> instructions;
    private final List<Reward> rewards;

    public AchievementContext(final int id, final Difficulty difficulty, final String title, final Requirement requirement){
        this.id = id;
        this.difficulty = difficulty;
        this.title = title;
        this.requirement = requirement;

        instructions = new ArrayList<>();
        rewards = new ArrayList<>();
    }

    public int getId(){
        return id;
    }

    public Difficulty getDifficulty(){
        return difficulty;
    }

    public String getTitle(final Player player){
        return Optional.ofNullable(title).orElse(requirement.toString(player));
    }

    public List<String> getInstructions(){
        return instructions;
    }

    public void addInstruction(final String line){
        instructions.add(line);
    }

    public int applyRequirement(final Player player){
        return requirement.apply(player);
    }

    public <T extends Requirement> T getRequirement(){
        return (T) requirement;
    }

    public int getRewardCount(){
        return rewards.size();
    }

    public void reward(final Player player){
        rewards.forEach(r -> r.apply(player));
    }

    public List<Reward> getRewards(){
        return rewards;
    }

    public void addReward(final Reward reward){
        rewards.add(reward);
    }

    private static void put(final AchievementContext ctx){
        MAP.put(ctx.getId(), ctx);
    }

    public static AchievementContext get(final int id){
        return MAP.get(id);
    }

    public static Stream<AchievementContext> stream(){
        return MAP.values().stream();
    }

    public static Stream<AchievementContext> stream(final Predicate<AchievementContext> filter){
        return stream().filter(filter);
    }

    public static Optional<AchievementContext> findFirst(final AchievementTracker tracker, final Predicate<AchievementContext> filter){
        return stream(filter)
                .filter(a -> tracker.getProgress(a).isNotComplete())
                .min(Comparator.comparingInt(a -> a.getRequirement().apply(tracker.getPlayer())));
    }

    public static boolean load(){
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder bldr = factory.newDocumentBuilder();
            final Document doc = bldr.parse(new File("./data/achievements.xml"));
            ParserUtils.elements(doc.getDocumentElement(), "achievement")
                    .map(AchievementContextParser::parse)
                    .forEach(AchievementContext::put);
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
