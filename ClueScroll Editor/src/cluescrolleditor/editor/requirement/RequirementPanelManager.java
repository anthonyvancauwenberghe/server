package cluescrolleditor.editor.requirement;

import cluescrolleditor.cluescroll.requirement.CombatLevelRequirement;
import cluescrolleditor.cluescroll.requirement.EquipmentRequirement;
import cluescrolleditor.cluescroll.requirement.ExperienceRequirement;
import cluescrolleditor.cluescroll.requirement.ItemRequirement;
import cluescrolleditor.cluescroll.requirement.LocationRequirement;
import cluescrolleditor.cluescroll.requirement.Requirement;
import java.awt.Dimension;

public final class RequirementPanelManager {

    private RequirementPanelManager(){}

    public static RequirementPanel create(final Requirement.Type type){
        return create(type.createDefault());
    }

    public static RequirementPanel create(final Requirement requirement){
        final RequirementPanel panel = get(requirement);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }

    private static RequirementPanel get(final Requirement requirement){
        switch(requirement.getType()){
            case LOCATION:
                return new LocationRequirementPanel((LocationRequirement)requirement);
            case EQUIPMENT:
                return new EquipmentRequirementPanel((EquipmentRequirement)requirement);
            case EXPERIENCE:
                return new ExperienceRequirementPanel((ExperienceRequirement)requirement);
            case ITEM:
                return new ItemRequirementPanel((ItemRequirement)requirement);
            case COMBAT_LEVEL:
                return new CombatLevelRequirementPanel((CombatLevelRequirement)requirement);
            default:
                return null;
        }
    }
}