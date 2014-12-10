package cluescrolleditor.editor.reward;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.reward.PointsReward;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

public class PointsRewardPanel extends RewardPanel<PointsReward> implements ItemListener{

    private final JComboBox<PointsReward.Type> typeBox;

    public PointsRewardPanel(final PointsReward reward){
        super(reward);

        typeBox = new JComboBox<>(PointsReward.Type.values());
        typeBox.setSelectedItem(reward.getPointsType());
        typeBox.setBorder(new TitledBorder("Type"));
        typeBox.addItemListener(this);

        add(typeBox, BorderLayout.NORTH);
        add(commonPanel, BorderLayout.CENTER);
    }

    public void itemStateChanged(final ItemEvent e){
        final Object source = e.getSource();
        if(!source.equals(typeBox))
            return;
        reward.setPointsType((PointsReward.Type)typeBox.getSelectedItem());
        ClueScrollEditorFrame.getInstance().getEditor().getRewardList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }
}
