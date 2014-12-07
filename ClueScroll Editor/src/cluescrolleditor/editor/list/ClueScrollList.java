package cluescrolleditor.editor.list;

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
import org.hyperion.rs2.model.cluescroll.ClueScroll;

public class ClueScrollList extends JPanel implements ListSelectionListener {

    private final DefaultListModel<ClueScroll> model;
    private final JList<ClueScroll> list;

    public ClueScrollList(){
        super(new BorderLayout());

        model = new DefaultListModel<>();

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new ClueScrollListRenderer());
        list.addListSelectionListener(this);

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void valueChanged(final ListSelectionEvent e){
        final Object source = e.getSource();
        if(!source.equals(list))
            return;
        ClueScrollEditorFrame.getInstance().getEditor().setClueScroll(getSelected());
    }

    public void repaintList(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                list.repaint();
            }
        });
    }

    public ClueScroll getSelected(){
        return list.getSelectedValue();
    }

    public void add(final ClueScroll clueScroll, final boolean select){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.addElement(clueScroll);
                if (select) {
                    list.setSelectedValue(clueScroll, true);
                    valueChanged(new ListSelectionEvent(list, -1, -1, false));
                }
                list.repaint();
            }
        });
    }

    public void add(final ClueScroll clueScroll){
        add(clueScroll, false);
    }

    public void remove(final ClueScroll clueScroll){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.removeElement(clueScroll);
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
