package cluescrolleditor.editor.reward.editor;

import cluescrolleditor.cluescroll.reward.Reward;
import cluescrolleditor.editor.reward.RewardPanelManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class RewardEditor extends JPanel {

    private Reward reward;

    public RewardEditor(){
        super(new BorderLayout());
    }

    public void setReward(final Reward reward){
        this.reward = reward;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeAll();
                if (reward != null)
                    add(RewardPanelManager.create(reward), BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });
    }

}
