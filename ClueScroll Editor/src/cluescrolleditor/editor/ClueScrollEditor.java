package cluescrolleditor.editor;

import cluescrolleditor.ClueScrollEditorFrame;
import cluescrolleditor.cluescroll.ClueScroll;
import cluescrolleditor.cluescroll.ClueScrollManager;
import cluescrolleditor.cluescroll.requirement.Requirement;
import cluescrolleditor.cluescroll.reward.Reward;
import cluescrolleditor.editor.requirement.editor.RequirementEditor;
import cluescrolleditor.editor.requirement.editor.list.RequirementList;
import cluescrolleditor.editor.reward.editor.RewardEditor;
import cluescrolleditor.editor.reward.editor.list.RewardList;
import cluescrolleditor.util.EditorUtils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class ClueScrollEditor extends JPanel implements ChangeListener, ItemListener, DocumentListener{

    public static final int ID = 1;
    public static final String DESCRIPTION = "Enter description here";
    public static final ClueScroll.Difficulty DIFFICULTY = ClueScroll.Difficulty.EASY;
    public static final ClueScroll.Trigger TRIGGER = ClueScroll.Trigger.DIG;

    private static final ClueScroll DUMMY = createDummyClueScroll();

    public static int id = 2677;

    private ClueScroll cs;

    private final JSpinner idSpinner;
    private final JComboBox<ClueScroll.Difficulty> difficultyBox;
    private final JComboBox<ClueScroll.Trigger> triggerBox;
    private final JTextArea descriptionArea;

    private final RequirementList requirementList;
    private final RequirementEditor requirementEditor;

    private final RewardList rewardList;
    private final RewardEditor rewardEditor;

    public ClueScrollEditor(){
        super(new BorderLayout());

        idSpinner = EditorUtils.createSpinner(1, 1, Integer.MAX_VALUE);
        idSpinner.setEnabled(false);
        idSpinner.setBorder(new TitledBorder("ID"));
        idSpinner.addChangeListener(this);

        difficultyBox = new JComboBox<>(ClueScroll.Difficulty.values());
        difficultyBox.setEnabled(false);
        difficultyBox.setBorder(new TitledBorder("Difficulty"));
        difficultyBox.addItemListener(this);

        triggerBox = new JComboBox<>(ClueScroll.Trigger.values());
        triggerBox.setEnabled(false);
        triggerBox.setBorder(new TitledBorder("Trigger"));
        triggerBox.addItemListener(this);

        final JPanel standardPanel = new JPanel(new GridLayout(1, 3));
        standardPanel.add(idSpinner);
        standardPanel.add(difficultyBox);
        standardPanel.add(triggerBox);

        descriptionArea = new JTextArea();
        descriptionArea.setEnabled(false);
        descriptionArea.setRows(5);
        descriptionArea.setBorder(new TitledBorder("Description"));
        descriptionArea.getDocument().addDocumentListener(this);

        final JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(standardPanel, BorderLayout.NORTH);
        northPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        rewardList = new RewardList();

        rewardEditor = new RewardEditor();

        final JSplitPane rewardSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, rewardList, rewardEditor);
        rewardSplit.setBorder(new TitledBorder("Rewards"));

        requirementList = new RequirementList();

        requirementEditor = new RequirementEditor();

        final JSplitPane requirementSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, requirementList, requirementEditor);
        requirementSplit.setBorder(new TitledBorder("Requirements"));

        add(northPanel, BorderLayout.NORTH);
        add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, requirementSplit, rewardSplit), BorderLayout.CENTER);
    }

    public void stateChanged(final ChangeEvent e){
        if(cs == null)
            return;
        final Object source = e.getSource();
        if(source.equals(idSpinner)){
            cs.setId((Integer)idSpinner.getValue());
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }
    }

    public void itemStateChanged(final ItemEvent e){
        if(cs == null)
            return;
        final Object source = e.getSource();
        if(source.equals(difficultyBox)){
            cs.setDifficulty((ClueScroll.Difficulty)difficultyBox.getSelectedItem());
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }else if(source.equals(triggerBox)){
            cs.setTrigger((ClueScroll.Trigger)triggerBox.getSelectedItem());
            ClueScrollEditorFrame.getInstance().getList().repaintList();
        }
    }

    public void insertUpdate(final DocumentEvent e){
        final Document doc = e.getDocument();
        if(cs == null || !doc.equals(descriptionArea.getDocument()))
            return;
        cs.setDescription(descriptionArea.getText());
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }

    public void removeUpdate(final DocumentEvent e){
        final Document doc = e.getDocument();
        if(cs == null || !doc.equals(descriptionArea.getDocument()))
            return;
        cs.setDescription(descriptionArea.getText());
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }

    public void changedUpdate(final DocumentEvent e){
        final Document doc = e.getDocument();
        if(cs == null || !doc.equals(descriptionArea.getDocument()))
            return;
        cs.setDescription(descriptionArea.getText());
        ClueScrollEditorFrame.getInstance().getList().repaintList();
    }

    public void setClueScroll(final ClueScroll cs){
        this.cs = cs;
        final ClueScroll value = cs == null ? DUMMY : cs;
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                idSpinner.setEnabled(cs != null);
                idSpinner.setValue(value.getId());
                idSpinner.repaint();
                difficultyBox.setEnabled(cs != null);
                difficultyBox.setSelectedItem(value.getDifficulty());
                difficultyBox.repaint();
                triggerBox.setEnabled(cs != null);
                triggerBox.setSelectedItem(value.getTrigger());
                triggerBox.repaint();
                descriptionArea.setEnabled(cs != null);
                descriptionArea.setText(value.getDescription());
                descriptionArea.repaint();
                requirementList.clear();
                for(final Requirement req : value.getRequirements())
                    requirementList.add(req);
                rewardList.clear();
                for(final Reward reward : value.getRewards())
                    rewardList.add(reward);
            }
        });
    }

    public ClueScroll getClueScroll(){
        return cs;
    }

    public RewardList getRewardList(){
        return rewardList;
    }

    public RewardEditor getRewardEditor(){
        return rewardEditor;
    }

    public RequirementList getRequirementList(){
        return requirementList;
    }

    public RequirementEditor getRequirementEditor(){
        return requirementEditor;
    }

    public static ClueScroll createDummyClueScroll(){
        while(ClueScrollManager.get(id) != null)
            ++id;
        return new ClueScroll(id++, DESCRIPTION, DIFFICULTY, TRIGGER);
    }
}
