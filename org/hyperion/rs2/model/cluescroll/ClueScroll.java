package org.hyperion.rs2.model.cluescroll;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.cluescroll.requirement.Requirement;
import org.hyperion.rs2.model.cluescroll.reward.Reward;
import org.hyperion.rs2.model.cluescroll.util.ClueScrollUtils;
import org.hyperion.util.Misc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ClueScroll {

    public enum Difficulty{
        EASY,
        MEDIUM,
        HARD,
        ELITE
    }

    public enum DefaultRewards {
        EASY(),
        MEDIUM(),
        HARD(),
        ELITE();

        Reward[] rewards;

        DefaultRewards(Reward... rewards) {
            this.rewards = rewards;
        }
    }

    public List<Reward> getRewards(Difficulty difficulty) {
        List<Reward> rewards = new ArrayList<>();
        for(DefaultRewards reward : DefaultRewards.values()) {
            if(reward.getClass().getSimpleName().equalsIgnoreCase(difficulty.name()))
                for(int i = 0; i < reward.rewards.length; i++)
                    rewards.add(reward.rewards[i]);
        }
        return rewards;
    }

    public enum Trigger{
        CRY(Animation.CRY),
        THINK(Animation.THINKING),
        WAVE(Animation.WAVE),
        BOW(Animation.BOW),
        ANGRY(Animation.ANGRY),
        YES(Animation.YES_EMOTE),
        NO(Animation.NO_EMOTE),
        SHRUG(Animation.SHRUG),
        CHEER(Animation.CHEER),
        BECKON(Animation.BECKON),
        LAUGH(Animation.LAUGH),
        JUMP_FOR_JOY(Animation.JOYJUMP),
        YAWN(Animation.YAWN),
        DANCE(Animation.DANCE),
        JIG(Animation.JIG),
        SPIN(Animation.SPIN),
        HEAD_BANG(Animation.HEADBANG),
        BLOW_KISS(Animation.BLOW_KISS),
        PANIC(Animation.PANIC),
        RASPBERRY(Animation.RASPBERRY),
        CLAP(Animation.CLAP),
        SALUTE(Animation.SALUTE),
        GOBLIN_BOW(Animation.GOBLIN_BOW),
        GOBLIN_SALUTE(Animation.GOBLIN_DANCE),
        GLASS_BOX(Animation.GLASS_BOX),
        CLIMB_ROPE(Animation.CLIMB_ROPE),
        LEAN_ON_AIR(Animation.LEAN),
        GLASS_WALL(Animation.GLASS_WALL),
        ATTACK_CAPE(4959),
        DEFENCE_CAPE(4961),
        STRENGTH_CAPE(4981),
        HITPOINTS_CAPE(4971),
        RANGING_CAPE(4973),
        PRAYER_CAPE(4979),
        MAGIC_CAPE(4939),
        COOKING_CAPE(4955),
        WOODUCTTING_CAPE(4957),
        FLETCHING_CAPE(4937),
        FISHING_CAPE(4951),
        FIREMAKING_CAPE(4975),
        CRAFTING_CAPE(4949),
        SMITHING_CAPE(4943),
        MINING_CAPE(4941),
        HERBLORE_CAPE(4969),
        AGILITY_CAPE(4977),
        THIEVING_CAPE(4965),
        SLAYER_CAPE(4967),
        FARMING_CAPE(4963),
        RUNECRAFTING_CAPE(4947),
        HUNTER_CAPE(5158),
        CONSTRUCTION_CAPE(4953),
        SUMMONING_CAPE(8525),
        QUEST_CAPE(4945);

        private final int id;

        private Trigger(final int id){
            this.id = id;
        }

        private Trigger(final Animation anim){
            this(anim.getId());
        }

        public int getId(){
            return id;
        }
    }

    private int id;
    private String description;
    private Difficulty difficulty;
    private Trigger trigger;

    private final List<Requirement> requirements;
    private final List<Reward> rewards;

    public ClueScroll(final int id, final String description, final Difficulty difficulty, final Trigger trigger){
        this.id = id;
        this.description = description;
        this.difficulty = difficulty;
        this.trigger = trigger;

        requirements = new ArrayList<>();
        rewards = new ArrayList<>();
        for(Reward reward : getRewards(difficulty))
            rewards.add(reward);
    }

    public int getId(){
        return id;
    }

    public void setId(final int id){
        this.id = id;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(final String description){
        this.description = description;
    }

    public Difficulty getDifficulty(){
        return difficulty;
    }

    public void setDifficulty(final Difficulty difficulty){
        this.difficulty = difficulty;
    }

    public Trigger getTrigger(){
        return trigger;
    }

    public void setTrigger(final Trigger trigger){
        this.trigger = trigger;
    }

    public List<Requirement> getRequirements(){
        return requirements;
    }

    public List<Reward> getRewards(){
        return rewards;
    }

    public boolean hasAllRequirements(final Player player){
        for(final Requirement req : requirements)
            if(!req.apply(player))
                return false;
        return true;
    }

    public void send(final Player player){
        final String[] lines = Misc.wrapString(description.replaceAll("<br>", "\n"), 50).split("\n");
        player.getActionSender().openQuestInterface(String.format("@dre@%s Clue Scroll", Misc.ucFirst(difficulty.toString().toLowerCase())), lines);
        if(player.debug) {
            player.sendf("trigger: %s", trigger);
            for(final Requirement req : requirements)
                player.sendf(req.toString());
        }
    }

    public void apply(final Player player){
        Item oldItem = Item.create(id);
        if(player.getInventory().remove(oldItem) < 1)
            return;
        double currentSteps = 0;
        if(player.getPermExtraData().get("clueScrollProgress") != null)
            currentSteps = (double)player.getPermExtraData().get("clueScrollProgress") + 1;
        double maxSteps = getDifficulty().ordinal() + 2;
        double minSteps = getDifficulty().ordinal();
        boolean giveReward = currentSteps > maxSteps;
        double random = Math.random();
        double number = currentSteps / maxSteps;
        if(currentSteps >= minSteps) {
            if (!giveReward && (number) > random) {
                giveReward = true;
            }
        }
        if(giveReward) {
            giveReward = giveReward(player);
        }
        if(!giveReward) {
            Item item = oldItem;
            while(item.getId() == oldItem.getId())
                item = Item.create(ClueScrollManager.getAll(difficulty).get((int) Math.round(Math.random() * (ClueScrollManager.getAll(difficulty).size() - 1 != -1 ? ClueScrollManager.getAll(difficulty).size() - 1 : 0))).getId());
            player.sendMessage("You find another clue scroll!");
            player.getInventory().add(item);
            player.getPermExtraData().put("clueScrollProgress", currentSteps);
        }
    }

    public boolean giveReward(final Player player) {
        player.getPermExtraData().put("clueScrollProgress", 0.0);
        int amount = getDifficulty().ordinal() + 1;
        List<Reward> received = new ArrayList<>();
        while(amount > 0 || received.isEmpty()) {
            for (final Reward reward : rewards) {
                if(received.contains(reward))
                    continue;
                if (reward.apply(player)) {
                    if(amount <= getDifficulty().ordinal() + 1) {
                        amount--;
                        received.add(reward);
                    }
                }
                if(Misc.random(2) == 1) {
                    amount--;
                }
            }
        }
        if(!received.isEmpty())
            return true;
        return false;
    }

    public Element toElement(final Document doc){
        final Element element = doc.createElement("cluescroll");
        element.setAttribute("id", Integer.toString(id));
        element.setAttribute("difficulty", difficulty.name());
        element.setAttribute("trigger", trigger.name());
        final Element requirementsElement = doc.createElement("requirements");
        for(final Requirement requirement : requirements)
            requirementsElement.appendChild(requirement.toElement(doc));
        final Element rewardsElement = doc.createElement("rewards");
        for(final Reward reward : rewards)
            rewardsElement.appendChild(reward.toElement(doc));
        element.appendChild(ClueScrollUtils.createElement(doc, "description", description));
        element.appendChild(requirementsElement);
        element.appendChild(rewardsElement);
        return element;
    }

    public boolean equals(final Object o){
        if(o == null || !(o instanceof ClueScroll))
            return false;
        if(o == this)
            return true;
        final ClueScroll cs = (ClueScroll) o;
        return cs.id == id
                && cs.description.equals(description)
                && cs.difficulty == difficulty
                && cs.trigger == trigger;
    }

    public String toString(){
        return Integer.toString(id);
    }

    public static ClueScroll parse(final Element element){
        final int id = Integer.parseInt(element.getAttribute("id"));
        final Difficulty difficulty = Difficulty.valueOf(element.getAttribute("difficulty"));
        final Trigger trigger = Trigger.valueOf(element.getAttribute("trigger"));
        final String description = element.getElementsByTagName("description").item(0).getTextContent();
        final ClueScroll clueScroll = new ClueScroll(id, description, difficulty, trigger);
        final Element requirementsElement = (Element) element.getElementsByTagName("requirements").item(0);
        final NodeList requirements = requirementsElement.getElementsByTagName("requirement");
        for(int i = 0; i < requirements.getLength(); i++){
            final Node node = requirements.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            final Element e = (Element) node;
            final Requirement.Type type = Requirement.Type.valueOf(e.getAttribute("type"));
            clueScroll.requirements.add(type.parse(e));
        }
        final Element rewardsElement = (Element) element.getElementsByTagName("rewards").item(0);
        final NodeList rewards = rewardsElement.getElementsByTagName("reward");
        for(int i = 0; i < rewards.getLength(); i++){
            final Node node = rewards.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            final Element e = (Element) node;
            final Reward.Type type = Reward.Type.valueOf(e.getAttribute("type"));
            clueScroll.rewards.add(type.parse(e));
        }
        return clueScroll;
    }

}
