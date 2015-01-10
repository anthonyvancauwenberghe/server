package cluescrolleditor.editor.reward;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.reward.ItemReward;
import cluescrolleditor.util.EditorUtils;
import java.awt.BorderLayout;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ItemRewardPanel extends RewardPanel<ItemReward> implements ChangeListener {

    private final JSpinner idSpinner;

    public ItemRewardPanel(final ItemReward reward){
        super(reward);

        idSpinner = EditorUtils.createSpinner(reward.getId(), 1, Integer.MAX_VALUE);
        idSpinner.setBorder(new TitledBorder("ID"));
        idSpinner.addChangeListener(this);

        add(idSpinner, BorderLayout.NORTH);
        add(commonPanel, BorderLayout.CENTER);
    }

    public void stateChanged(final ChangeEvent e){
        super.stateChanged(e);
        final Object source = e.getSource();
        if(!source.equals(idSpinner))
            return;
        reward.setId((Integer)idSpinner.getValue());
        ClueScrollEditorFrame.getInstance().getEditor().getRewardList().repaintList();
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }
}
