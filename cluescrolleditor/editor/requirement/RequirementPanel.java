package cluescrolleditor.editor.requirement;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.hyperion.rs2.model.cluescroll.requirement.Requirement;

public class RequirementPanel<T extends Requirement> extends JPanel{

    protected final T requirement;

    protected RequirementPanel(final T requirement){
        super(new BorderLayout());
        this.requirement = requirement;
    }

    public T getRequirement(){
        return requirement;
    }
}
