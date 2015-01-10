package cluescrolleditor.editor.requirement;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.requirement.ItemRequirement;
import cluescrolleditor.util.EditorUtils;
import java.awt.BorderLayout;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ItemRequirementPanel extends RequirementPanel<ItemRequirement> implements ChangeListener {

    private final JSpinner idSpinner;
    private final JSpinner amountSpinner;

    public ItemRequirementPanel(final ItemRequirement requirement){
        super(requirement);

        idSpinner = EditorUtils.createSpinner(requirement.getId(), 1, Integer.MAX_VALUE);
        idSpinner.setBorder(new TitledBorder("ID"));
        idSpinner.addChangeListener(this);

        amountSpinner = EditorUtils.createSpinner(requirement.getAmount(), 1, Integer.MAX_VALUE);
        amountSpinner.setBorder(new TitledBorder("Amount"));
        amountSpinner.addChangeListener(this);

        add(idSpinner, BorderLayout.WEST);
        add(amountSpinner, BorderLayout.CENTER);
    }

    public void stateChanged(final ChangeEvent e){
        final Object source = e.getSource();
        if(source.equals(idSpinner)){
            requirement.setId((Integer)idSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(amountSpinner)){
            requirement.setAmount((Integer)amountSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }
    }
}
