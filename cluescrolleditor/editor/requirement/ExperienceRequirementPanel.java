package cluescrolleditor.editor.requirement;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.util.EditorUtils;
import cluescrolleditor.util.Skill;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.cluescroll.requirement.ExperienceRequirement;

public class ExperienceRequirementPanel extends RequirementPanel<ExperienceRequirement> implements ItemListener, ChangeListener {

    private final JComboBox<Skill> skillBox;
    private final JSpinner xpSpinner;

    public ExperienceRequirementPanel(final ExperienceRequirement requirement){
        super(requirement);

        skillBox = new JComboBox<>(Skill.values());
        skillBox.setSelectedItem(Skill.values()[requirement.getSkill()]);
        skillBox.setBorder(new TitledBorder("Skill"));
        skillBox.addItemListener(this);

        xpSpinner = EditorUtils.createSpinner(requirement.getXp(), 1, Skills.MAXIMUM_EXP);
        xpSpinner.setBorder(new TitledBorder("XP"));
        xpSpinner.addChangeListener(this);

        add(skillBox, BorderLayout.WEST);
        add(xpSpinner, BorderLayout.CENTER);
    }

    public void itemStateChanged(final ItemEvent e){
        final Object source = e.getSource();
        if(!source.equals(skillBox))
            return;
        requirement.setSkill(((Skill)skillBox.getSelectedItem()).ordinal());
        ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }

    public void stateChanged(final ChangeEvent e){
        final Object source = e.getSource();
        if(!source.equals(xpSpinner))
            return;
        requirement.setXp((Integer)xpSpinner.getValue());
        ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }
}
