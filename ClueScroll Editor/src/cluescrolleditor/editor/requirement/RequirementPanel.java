package cluescrolleditor.editor.requirement;

import cluescrolleditor.cluescroll.requirement.Requirement;
import java.awt.BorderLayout;
import javax.swing.JPanel;

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
