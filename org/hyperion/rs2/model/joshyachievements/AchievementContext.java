package org.hyperion.rs2.model.joshyachievements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.joshyachievements.parser.AchievementContextParser;
import org.hyperion.rs2.model.joshyachievements.parser.ParserUtils;
import org.hyperion.rs2.model.joshyachievements.requirement.Requirement;
import org.hyperion.rs2.model.joshyachievements.reward.Reward;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AchievementContext{

    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD,
        ELITE
    }

    private static final Map<Integer, AchievementContext> MAP = new TreeMap<>();

    private final int id;
    private final Difficulty difficulty;
    private final Rank forRank;
    private final String title;
    private final Requirement requirement;
    private final List<String> instructions;
    private final List<Reward> rewards;

    public AchievementContext(final int id, final Difficulty difficulty, final Rank forRank, final String title, final Requirement requirement){
        this.id = id;
        this.difficulty = difficulty;
        this.forRank = forRank;
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

    public Rank getForRank(){
        return forRank;
    }

    public boolean isFor(final Player player){
        return Rank.hasAbility(player, forRank);
    }

    public String getTitle(){
        return title;
    }

    public List<String> getInstructions(){
        return instructions;
    }

    public void addInstruction(final String line){
        instructions.add(line);
    }

    public int getRequirementMax(){
        return requirement.get();
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

    public String toString(){
        return String.format(
                "===================================================%nAchievementContext(id=%d,difficulty=%s,forRank=%s)%n%s - %s%n%s%nRewards: %,d%n%s%n===================================================",
                id, difficulty, forRank, title, requirement,
                String.join("\n", instructions),
                rewards.size(),
                rewards.stream().map(Reward::toString).collect(Collectors.joining("\n"))
        );
    }

    public static void put(final AchievementContext ctx){
        MAP.put(ctx.getId(), ctx);
    }

    public static AchievementContext get(final int id){
        return MAP.get(id);
    }

    public static int count(){
        return MAP.size();
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
                .min(Comparator.comparingInt(AchievementContext::getRequirementMax));
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

    public static boolean save(){
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder bldr = factory.newDocumentBuilder();
            final Document doc = bldr.newDocument();
            final Element root = doc.createElement("achievements");
            stream().forEach(a -> AchievementContextParser.append(doc, root, a));
            doc.appendChild(root);
            final Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(new File("./data/achievements.xml"))));
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
