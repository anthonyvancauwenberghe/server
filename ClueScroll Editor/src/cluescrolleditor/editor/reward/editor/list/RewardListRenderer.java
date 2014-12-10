package cluescrolleditor.editor.reward.editor.list;

import cluescrolleditor.cluescroll.reward.Reward;
import cluescrolleditor.res.Res;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class RewardListRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
        final Component c = super.getListCellRendererComponent(list, o, i, s, f);
        if(o == null)
            return c;
        final Reward r = (Reward) o;
        final JLabel label = (JLabel) c;
        label.setIcon(Res.REWARD_16);
        label.setToolTipText(r.toString());
        return label;
    }
}
