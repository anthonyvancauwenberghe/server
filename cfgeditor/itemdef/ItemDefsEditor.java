package cfgeditor.itemdef;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.hyperion.rs2.model.ItemDefinition;

public class ItemDefsEditor extends JPanel implements ListSelectionListener {

    private static class Renderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(final JList l, final Object o, final int i, final boolean s, final boolean f){
            final Component c = super.getListCellRendererComponent(l, o, i, s, f);
            if(o == null)
                return c;
            final ItemDefinition def = (ItemDefinition) o;
            final JLabel label = (JLabel) c;
            label.setText(String.format("%s (%d)", def.getName(), def.getId()));
            label.setToolTipText(String.format("<html><center><img src=\"https://soulsplit.com/item_database/item_pics/items/%d.png\"/></center><br><center>%s (%d)</center><br><center>%s</center></html>", def.getId(), def.getName(), def.getId(), def.getDescription()));
            if(i == 0 || i % 2 == 0){
                label.setForeground(Color.WHITE);
                label.setBackground(Color.GRAY);
            }
            return label;
        }
    }

    private final JList<ItemDefinition> list;
    private final DefaultListModel<ItemDefinition> model;

    private final SingleItemDefEditor editor;

    public ItemDefsEditor(){
        super(new BorderLayout());

        model = new DefaultListModel<>();

        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new Renderer());
        list.addListSelectionListener(this);

        editor = new SingleItemDefEditor();

        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), editor), BorderLayout.CENTER);

        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                try{
                    ItemDefinitionManager.load();
                    for(final ItemDefinition def : ItemDefinitionManager.getDefinitions())
                        if(def != null){
                            SwingUtilities.invokeLater(
                                    new Runnable() {
                                        public void run() {
                                            model.addElement(def);
                                            list.repaint();
                                        }
                                    }
                            );
                        }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void setFunctional(final boolean functional){
        list.setEnabled(functional);
        list.repaint();
        editor.setFunctional(functional);
    }

    public void valueChanged(final ListSelectionEvent e){
        final Object source = e.getSource();
        if(!source.equals(list))
            return;
        editor.set(list.getSelectedValue());
    }
}
