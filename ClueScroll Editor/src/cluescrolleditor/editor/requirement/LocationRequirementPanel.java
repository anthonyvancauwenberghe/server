package cluescrolleditor.editor.requirement;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.util.EditorUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.hyperion.rs2.model.cluescroll.requirement.LocationRequirement;

public class LocationRequirementPanel extends RequirementPanel<LocationRequirement> implements ChangeListener{

    private final JSpinner xSpinner;
    private final JSpinner ySpinner;
    private final JSpinner zSpinner;

    public LocationRequirementPanel(final LocationRequirement requirement){
        super(requirement);

        xSpinner = EditorUtils.createSpinner(requirement.getX(), 1, 10000);
        xSpinner.setBorder(new TitledBorder("X"));
        xSpinner.addChangeListener(this);

        ySpinner = EditorUtils.createSpinner(requirement.getY(), 1, 10000);
        ySpinner.setBorder(new TitledBorder("Y"));
        ySpinner.addChangeListener(this);

        zSpinner = EditorUtils.createSpinner(requirement.getZ(), -1, 4);
        zSpinner.setBorder(new TitledBorder("Z"));
        zSpinner.addChangeListener(this);

        final JPanel container = new JPanel(new GridLayout(1, 3));
        container.add(xSpinner);
        container.add(ySpinner);
        container.add(zSpinner);

        add(container, BorderLayout.CENTER);
    }

    public void stateChanged(final ChangeEvent e){
        final Object source = e.getSource();
        if(source.equals(xSpinner)){
            requirement.setX((Integer)xSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(ySpinner)){
            requirement.setY((Integer)ySpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(zSpinner)){
            requirement.setZ((Integer)zSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }
    }
}
