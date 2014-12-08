package cluescrolleditor.editor.requirement.editor.list;

import cluescrolleditor.res.Res;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.hyperion.rs2.model.cluescroll.requirement.Requirement;

public class RequirementListRenderer extends DefaultListCellRenderer {

    public Component getListCellRendererComponent(final JList list, final Object o, final int i, final boolean s, final boolean f){
        final Component c = super.getListCellRendererComponent(list, o, i, s, f);
        if(o == null)
            return c;
        final Requirement r = (Requirement) o;
        final JLabel label = (JLabel) c;
        label.setIcon(Res.LOCK_16);
        label.setToolTipText(r.toString());
        return label;
    }
}
