package cluescrolleditor.editor.reward;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.reward.ExperienceReward;
import cluescrolleditor.util.Skill;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

public class ExperienceRewardPanel extends RewardPanel<ExperienceReward> implements ItemListener {

    private final JComboBox<Skill> skillBox;

    public ExperienceRewardPanel(final ExperienceReward reward){
        super(reward);

        skillBox = new JComboBox<>(Skill.values());
        skillBox.setSelectedItem(Skill.values()[reward.getSkill()]);
        skillBox.setBorder(new TitledBorder("Skill"));
        skillBox.addItemListener(this);

        add(skillBox, BorderLayout.NORTH);
        add(commonPanel, BorderLayout.CENTER);
    }

    public void itemStateChanged(final ItemEvent e){
        final Object source = e.getSource();
        if(!source.equals(skillBox))
            return;
        reward.setSkill(((Skill)skillBox.getSelectedItem()).ordinal());
        ClueScrollEditorFrame.getInstance().getEditor().getRewardList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }
}
