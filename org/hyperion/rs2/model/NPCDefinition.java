package org.hyperion.rs2.model;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.rs2.event.impl.WildernessBossEvent;
import org.hyperion.rs2.model.combat.attack.AvatarOfDestruction;
import org.hyperion.rs2.model.combat.attack.BorkAndMinions;
import org.hyperion.rs2.model.combat.attack.GodWarsBandos;
import org.hyperion.rs2.model.combat.attack.RevAttack;
import org.hyperion.rs2.util.IoBufferUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Represents a type of NPC.</p>
 *
 * @author Graham Edgecombe
 */
public class NPCDefinition {

	/**
	 * Gets an npc definition by its id.
	 *
	 * @param id The id.
	 * @return The definition.
	 */
	public static NPCDefinition forId(int id) {
		if(definition[id] == null)
			return new NPCDefinition(id);
		else return definition[id];
	}

	private static NPCDefinition[] definition = new NPCDefinition[12000];
	public static NPCDefinition[] getDefinitions() {
		return definition;
	}
	private int[] atkEmote = new int[5];
	private int deathEmote = 0x900;
	private int blockEmote = 404;

	//public int[][] dropId = null;//structer dropId[][itemId-0,minAm-1, maxAm-2, percentage out of 1000-3]
	//Map.Entry<NPCID, NPCDrop>
	private List<NPCDrop> drops = new LinkedList<>(); //too lazy to find/set size cba
	
	public List<NPCDrop> getDrops() {
		return drops;
	}

	private int spawnTime = 30;
	private int combat = 3;
	private int maxHp = 0;
	private String name = "null";
	private int[] bonus = new int[10];
	private int size = 0;

	private boolean doesDefEmote = true;

	public boolean doesDefEmote() {
		return doesDefEmote;
	}

	public int getAtkEmote(int i) {
		return atkEmote[i];
	}

	public int deathEmote() {
		return deathEmote;
	}

	public int blockEmote() {
		return blockEmote;
	}

	public int spawnTime() {
		return spawnTime;
	}

	public int combat() {
		return combat;
	}

	public int maxHp() {
		return maxHp;
	}

	public String name() {
		return name;
	}

	public String getName() {
		return name;
	}

	public int[] getBonus() {
		return bonus;
	}

	public int sizeX() {
		return size;
	}

	public int sizeY() {
		return size;
	}

	public static void init() {
	    /*try {
			String line = "";
			String token = "";
			String token2 = "";
			String token2_2 = "";
			String[] token3 = new String[5];
			boolean EndOfFile = false;
			int ReadMode = 0;
			BufferedReader characterfile = null;
			try {
				characterfile = new BufferedReader(new FileReader("./data/NPCEmotes.cfg"));
			} catch(Exception fileex) {
			} 
			try {
				line = characterfile.readLine();
			} catch(Exception exception) {
			} 
			while(EndOfFile == false && line != null) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token2_2 = token2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token3 = token2_2.split("\t");
					if (token.equals("npcID")) {
						atkEmote[Integer.parseInt(token3[0])][0] = Integer.parseInt(token3[1]);
						blockEmote[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
						deathEmote[Integer.parseInt(token3[0])] = Integer.parseInt(token3[3]);
						if(token3.length >= 6){
							for(int i = 0; i < (token3.length-5); i++){
								atkEmote[Integer.parseInt(token3[0])]
									[1+i] 
									= Integer.parseInt(token3[5+i]);
							}
						}
					}
				} else { 
					if (line.equals("[ENDOFNPCEMOTES]")) {
						try { 
							characterfile.close(); 
						} catch(Exception exception) { }
					}
				}
				try {
					line = characterfile.readLine();
				} catch(Exception exception1) {
					EndOfFile = true;
				}
			}
			try { 
				characterfile.close();
			} catch(Exception exception) { }
		} catch(Exception exception23) { 
			exception23.printStackTrace();
		}
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader("./data/npcdefinitions.cfg"));
			while (true) {
				String line = file.readLine();
				if (line == null)
					break;
				int spot = line.indexOf('=');
				if (spot > -1) {
					String values = line.substring(spot + 1);
					values = values.replace("\t\t", "\t");
					values = values.replace("\t\t", "\t");
					values = values.trim();
					String[] valuesArray = values.split("\t");
					int time = 30;
					if (valuesArray.length > 4) {
						time = Integer.valueOf(valuesArray[4]);
					}
					names[Integer.valueOf(valuesArray[0])] = valuesArray[1].replaceAll("_", " ");
					combat[Integer.valueOf(valuesArray[0])] = Integer.valueOf(valuesArray[2]);
					maxHp22[Integer.valueOf(valuesArray[0])] = Integer.valueOf(valuesArray[3]);
					spawnTime[Integer.valueOf(valuesArray[0])] = time;
				}
			}
		} finally {
			if (file != null)
				file.close();
		}
		try{
		OutputStream os = new FileOutputStream("data/npcdump.bin");
		buf = IoBuffer.allocate(1024);
		buf.setAutoExpand(true);
		for(int i = 0; i < names.length; i++){
			buf.putShort((short) i);
			if(names[i] == null)
				names[i] = "null";
			IoBufferUtils.putRS2String(buf, names[i]);
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
			
			for(int i2 = 0; i2 < 10; i2++){
				buf.putShort((short) i2);
			}
			
		}
		buf.flip();
		byte[] data = new byte[buf.limit()];
		buf.get(data);
		os.write(data);
		os.flush();
		os.close();
		} catch(Exception e){e.printStackTrace();}*/
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
						int id = buf.getUnsignedShort();
						String name = IoBufferUtils.getRS2String(buf);
						int combat = buf.getUnsignedShort();
						int hp = buf.getUnsignedShort();
						int spawnTime = buf.getUnsignedShort();
						int deathEmote = buf.getUnsignedShort();
						int blockEmote = buf.getUnsignedShort();
						int atkLenght = buf.getUnsigned();
						int[] attacks = new int[4];
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
						j++;
						switch(id) { //To Hardcode HP etc
                            case 83:
                                hp = 100;
                                break;
                            case 63:
                                hp = 47;
                                break;
                            case 50:
                                hp = 950;
                                combat = 350;
								spawnTime = 250;
								break;
							case 5666:
								hp = 400;
								combat = 200;
								spawnTime = 180;
								break;
							case 1590:
                            case 1591:
                                hp *= 3;
                                combat *= 1.15;
                                break;
                            case 1592:
                                hp *= 2;
                                break;
                            case 54:
                                combat = 240;
                                hp = 210;
                                break;
							case 9463:
								hp = 400;
								break;
							case 1677:
							case 1678:
								hp = 100;
								break;
							case 2026:
							case 2029:
							case 2027:
							case 2025:
							case 2028:
							case 2030:
								hp = 120;
								break;
                            case 5399:
                                hp = 70;
                                combat = 180;
                                break;
							case GodWarsBandos.BANDOS_BOSS:
								hp = 255;
								break;
						}

						NPCDefinition npcDef = new NPCDefinition(id, hp, combat, bonus, deathEmote, blockEmote, attacks, size, name, spawnTime);
						definition[id] = npcDef;

					} else {
						int[] bonus = new int[10];
						for(int i2 = 0; i2 < 10; i2++) {
							bonus[i2] = 340;
						}
						int[] atks = {12791};
						definition[9463] = new NPCDefinition(9463, 450, 220, bonus, 12793, 12791, atks, 2, "Ice_Strykewyrm", 30);
						definition[8349] = new NPCDefinition(8349, 700, 350, bonus, 10924, 10923, atks, 2, "Tormented_Demons", 30);
						definition[8133] = NPCDefinition.create(8133, 1200, 650, bonus, 10059, 10053, new int[]{10057, 10058}, 4, "Corporeal_Beast", 184);
						for(int n : SummoningMonsters.SUMMONING_MONSTERS) {
							definition[n] = SummoningMonsters.loadDefinition(n);
						}
						for(int n : RevAttack.getRevs()) {
							if((definition[n] = RevAttack.loadDefinition(n)) != null)
								System.out.println("Rev monster: "+n+" added, name: "+definition[n].getName());
						}
                        AvatarOfDestruction.loadDefinitions();
                        BorkAndMinions.init();
                        WildernessBossEvent.init();
						//int id, int maxHp, int cb, int[] bonus, int deathAnim, int blockAnim, int[] atkAnims, int size, String name, int spawnTime
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
		//System.out.println("Loaded "+j+" NPC Definitions.");
	}
	
	public static final NPCDefinition create(int id, int maxHp, int cb, int[] bonus, int deathAnim, int blockAnim, int[] atkAnims, int size, String name, int spawnTime) {
		return new NPCDefinition(id, maxHp, cb, bonus, deathAnim, blockAnim, atkAnims, size, name, spawnTime);
	}
	
	/*private static IoBuffer buf = null;
	public static int npcSize(int npcType)//extra value added to x and y cause the npc is fat (this brings the absX to the CENTRE) - credits scu11 cause i was too lazy to do it lol
	{
		switch(npcType)
		{
			case 50:
			case 53:
			case 54:
			case 55:
			case 941:
			case 1590:
			case 1591:
			case 1592:
			case 1155:
			case 1158:
			case 1160:
			case 2741:
			case 2742:
			case 2743:
			case 2744:
			case 2745:
				return 2;
			case 116:
			case 1153:
			case 1154:
			case 2452:
			case 2453:
			case 2629:
			case 2630:
			case 2631:
			case 2632:
			case 2881:
			case 2882:
			case 2883:
			case 3058:
			case 3063:
			case 3064:
			case 3066:
			case 2890:
			case 3777:
			case 3778:
			case 3779:
			case 3780:  			
			case 3776:
				return 1;
			default:
				return 0;
		}
	}*/

	/**
	 * The id.
	 */
	private int id;

	/**
	 * Creates the definition.
	 *
	 * @param id The id.
	 */
	public NPCDefinition(int id) {
		this.id = id;
	}

	public NPCDefinition(int id, int maxHp, int cb, int[] bonus, int deathAnim, int blockAnim, int[] atkAnims, int size, String name, int spawnTime) {
		this.id = id;
		this.atkEmote[0] = 0x326;
		this.maxHp = maxHp;
		this.combat = cb;
		this.bonus = bonus;
		this.name = name;
		this.size = size;
		if(deathAnim > 0)
			this.deathEmote = deathAnim;
		if(blockAnim > 0)
			this.blockEmote = blockAnim;
		if(spawnTime > 0)
			this.spawnTime = spawnTime;
		if(atkAnims[0] != 0)
			this.atkEmote = atkAnims;
		//if(doesDefAnim(id))
		//this.doesDefEmote = false;
	}


	/**
	 * Gets the id.
	 *
	 * @return The id.
	 */
	public int getId() {
		return this.id;
	}

}
