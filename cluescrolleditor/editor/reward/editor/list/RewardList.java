package cluescrolleditor.editor.reward.editor.list;

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
import org.hyperion.rs2.model.cluescroll.reward.Reward;

public class RewardList extends JPanel implements ListSelectionListener{

    private final DefaultListModel<Reward> model;
    private final JList<Reward> list;

    public RewardList(){
        super(new BorderLayout());

        model = new DefaultListModel<>();

        list = new JList<>(model);
        list.setCellRenderer(new RewardListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void repaintList(){
        SwingUtilities.invokeLater(list::repaint);
    }

    public void valueChanged(final ListSelectionEvent e){
        final Object source = e.getSource();
        if(!source.equals(list))
            return;
        ClueScrollEditorFrame.getInstance().getEditor().getRewardEditor().setReward(getSelected());
    }

    public Reward getSelected(){
        return list.getSelectedValue();
    }

    public void clear(){
        SwingUtilities.invokeLater(() -> {
            model.removeAllElements();
            list.clearSelection();
            list.repaint();
        });
    }

    public void add(final Reward reward, final boolean select){
        SwingUtilities.invokeLater(() -> {
            model.addElement(reward);
            if(select){
                list.setSelectedValue(reward, true);
                valueChanged(new ListSelectionEvent(list, -1, -1, false));
            }
            list.repaint();
        });
    }

    public void add(final Reward reward){
        add(reward, false);
    }

    public void remove(final Reward reward){
        SwingUtilities.invokeLater(() -> {
            model.removeElement(reward);
            if (model.isEmpty())
                list.clearSelection();
            else
                list.setSelectedIndex(0);
            valueChanged(new ListSelectionEvent(list, -1, -1, false));
            list.repaint();
        });
    }
}
