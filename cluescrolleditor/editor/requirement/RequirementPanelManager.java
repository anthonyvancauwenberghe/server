package cluescrolleditor.editor.requirement;

import java.awt.Dimension;
import org.hyperion.rs2.model.cluescroll.requirement.CombatLevelRequirement;
import org.hyperion.rs2.model.cluescroll.requirement.EquipmentRequirement;
import org.hyperion.rs2.model.cluescroll.requirement.ExperienceRequirement;
import org.hyperion.rs2.model.cluescroll.requirement.ItemRequirement;
import org.hyperion.rs2.model.cluescroll.requirement.LocationRequirement;
import org.hyperion.rs2.model.cluescroll.requirement.Requirement;

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
