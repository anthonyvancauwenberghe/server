package cluescrolleditor.editor.requirement.editor.list;

import cluescrolleditor.ClueScrollEditorFrame;
import java.awt.BorderLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hyperion.rs2.model.cluescroll.requirement.Requirement;

public class RequirementList extends JPanel implements ListSelectionListener{

    private final DefaultListModel<Requirement> model;
    private final JList<Requirement> list;

    public RequirementList(){
        super(new BorderLayout());

        model = new DefaultListModel<>();

        list = new JList<>(model);
        list.setCellRenderer(new RequirementListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void repaintList(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                list.repaint();
            }
        });
    }

    public void valueChanged(final ListSelectionEvent e){
        final Object source = e.getSource();
        if(!source.equals(list))
            return;
        ClueScrollEditorFrame.getInstance().getEditor().getRequirementEditor().setRequirement(getSelected());
    }

    public Requirement getSelected(){
        return list.getSelectedValue();
    }

    public void clear(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.removeAllElements();
                list.clearSelection();
                list.repaint();
            }
        });
    }

    public void add(final Requirement requirement, final boolean select){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.addElement(requirement);
                if (select) {
                    list.setSelectedValue(requirement, true);
                    valueChanged(new ListSelectionEvent(list, -1, -1, false));
                }
                list.repaint();
            }
        });
    }

    public void add(final Requirement requirement){
        add(requirement, false);
    }

    public void remove(final Requirement requirement){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.removeElement(requirement);
                if (model.isEmpty())
                    list.clearSelection();
                else
                    list.setSelectedIndex(0);
                valueChanged(new ListSelectionEvent(list, -1, -1, false));
                list.repaint();
            }
        });
    }
}
