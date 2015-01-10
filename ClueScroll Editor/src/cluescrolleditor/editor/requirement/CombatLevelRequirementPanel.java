package cluescrolleditor.editor.requirement;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.requirement.CombatLevelRequirement;
import cluescrolleditor.util.EditorUtils;
import java.awt.BorderLayout;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CombatLevelRequirementPanel extends RequirementPanel<CombatLevelRequirement> implements ChangeListener {

    private final JSpinner combatLevelSpinner;

    public CombatLevelRequirementPanel(final CombatLevelRequirement requirement){
        super(requirement);

        combatLevelSpinner = EditorUtils.createSpinner(requirement.getCombatLevel(), 1, 126);
        combatLevelSpinner.setBorder(new TitledBorder("Combat Level"));
        combatLevelSpinner.addChangeListener(this);

        add(combatLevelSpinner, BorderLayout.CENTER);
    }

    public void stateChanged(final ChangeEvent e){
        final Object source = e.getSource();
        if(!source.equals(combatLevelSpinner))
            return;
        requirement.setCombatLevel((Integer)combatLevelSpinner.getValue());
        ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }
}
