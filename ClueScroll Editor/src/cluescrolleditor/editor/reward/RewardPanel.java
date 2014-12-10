package cluescrolleditor.editor.reward;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.reward.Reward;
import cluescrolleditor.util.EditorUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RewardPanel<T extends Reward> extends JPanel implements ChangeListener{

    protected final T reward;

    private final JSpinner minAmountSpinner;
    private final JSpinner maxAmountSpinner;
    private final JSpinner chanceSpinner;

    protected final JPanel commonPanel;

    protected RewardPanel(final T reward){
        super(new BorderLayout());
        this.reward = reward;

        minAmountSpinner = EditorUtils.createSpinner(reward.getMinAmount(), 0, Integer.MAX_VALUE);
        minAmountSpinner.setBorder(new TitledBorder("Min Amount"));
        minAmountSpinner.addChangeListener(this);

        maxAmountSpinner = EditorUtils.createSpinner(reward.getMaxAmount(), 0, Integer.MAX_VALUE);
        maxAmountSpinner.setBorder(new TitledBorder("Max Amount"));
        maxAmountSpinner.addChangeListener(this);

        chanceSpinner = EditorUtils.createSpinner(reward.getChance(), 0, 100);
        chanceSpinner.setBorder(new TitledBorder("Chance (%)"));
        chanceSpinner.addChangeListener(this);

        commonPanel = new JPanel(new GridLayout(3, 1));
        commonPanel.add(minAmountSpinner);
        commonPanel.add(maxAmountSpinner);
        commonPanel.add(chanceSpinner);
    }

    public T getReward(){
        return reward;
    }

    public void stateChanged(final ChangeEvent e){
        final Object source = e.getSource();
        if(source.equals(minAmountSpinner)){
            reward.setMinAmount((Integer)minAmountSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRewardList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(maxAmountSpinner)){
            reward.setMaxAmount((Integer) maxAmountSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRewardList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(chanceSpinner)){
            reward.setChance((Integer) chanceSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getEditor().getRewardList().repaintList();
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }
    }
}
