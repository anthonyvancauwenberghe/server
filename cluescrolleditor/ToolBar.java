package cluescrolleditor;

import cluescrolleditor.editor.ClueScrollEditor;
import cluescrolleditor.res.Res;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.hyperion.rs2.model.cluescroll.ClueScroll;
import org.hyperion.rs2.model.cluescroll.ClueScrollManager;
import org.hyperion.rs2.model.cluescroll.requirement.Requirement;
import org.hyperion.rs2.model.cluescroll.reward.Reward;

public class ToolBar extends JToolBar implements ActionListener{

    private class Button extends JButton {

        private Button(final String name, final ImageIcon icon){
            super(name, icon);
            setHorizontalTextPosition(JLabel.CENTER);
            setVerticalTextPosition(JLabel.BOTTOM);

            addActionListener(ToolBar.this);
        }
    }

    private final Button addButton;
    private final Button deleteButton;

    private final Button addRequirementButton;
    private final Button deleteRequirementButton;

    private final Button addRewardButton;
    private final Button deleteRewardButton;

    private final Button saveButton;

    public ToolBar(){
        setFloatable(false);

        final JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        addButton = new Button("Add", Res.SCROLL_ADD_32);
        deleteButton = new Button("Delete", Res.SCROLL_DELETE_32);

        final JPopupMenu addRequirementPopup = new JPopupMenu();
        for(final Requirement.Type type : Requirement.Type.values()){
            final JMenuItem item = new JMenuItem(type.name(), Res.LOCK_16);
            item.addActionListener(e -> {
                if(ClueScrollEditorFrame.getInstance().getEditor().getClueScroll() == null)
                    return;
                final ClueScroll cs = ClueScrollEditorFrame.getInstance().getEditor().getClueScroll();
                final Requirement req = type.createDefault();
                cs.getRequirements().add(req);
                ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().add(req, true);
                ClueScrollEditorFrame.getInstance().getList().repaintList();
            });
            addRequirementPopup.add(item);
        }

        addRequirementButton = new Button("Add Requirement", Res.LOCK_ADD_32);
        addRequirementButton.setComponentPopupMenu(addRequirementPopup);

        deleteRequirementButton = new Button("Delete Requirement", Res.LOCK_DELETE_32);

        final JPopupMenu addRewardPopup = new JPopupMenu();
        for(final Reward.Type type : Reward.Type.values()){
            final JMenuItem item = new JMenuItem(type.name(), Res.REWARD_16);
            item.addActionListener(e -> {
                if (ClueScrollEditorFrame.getInstance().getEditor().getClueScroll() == null)
                    return;
                final ClueScroll cs = ClueScrollEditorFrame.getInstance().getEditor().getClueScroll();
                final Reward reward = type.createDefault();
                cs.getRewards().add(reward);
                ClueScrollEditorFrame.getInstance().getEditor().getRewardList().add(reward, true);
                ClueScrollEditorFrame.getInstance().getList().repaintList();
            });
            addRewardPopup.add(item);
        }

        addRewardButton = new Button("Add Reward", Res.REWARD_ADD_32);
        addRewardButton.setComponentPopupMenu(addRewardPopup);

        deleteRewardButton = new Button("Delete Reward", Res.REWARD_DELETE_32);

        saveButton = new Button("Save", Res.SCROLL_SAVE_32);

        container.add(addButton);
        container.add(deleteButton);
        container.add(Box.createHorizontalStrut(5));
        container.add(addRequirementButton);
        container.add(deleteRequirementButton);
        container.add(Box.createHorizontalStrut(5));
        container.add(addRewardButton);
        container.add(deleteRewardButton);
        container.add(Box.createHorizontalGlue());
        container.add(saveButton);

        add(container, BorderLayout.CENTER);
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(addButton)){
            final ClueScroll cs = ClueScrollEditor.createDummyClueScroll();
            ClueScrollManager.add(cs);
            ClueScrollEditorFrame.getInstance().getList().add(cs, true);
        }else if(source.equals(deleteButton)){
            final ClueScroll cs = ClueScrollEditorFrame.getInstance().getList().getSelected();
            if(cs == null)
                return;
            ClueScrollManager.remove(cs);
            ClueScrollEditorFrame.getInstance().getList().remove(cs);
        }else if(source.equals(deleteRequirementButton)){
            if(ClueScrollEditorFrame.getInstance().getEditor().getClueScroll() == null)
                return;
            final ClueScroll cs = ClueScrollEditorFrame.getInstance().getEditor().getClueScroll();
            final Requirement selected = ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().getSelected();
            if(cs == null || selected == null)
                return;
            cs.getRequirements().remove(selected);
            ClueScrollEditorFrame.getInstance().getEditor().getRequirementList().remove(selected);
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(deleteRewardButton)){
            if(ClueScrollEditorFrame.getInstance().getEditor().getClueScroll() == null)
                return;
            final ClueScroll cs = ClueScrollEditorFrame.getInstance().getEditor().getClueScroll();
            final Reward selected = ClueScrollEditorFrame.getInstance().getEditor().getRewardList().getSelected();
            if(cs == null || selected == null)
                return;
            cs.getRewards().remove(selected);
            ClueScrollEditorFrame.getInstance().getEditor().getRewardList().remove(selected);
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(saveButton)){
            try{
                ClueScrollManager.save();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
