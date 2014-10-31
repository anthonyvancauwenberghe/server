package cfgeditor.itemdef;

import cfgeditor.comp.LabelledCheckBox;
import cfgeditor.comp.LabelledComboBox;
import cfgeditor.comp.LabelledSpinner;
import cfgeditor.comp.LabelledText;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.TitledBorder;
import org.hyperion.rs2.model.ItemDefinition;

public class SingleItemDefEditor extends JPanel implements ActionListener {

    private ItemDefinition def;

    private final JButton saveButton;

    private final LabelledSpinner idComp;
    private final LabelledText nameComp;
    private final LabelledText examineComp;
    private final LabelledCheckBox notedComp;
    private final LabelledCheckBox noteableComp;
    private final LabelledCheckBox stackableComp;
    private final LabelledSpinner parentIdComp;
    private final LabelledSpinner notedIdComp;
    private final LabelledSpinner highAlchComp;
    private final LabelledComboBox<ArmourSlot> armourSlotComp;

    private final LabelledSpinner attackStabComp;
    private final LabelledSpinner attackSlashComp;
    private final LabelledSpinner attackCrushComp;
    private final LabelledSpinner attackMagicComp;
    private final LabelledSpinner attackRangeComp;
    private final LabelledSpinner defenceStabComp;
    private final LabelledSpinner defenceSlashComp;
    private final LabelledSpinner defenceCrushComp;
    private final LabelledSpinner defenceMagicComp;
    private final LabelledSpinner defenceRangeComp;
    private final LabelledSpinner strengthComp;
    private final LabelledSpinner prayerComp;

    public SingleItemDefEditor(){
        super(new BorderLayout());

        saveButton = new JButton("Save Changes");
        saveButton.addActionListener(this);

        idComp = new LabelledSpinner("ID");
        idComp.comp.setEnabled(false);
        nameComp = new LabelledText("Name");
        examineComp = new LabelledText("Examine");
        notedComp = new LabelledCheckBox("Noted");
        notedComp.comp.setEnabled(false);
        noteableComp = new LabelledCheckBox("Noteable");
        noteableComp.comp.setEnabled(false);
        stackableComp = new LabelledCheckBox("Stackable");
        parentIdComp = new LabelledSpinner("Parent ID");
        parentIdComp.comp.setEnabled(false);
        notedIdComp = new LabelledSpinner("Noted ID");
        notedIdComp.comp.setEnabled(false);
        highAlchComp = new LabelledSpinner("High Alch");
        armourSlotComp = new LabelledComboBox<>("Armour Slot", ArmourSlot.values());

        final JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.Y_AXIS));
        generalPanel.setBorder(new TitledBorder("General"));
        generalPanel.add(idComp);
        generalPanel.add(nameComp);
        generalPanel.add(examineComp);
        generalPanel.add(notedComp);
        generalPanel.add(noteableComp);
        generalPanel.add(stackableComp);
        generalPanel.add(parentIdComp);
        generalPanel.add(notedIdComp);
        generalPanel.add(highAlchComp);
        generalPanel.add(armourSlotComp);
        
        attackStabComp = new LabelledSpinner("Stab");
        attackSlashComp = new LabelledSpinner("Slash");
        attackCrushComp = new LabelledSpinner("Crush");
        attackMagicComp = new LabelledSpinner("Magic");
        attackRangeComp = new LabelledSpinner("Range");
        
        final JPanel attackPanel = new JPanel();
        attackPanel.setLayout(new BoxLayout(attackPanel, BoxLayout.Y_AXIS));
        attackPanel.setBorder(new TitledBorder("Attack"));
        attackPanel.add(attackStabComp);
        attackPanel.add(attackSlashComp);
        attackPanel.add(attackCrushComp);
        attackPanel.add(attackMagicComp);
        attackPanel.add(attackRangeComp);

        defenceStabComp = new LabelledSpinner("Stab");
        defenceSlashComp = new LabelledSpinner("Slash");
        defenceCrushComp = new LabelledSpinner("Crush");
        defenceMagicComp = new LabelledSpinner("Magic");
        defenceRangeComp = new LabelledSpinner("Range");

        final JPanel defencePanel = new JPanel();
        defencePanel.setLayout(new BoxLayout(defencePanel, BoxLayout.Y_AXIS));
        defencePanel.setBorder(new TitledBorder("Defence"));
        defencePanel.add(defenceStabComp);
        defencePanel.add(defenceSlashComp);
        defencePanel.add(defenceCrushComp);
        defencePanel.add(defenceMagicComp);
        defencePanel.add(defenceRangeComp);

        strengthComp = new LabelledSpinner("Strength");
        prayerComp = new LabelledSpinner("Prayer");

        final JPanel otherPanel = new JPanel();
        otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.Y_AXIS));
        otherPanel.setBorder(new TitledBorder("Other"));
        otherPanel.add(strengthComp);
        otherPanel.add(prayerComp);

        final JPanel bonusesPanel = new JPanel();
        bonusesPanel.setLayout(new BoxLayout(bonusesPanel, BoxLayout.Y_AXIS));
        bonusesPanel.setBorder(new TitledBorder("Bonuses"));
        bonusesPanel.add(new JScrollPane(attackPanel));
        bonusesPanel.add(new JScrollPane(defencePanel));
        bonusesPanel.add(new JScrollPane(otherPanel));

        add(saveButton, BorderLayout.NORTH);
        add(new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(generalPanel), new JScrollPane(bonusesPanel)), BorderLayout.CENTER);
    }

    public void setFunctional(final boolean functional){
        saveButton.setEnabled(functional);
        saveButton.repaint();
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(!source.equals(saveButton) || def == null)
            return;
        def.setName(nameComp.value());
        def.setDescription(examineComp.value());
        def.setStackable(stackableComp.value());
        def.setHighAlcValue(highAlchComp.value());
        def.setArmourSlot(armourSlotComp.value().value);
        def.setBonus(0, attackStabComp.value());
        def.setBonus(1, attackSlashComp.value());
        def.setBonus(2, attackCrushComp.value());
        def.setBonus(3, attackMagicComp.value());
        def.setBonus(4, attackRangeComp.value());
        def.setBonus(5, defenceStabComp.value());
        def.setBonus(6, defenceSlashComp.value());
        def.setBonus(7, defenceCrushComp.value());
        def.setBonus(8, defenceMagicComp.value());
        def.setBonus(9, defenceRangeComp.value());
        def.setBonus(10, strengthComp.value());
        def.setBonus(11, prayerComp.value());
    }

    public void set(final ItemDefinition def){
        this.def = def;
        idComp.set(def.getId());
        nameComp.set(def.getName());
        examineComp.set(def.getDescription());
        notedComp.set(def.isNoted());
        noteableComp.set(def.isNoteable());
        stackableComp.set(def.isStackable());
        parentIdComp.set(def.getParentId());
        notedIdComp.set(def.getNotedId());
        highAlchComp.set(def.getHighAlcValue());
        armourSlotComp.set(ArmourSlot.byValue(def.getArmourSlot()));
        final int[] bonuses = def.getBonus();
        attackStabComp.set(bonuses[0]);
        attackSlashComp.set(bonuses[1]);
        attackCrushComp.set(bonuses[2]);
        attackMagicComp.set(bonuses[3]);
        attackRangeComp.set(bonuses[4]);
        defenceStabComp.set(bonuses[5]);
        defenceSlashComp.set(bonuses[6]);
        defenceCrushComp.set(bonuses[7]);
        defenceMagicComp.set(bonuses[8]);
        defenceRangeComp.set(bonuses[9]);
        strengthComp.set(bonuses[10]);
        prayerComp.set(bonuses[11]);
    }
}
