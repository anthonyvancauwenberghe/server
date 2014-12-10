package cluescrolleditor.cluescroll;

import cluescrolleditor.cluescroll.requirement.Requirement;
import cluescrolleditor.cluescroll.reward.Reward;
import cluescrolleditor.cluescroll.util.ClueScrollUtils;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ClueScroll {

    public enum Difficulty{
        EASY, MEDIUM, HARD, ELITE
    }

    public enum Trigger{
        DIG, WAVE, BOW
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
