package cluescrolleditor.cluescroll.requirement;

import cluescrolleditor.cluescroll.util.ClueScrollUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CombatLevelRequirement extends Requirement {

    private int combatLevel;

    public CombatLevelRequirement(final int combatLevel){
        super(Type.COMBAT_LEVEL);
        this.combatLevel = combatLevel;
    }

    public int getCombatLevel(){
        return combatLevel;
    }

    public void setCombatLevel(final int combatLevel){
        this.combatLevel = combatLevel;
    }

    protected void append(final Document doc, final Element root){
        root.appendChild(ClueScrollUtils.createElement(doc, "combatLevel", combatLevel));
    }

    public String toString(){
        return String.format("%s: %d", super.toString(), combatLevel);
    }

    public static CombatLevelRequirement parse(final Element element){
        final int level = ClueScrollUtils.getInteger(element, "combatLevel");
        return new CombatLevelRequirement(level);
    }
}
