package cluescrolleditor;

import cluescrolleditor.editor.ClueScrollEditor;
import cluescrolleditor.editor.list.ClueScrollList;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import org.hyperion.rs2.model.cluescroll.ClueScrollManager;

public class ClueScrollEditorFrame extends JFrame {

    private static ClueScrollEditorFrame instance;

    private final ClueScrollList list;
    private final ClueScrollEditor editor;

    public ClueScrollEditorFrame(){
        super("Clue Scroll Editor");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        list = new ClueScrollList();

        editor = new ClueScrollEditor();

        add(new ToolBar(), BorderLayout.NORTH);
        add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, editor), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);

        setVisible(true);

        new SwingWorker<Void, Void>(){
            public Void doInBackground(){
                try{
                    ClueScrollManager.load();
                    ClueScrollManager.getAll().forEach(list::add);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();

        setExtendedState(MAXIMIZED_BOTH);
    }

    public ClueScrollList getList(){
        return list;
    }

    public ClueScrollEditor getEditor(){
        return editor;
    }

    public static ClueScrollEditorFrame getInstance(){
        if(instance == null)
            instance = new ClueScrollEditorFrame();
        return instance;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(ClueScrollEditorFrame::getInstance);
    }
}
