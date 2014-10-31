package cfgeditor.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public abstract class LabelledComp<T extends JComponent, R> extends JPanel {

    public final T comp;

    public LabelledComp(final String text, final T comp){
        super(new BorderLayout());
        this.comp = comp;

        final JLabel label = new JLabel(text, JLabel.RIGHT);
        label.setBorder(new EmptyBorder(0, 0, 0, 15));

        add(label, BorderLayout.WEST);
        add(comp, BorderLayout.CENTER);

        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
    }

    public abstract R value();

    public void set(final R value){
        SwingUtilities.invokeLater(
                new Runnable(){
                    public void run(){
                        set$(value);
                        comp.repaint();
                    }
                }
        );
    }

    protected abstract void set$(final R value);
}
