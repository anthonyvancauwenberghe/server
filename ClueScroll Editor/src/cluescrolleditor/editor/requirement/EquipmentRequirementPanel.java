package cluescrolleditor.editor.requirement;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.requirement.EquipmentRequirement;
import cluescrolleditor.util.EditorUtils;
import cluescrolleditor.util.Slot;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EquipmentRequirementPanel extends RequirementPanel<EquipmentRequirement> implements ItemListener, ChangeListener{

    private final JComboBox<Slot> slotBox;
    private final JSpinner itemIdSpinner;

    public EquipmentRequirementPanel(final EquipmentRequirement requirement){
        super(requirement);

        slotBox = new JComboBox<>(Slot.values());
        slotBox.setSelectedItem(Slot.getSlot(requirement.getSlot()));
        slotBox.setBorder(new TitledBorder("Slot"));
        slotBox.addItemListener(this);

        itemIdSpinner = EditorUtils.createSpinner(requirement.getItemId(), 1, Integer.MAX_VALUE);
        itemIdSpinner.setBorder(new TitledBorder("Item"));
        itemIdSpinner.addChangeListener(this);

        add(slotBox, BorderLayout.CENTER);
        add(itemIdSpinner, BorderLayout.EAST);
    }

    public void itemStateChanged(final ItemEvent e){
        final Object source = e.getSource();
        if(!source.equals(slotBox))
            return;
        requirement.setSlot(((Slot)slotBox.getSelectedItem()).id);
        ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }

    public void stateChanged(final ChangeEvent e){
        final Object source = e.getSource();
        if(!source.equals(itemIdSpinner))
            return;
        final int id = (Integer) itemIdSpinner.getValue();
        requirement.setItemId(id);
        ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }
}
