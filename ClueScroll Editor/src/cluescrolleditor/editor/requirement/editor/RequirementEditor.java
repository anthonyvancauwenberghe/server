package cluescrolleditor.editor.requirement.editor;

import cluescrolleditor.cluescroll.requirement.Requirement;
import cluescrolleditor.editor.requirement.RequirementPanelManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RequirementEditor extends JPanel {

    private Requirement requirement;

    public RequirementEditor(){
        super(new BorderLayout());
    }

    public void setRequirement(final Requirement requirement){
        this.requirement = requirement;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                removeAll();
                if(requirement != null)
                    add(RequirementPanelManager.create(requirement), BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });
    }

}
