package cfgeditor;

import cfgeditor.itemdef.ItemDefinitionManager;
import cfgeditor.itemdef.ItemDefsEditor;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import org.hyperion.rs2.model.ItemDefinition;

public class ConfigEditor extends JFrame implements ActionListener, ItemDefinitionManager.Listener{

    /*
    only wanna spawn npc's,
    change items bonusses
     */

    private static ConfigEditor instance;

    private final JButton saveItemsButton;

    private final ItemDefsEditor itemDefsEditor;

    private final JProgressBar itemDefsProgressBar;
    private final JProgressBar npcSpawnsProgressBar;

    public ConfigEditor(){
        super("Config Editor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        itemDefsProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, ItemDefinitionManager.MAX);
        itemDefsProgressBar.setString("Item Definitions: ----");
        itemDefsProgressBar.setStringPainted(true);

        npcSpawnsProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        npcSpawnsProgressBar.setString("NPC Spawns: ----");
        npcSpawnsProgressBar.setStringPainted(true);

        final JPanel progressPanel = new JPanel(new GridLayout(2, 1));
        progressPanel.add(itemDefsProgressBar);
        progressPanel.add(npcSpawnsProgressBar);

        ItemDefinitionManager.setListener(this);

        saveItemsButton = new JButton("Save Items");
        saveItemsButton.addActionListener(this);

        final JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(saveItemsButton);

        final JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(toolBar, BorderLayout.WEST);
        northPanel.add(progressPanel, BorderLayout.CENTER);

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Items", itemDefsEditor = new ItemDefsEditor());

        add(northPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void onUpdate(final boolean saving, final int current, final double progress, final ItemDefinition def){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        itemDefsProgressBar.setValue(current);
                        if(progress == 100)
                            itemDefsProgressBar.setString(String.format("Item Definitions: Finished %s.", saving ? "saving" : "loading"));
                        else
                            itemDefsProgressBar.setString(String.format("Item Definitions: %s... %d / %d = %1.2f%%", saving ? "Saving" : "Loading", current, ItemDefinitionManager.MAX, progress));
                        itemDefsProgressBar.repaint();
                    }
                }
        );
    }

    public void actionPerformed(final ActionEvent e){
        final Object source = e.getSource();
        if(source.equals(saveItemsButton)){
            new SwingWorker<Void, Void>(){
                public Void doInBackground(){
                    saveItemsButton.setEnabled(false);
                    saveItemsButton.repaint();
                    itemDefsEditor.setFunctional(false);
                    try{
                        ItemDefinitionManager.save();
                    }catch(Exception ex){
                        JOptionPane.showMessageDialog(null, "Error saving item definitions: " + ex);
                    }
                    saveItemsButton.setEnabled(true);
                    saveItemsButton.repaint();
                    itemDefsEditor.setFunctional(true);
                    return null;
                }
            }.execute();
        }
    }

    public static void main(String[] args){
        instance = new ConfigEditor();
    }
}
