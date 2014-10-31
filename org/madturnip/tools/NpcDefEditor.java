package org.madturnip.tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.util.IoBufferUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

public class NpcDefEditor {

	public static void main(String[] agrs) {
		new NpcDefEditor();
	}

	public JFrame frame;
	public JButton button1, button2, button3;
	public JTextField[] fields = new JTextField[19];//19
	//public JTextField npcId, npcName, npcCombat, npcHp, spawnTime, deathEmote, blockEmote, attackEmote, npcSize, bonus0, bonus1, bonus2, bonus3, bonus4, bonus5, bonus6, bonus7, bonus8, bonus9;

	/*
	buf.putShort((short) i);
			buf.putString((short) names[i]);
			buf.putShort((short) combat[i]);
			buf.putShort((short) maxHp22[i]);
			buf.putShort((short) spawnTime[i]);
			buf.putShort((short) deathEmote[i]);
			buf.putShort((short) blockEmote[i]);
			int i3 = 0;
			for(int i2 = 0; i2 < atkEmote[i].length; i2++){
				if(atkEmote[i][i2] > 0)
					i3++;
			}
			buf.put((byte) i3);
			for(int i2 = 0; i2 < i3; i2++){
				buf.putShort((short) atkEmote[i][i2]);
			}
			buf.put((byte) npcSize(i));
	*/
	public String[] names = {"npcId", "npcName", "npcCombat", "npcHp", "spawnTime", "deathEmote", "blockEmote", "attackEmote", "npcSize", "attack stab", "attack slash", "attack crush", "attack range", "attack magic", "defence stab", "defence slash", "defence crush", "defence magic", "defence range",};

	public void saveFile() {

		try {
			OutputStream os = new FileOutputStream("data/npcdump.bin");
			IoBuffer buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);
			for(int i = 0; i < maxId; i++) {
				buf.putShort((short) i);
				if(names3[i] == null)
					names3[i] = "null";
				IoBufferUtils.putRS2String(buf, names3[i]);
				buf.putShort((short) combat[i]);
				buf.putShort((short) maxHp22[i]);
				buf.putShort((short) spawnTime[i]);
				buf.putShort((short) deathEmote[i]);
				buf.putShort((short) blockEmote[i]);
				int i3 = 0;
				for(int i2 = 0; i2 < atkEmote[i].length; i2++) {
					if(atkEmote[i][i2] > 0)
						i3++;
				}
				buf.put((byte) i3);
				for(int i2 = 0; i2 < i3; i2++) {
					buf.putShort((short) atkEmote[i][i2]);
				}
				buf.put((byte) size[i]);

				for(int i2 = 0; i2 < 10; i2++) {
	                /*if(i2 < 5)
						buf.putShort((short) (combat[i]*2.25));
					else
						buf.putShort((short) combat[i]);*/
					buf.putShort((short) bonus[i][i2]);
				}

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

	public void loader() {
		/*int id = (int) buf.getUnsignedShort();
		names3[id] = IoBufferUtils.getRS2String(buf);
		combat[id] = (int) buf.getUnsignedShort();
		maxHp22[id] = (int) buf.getUnsignedShort();
		spawnTime[id] = (int) buf.getUnsignedShort();
		deathEmote[id] = (int) buf.getUnsignedShort();
		blockEmote[id] = (int) buf.getUnsignedShort();
		int attacks = buf.get();
		for(int i2 = 0; i2 < attacks; i2++){
			atkEmote[id][i2] = (int) buf.getUnsignedShort();
		}
		size[id] = buf.get();
		for(int i = 0; i < 10; i++){
			bonus[id][i] = (int) buf.getUnsignedShort();
		}*/

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
			int j = 0;
			while(true) {
				try {
					if(buf.hasRemaining()) {
						int id = (int) buf.getUnsignedShort();
						names3[id] = IoBufferUtils.getRS2String(buf);
						combat[id] = (int) buf.getUnsignedShort();
						maxHp22[id] = (int) buf.getUnsignedShort();
						spawnTime[id] = (int) buf.getUnsignedShort();
						deathEmote[id] = (int) buf.getUnsignedShort();
						blockEmote[id] = (int) buf.getUnsignedShort();
						int attacks = buf.get();
						for(int i2 = 0; i2 < attacks; i2++) {
							atkEmote[id][i2] = (int) buf.getUnsignedShort();
						}
						size[id] = buf.get();
						for(int i = 0; i < 10; i++) {
							bonus[id][i] = (int) buf.getUnsignedShort();
						}
						if(id > maxId)
							maxId = id;
						j++;
					} else {
						System.out.println("Loaded " + j + " NPC Definitions.");
						return;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} catch(Exception e2) {
			e2.printStackTrace();
		}
	}

	public static int maxId = 6391;

	public static int[][] atkEmote = new int[7000][5];
	public static int[] deathEmote = new int[7000];
	public static int[] blockEmote = new int[7000];

	public static int[] spawnTime = new int[7000];
	public static int[] combat = new int[7000];
	public static int[] maxHp22 = new int[7000];
	public static String[] names3 = new String[7000];
	public static int[] size = new int[7000];
	public static int[][] bonus = new int[7000][10];

	/*
 * The npc tab.
 */
	public JPanel npcListPanel;
	public JLabel npcListText;
	public JList npcList;
	public DefaultListModel npcListModel;
	public JScrollPane npcListPane;

	public NpcDefEditor() {
		/**
		 *	Constructor
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e) {
		} // leave default laf
		//JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("NPC Def Editor");
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		//npcs

		npcListPanel = new JPanel();
		npcListPanel.setLayout(new GridLayout(1, 1));
		npcListModel = new DefaultListModel();
		npcList = new JList(npcListModel);
		for(int i = 0; i < maxId; i++) {
			npcListModel.addElement("id: " + i);
		}
		npcListPane = new JScrollPane(npcList);
		npcListPanel.add(npcListPane);
		button2 = new JButton("Save NPC");


		// ============================
		// Finish npc list setup
		// ============================
		npcListText = new JLabel("NPC's: " + maxId);
		npcListPanel.setPreferredSize(new Dimension(200, 240));
		frame.getContentPane().add(npcListText, BorderLayout.NORTH);
		//npcListPanel.setBorder(BorderFactory.createEmptyBorder(2, 20, 5, 5));

		frame.getContentPane().add(npcListPanel, BorderLayout.WEST);


		JPanel gpanel = new JPanel();
		gpanel.setLayout(new GridLayout(7, 6));
		gpanel.setPreferredSize(new Dimension(480, 240));
		frame.getContentPane().add(gpanel, BorderLayout.CENTER);
		button1 = new JButton("Load NPC");
		button3 = new JButton("Save Definitions");
		for(int i = 0; i < 19; i++) {
			fields[i] = new JTextField(3);
			gpanel.add(new JLabel("    " + names[i] + ": "));
			gpanel.add(fields[i]);
		}
		JPanel gpanel2 = new JPanel();
		gpanel2.setLayout(new GridLayout(1, 3));
		gpanel2.add(button1, BorderLayout.SOUTH);
		gpanel2.add(button2, BorderLayout.SOUTH);
		gpanel2.add(button3, BorderLayout.SOUTH);
		frame.getContentPane().add(gpanel2, BorderLayout.SOUTH);
		for(int i = 0; i < 19; i++) {
			fields[i].setText("-1");
		}
		frame.pack();
		frame.setVisible(true);
		loader();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = npcList.getSelectedIndex();
				if(idx != - 1) {
					//String npcId = (String) npcListModel.get(idx);
					fields[0].setText("" + idx);
					fields[1].setText("" + names3[idx]);
					fields[2].setText("" + combat[idx]);
					fields[3].setText("" + maxHp22[idx]);
					fields[4].setText("" + spawnTime[idx]);
					fields[5].setText("" + deathEmote[idx]);
					fields[6].setText("" + blockEmote[idx]);
					fields[7].setText("" + atkEmote[idx][0] + ", " + atkEmote[idx][1] + ", " + atkEmote[idx][2] + ", " + atkEmote[idx][3] + ", " + atkEmote[idx][4]);
					fields[8].setText("" + size[idx]);
					for(int i = 0; i < 10; i++) {
						fields[9 + i].setText("" + bonus[idx][i]);
					}
				}
			}
		});
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				names3[Integer.parseInt(fields[0].getText())] = fields[1].getText();
				combat[Integer.parseInt(fields[0].getText())] = Integer.parseInt(fields[2].getText());
				maxHp22[Integer.parseInt(fields[0].getText())] = Integer.parseInt(fields[3].getText());
				spawnTime[Integer.parseInt(fields[0].getText())] = Integer.parseInt(fields[4].getText());
				deathEmote[Integer.parseInt(fields[0].getText())] = Integer.parseInt(fields[5].getText());
				blockEmote[Integer.parseInt(fields[0].getText())] = Integer.parseInt(fields[6].getText());
				String[] attacks = fields[7].getText().replace(" ", "").split(",");
				for(int i = 0; i < attacks.length; i++) {
					atkEmote[Integer.parseInt(fields[0].getText())][i] = Integer.parseInt(attacks[i]);
				}
				size[Integer.parseInt(fields[0].getText())] = Integer.parseInt(fields[8].getText());
				for(int i = 0; i < 10; i++) {
					bonus[Integer.parseInt(fields[0].getText())][i] = Integer.parseInt(fields[9 + i].getText());
				}
			}
		});
	}

}