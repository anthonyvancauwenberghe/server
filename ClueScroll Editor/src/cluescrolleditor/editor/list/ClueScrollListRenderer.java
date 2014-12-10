package cluescrolleditor.editor.list;

import cluescrolleditor.cluescroll.ClueScroll;
import cluescrolleditor.cluescroll.requirement.Requirement;
import cluescrolleditor.cluescroll.reward.Reward;
import cluescrolleditor.res.Res;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class ClueScrollListRenderer extends DefaultListCellRenderer{

    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
        final Component c = super.getListCellRendererComponent(list, o, i, s, f);
        if(o == null)
            return c;
        final ClueScroll cs = (ClueScroll) o;
        final JLabel label = (JLabel) c;
        label.setText(String.format("%d (%s)", cs.getId(), cs.getDifficulty()));
        label.setIcon(Res.SCROLL_16);
        final StringBuilder bldr = new StringBuilder();
        bldr.append("<html>");
        bldr.append(String.format("ID: %d", cs.getId())).append("<br>");
        bldr.append(String.format("Difficulty: %s", cs.getDifficulty())).append("<br>");
        bldr.append(String.format("Trigger: %s", cs.getTrigger())).append("<br>");
        bldr.append(String.format("Requirements: %d", cs.getRequirements().size())).append("<br>");
        if(!cs.getRequirements().isEmpty()){
            bldr.append("<ol>");
            for(final Requirement req : cs.getRequirements())
                bldr.append("<li>").append(req).append("</li>");
            bldr.append("</ol>");
        }
        bldr.append(String.format("Rewards: %d", cs.getRewards().size())).append("<br>");
        if(!cs.getRewards().isEmpty()){
            bldr.append("<ol>");
            for(final Reward rew : cs.getRewards())
                bldr.append("<li>").append(rew).append("</li>");
            bldr.append("</ol>");
        }
        bldr.append(String.format("Description: %s", cs.getDescription()));
        bldr.append("</html>");
        label.setToolTipText(bldr.toString());
        return label;
    }
}
