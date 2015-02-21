package org.madturnip.tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.skill.dungoneering.RoomDefinition;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/21/15
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoomDefinitionCreator extends JFrame {

    final Player player;

    private final DefaultListModel<DefinitionFrame> frames = new DefaultListModel<>();

    public RoomDefinitionCreator(final Player player) {
        super("Room Definition Creator");

        this.player = player;
        final JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        final JPanel north = new JPanel();

        final JList<DefinitionFrame> list = new JList<>(frames);


        final JButton open = new JButton("Open");
        final JButton add = new JButton("Add");

        north.add(open);
        north.add(add);

        open.addActionListener(e -> list.getSelectedValue().setVisible(true));
        add.addActionListener(e -> frames.addElement(new DefinitionFrame()));


        panel.add(north, BorderLayout.NORTH);

        panel.add(list, BorderLayout.CENTER);

        setContentPane(panel);

    }


    class DefinitionFrame extends JFrame {

        final InputPanel loc = new InputPanel("Location: ");
        final List<InputPanel> spawnLocations = new ArrayList<InputPanel>();

        public DefinitionFrame() {
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(loc, BorderLayout.NORTH);

            final JPanel center = new JPanel();


            final JButton south = new JButton("Add");

            getContentPane().add(south, BorderLayout.SOUTH);

            south.addActionListener(e -> {
                final InputPanel panel = new InputPanel("Spawn Location "+spawnLocations.size());
                spawnLocations.add(panel);
                center.add(panel);
                pack();
            });

            pack();

        }

        public RoomDefinition toDefinition() {
            final List<Point> points = new ArrayList<>();
            spawnLocations.stream().map(InputPanel::loc).forEach(points::add);
            return new RoomDefinition(loc.loc().x, loc.loc().y, points);
        }

        @Override
        public String toString() {
            return "Room Definition";
        }

    }

    class InputPanel extends JPanel {

        final JTextField x_field = new JTextField("0"), y_field = new JTextField("0");

        public InputPanel(final String name) {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

            final JLabel label = new JLabel(name + ": ");
            final JLabel x = new JLabel("X: "), y = new JLabel("Y: ");

            add(label);
            add(x); add(x_field);
            add(y); add(y_field);

            final JButton button = new JButton("Load");

            add(button);
            button.addActionListener(e -> {
                x_field.setText(player.getLocation().getX()+"");
                y_field.setText(player.getLocation().getY()+"");
            });
        }

        public final Point loc() {
            return new Point(Integer.parseInt(x_field.getText()), Integer.parseInt(y_field.getText()));
        }

    }

    public void save() {
        try {
            OutputStream os = new FileOutputStream("data/roomdef.bin");
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            for(int i = 0; i < frames.size(); i++) {
                frames.get(i).toDefinition().save(buf);
            }
            buf.flip();
            byte[] data = new byte[buf.limit()];
            buf.get(data);
            os.write(data);
            os.flush();
            os.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
