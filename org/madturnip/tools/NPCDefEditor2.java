package org.madturnip.tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.util.IoBufferUtils;
import org.hyperion.rs2.util.TextUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/18/15
 * Time: 9:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class NPCDefEditor2 extends JPanel{

    private static final class NPCDef {

        private final int id, combat, hp, spawnTime, deathEmote, blockEmote, npcSize;
        private final String name;
        private final int[] attackEmotes, bonus;

        public NPCDef(final int id, final String name, final int combat, final int hp, final int spawnTime, final int deathEmote,
                      final int blockEmote, final int[] attackEmotes, final int npcSize, final int bonus[]) {

             this.id = id;
             this.combat = combat; this.name = name; this.hp = hp; this.spawnTime = spawnTime; this.deathEmote = deathEmote; this.blockEmote = blockEmote;
             this.attackEmotes = attackEmotes; this.npcSize = npcSize; this.bonus = bonus;

        }

        @Override
        public String toString() {
            return String.format("%d - %s", id, name);
        }

        public void save(final IoBuffer buf) {
            buf.putShort((short) id);
            IoBufferUtils.putRS2String(buf, name == null ? "null" : name);
            buf.putShort((short)combat);
            buf.putShort((short)hp);
            buf.putShort((short)spawnTime);
            buf.putShort((short)deathEmote);
            buf.putShort((short)blockEmote);
            int attack_size = 0;
            for(int i2 = 0; i2 < attackEmotes.length; i2++) {
                if(attackEmotes[i2] > 0)
                    attack_size++;
            }
            buf.put((byte)attack_size);
            for(int i = 0; i < attack_size; i++)
                buf.putShort((short)attackEmotes[i]);
            buf.put((byte)npcSize);
            for(int i = 0; i < 10; i++)
                buf.putShort((short)bonus[i]);

        }

    }

    public NPCDefEditor2() {
        setLayout(new BorderLayout());
        loadFromFile();

        final JList<NPCDef> jlist = new JList<>(npcs);

        add(new JScrollPane(jlist), BorderLayout.WEST);

        final JPanel fields = new JPanel();
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        for(final String s : names) {
            fields.add(new JLabelComponent(TextUtils.titleCase(s)));
        }

        add(fields, BorderLayout.CENTER);

        final JPanel south = new JPanel();

        south.setLayout(new BorderLayout());

        final JButton save = new JButton("Save File");
        final JButton add = new JButton("Add");
        final JButton load = new JButton("Load");

        save.addActionListener((e) -> save());
        add.addActionListener((e) -> {
            remove(JLabelComponent.getInt(names[0]), false);
            npcs.addElement(JLabelComponent.forFields());
        });

        load.addActionListener((e) -> JLabelComponent.load(jlist.getSelectedValue()));

        south.add(save, BorderLayout.EAST);
        south.add(add);
        south.add(load, BorderLayout.WEST);

        add(south, BorderLayout.SOUTH);
    }

    private final DefaultListModel<NPCDef> npcs = new DefaultListModel<>();

    private void loadFromFile() {
        try {
            File f = new File("./data/npcdump.bin");
            InputStream is = new FileInputStream(f);
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            while(true) {
                byte[] temp = new byte[1024];
                int read = is.read(temp, 0, temp.length);
                if(read == - 1) {
                    break;
                } else {
                    buf.put(temp, 0, read);
                }
            }
            buf.flip();
            while(buf.hasRemaining()) {
                try {
                    int id = buf.getUnsignedShort();
                    String name = IoBufferUtils.getRS2String(buf);
                    int combat = buf.getUnsignedShort();
                    int hp = buf.getUnsignedShort();
                    int spawnTime = buf.getUnsignedShort();
                    int deathEmote = buf.getUnsignedShort();
                    int blockEmote = buf.getUnsignedShort();
                    int atkLenght = buf.getUnsigned();
                    int[] attacks = new int[atkLenght];
                    //System.out.println("new atk anim");
                    for(int i2 = 0; i2 < atkLenght; i2++) {
                        attacks[i2] = buf.getUnsignedShort();
                        //System.out.println("" + attacks[i2]);
                    }
                    int size = buf.getUnsigned();
                    int[] bonus = new int[10];
                    for(int i2 = 0; i2 < 10; i2++) {
                        bonus[i2] = buf.getUnsignedShort();
                    }

                    npcs.addElement(new NPCDef(id, name, combat, hp, spawnTime, deathEmote, blockEmote, attacks, size, bonus));
                } catch(Exception ex) {

                }
            }
        }catch(final Exception ex) {

        }
    }

    private void save() {
        try {
            OutputStream os = new FileOutputStream("data/npcdump2.bin");
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            for(int i = 0; i < npcs.size(); i++) {
                npcs.get(i).save(buf);
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



    private void remove(int id, boolean NOTHING) {
        for(int i = 0; i < npcs.size(); i++) {
            if(npcs.get(i).id == id) {
                npcs.remove(i);
                return;
            }
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
        }

        final JFrame frame = new JFrame();

        frame.setPreferredSize(new Dimension(500, 400));

        frame.add(new NPCDefEditor2());

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public static final class JLabelComponent extends JPanel{

        public static final List<JLabelComponent> components = new ArrayList<>();

        public final JTextField box = new JTextField("-1");
        private final String name;

        public JLabelComponent(final String name) {
            setLayout(new BorderLayout());
            final JLabel label = new JLabel(this.name = name);
            box.setPreferredSize(new Dimension(100, 15));
            add(label);
            add(box, BorderLayout.EAST);
            components.add(this);
        }

        public String getText() {
            return box.getText();
        }

        public static JLabelComponent forName(final String name) {
            for(final JLabelComponent comp : components)
                if(comp.name.equalsIgnoreCase(name))
                    return comp;
            return null;
        }

        public static int getInt(final String name) {
            try {
                return forName(name) == null ? -1 : Integer.parseInt(forName(name).getText().trim());
            }catch(final Exception ex) {
                return 0;
            }
        }

        public static void setText(final String name, final Object text) {
            final JLabelComponent comp = forName(name);
            if(text == null) return;
            if(comp != null)
                comp.box.setText(text.toString());
        }

        public static void load(final NPCDef def) {
            setText(names[0], def.id);
            setText(names[1], def.name);
            setText(names[2], def.combat);
            setText(names[3], def.hp);
            setText(names[4], def.spawnTime);
            setText(names[5], def.deathEmote);
            setText(names[6], def.blockEmote);
            final StringBuilder builder = new StringBuilder();
            for(int i = 0; i < def.attackEmotes.length; i++) {
                builder.append(def.attackEmotes[i]);
                if(i != def.attackEmotes.length - 1)
                    builder.append(",");
            }

            setText(names[7], builder.toString());
            setText(names[8], def.npcSize);

            for(int i = 9; i < names.length; i++) {
                setText(names[i], def.bonus[i - 9]);
            }
        }

        public static NPCDef forFields() {
            final int id = getInt(names[0]);
            final String name = forName(names[1]).getText();
            final int combat = getInt(names[2]);
            final int hp = getInt(names[3]);
            final int spawnTime = getInt(names[4]);
            final int deathEmote = getInt(names[5]);
            final int blockEmote = getInt(names[6]);
            final String[] split = forName(names[7]).getText().split(",");
            int attackEmotes[] = new int[split.length];
            for(int i = 0; i < attackEmotes.length; i++) {
                try {
                    attackEmotes[i] = Integer.parseInt(split[i]);
                } catch(NumberFormatException e) {

                }
            }

            final int size = getInt(names[8]);
            final int[] bonus = new int[10];

            for(int i = 9; i < names.length; i++) {
                bonus[i - 9] = getInt(names[i]);
            }
            return new NPCDef(id, name, combat,hp, spawnTime, deathEmote, blockEmote, attackEmotes, size, bonus);
        }
    }

    public static String[] names = {"npcId", "npcName", "npcCombat", "npcHp", "spawnTime", "deathEmote", "blockEmote",
            "attackEmote", "npcSize", "attack stab", "attack slash", "attack crush", "attack magic", "attack range",
            "defence stab", "defence slash", "defence crush", "defence magic", "defence range",};

}
