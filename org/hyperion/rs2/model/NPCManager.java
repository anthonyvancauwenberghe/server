package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.util.ClassUtils;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NPCManager {

	public void restoreArea(Location location) throws IOException {
		// NPC npc =
		// World.getWorld().getNPCManager().addNPC(player.getLocation().getX(),
		// player.getLocation().getY(), player.getLocation().getZ(),
		// Integer.parseInt(as[1]), -1);
		// npc.agressiveDis = 25;
		// spawn = 175 2159 5104 0 2160 5105 2158 5103 1
		// TextUtils.writeToFile("./data/spawns.cfg",
		// "spawn = "+Integer.parseInt(as[1])+"	"+player.getLocation().getX()+"	"+player.getLocation().getY()+"	"+player.getLocation().getZ()+"	"+(player.getLocation().getX()-1)+"	"+(player.getLocation().getY()-1)+"	"+(player.getLocation().getX()+1)+"	"+(player.getLocation().getY()+1)+"	1");

		String name = "./data/spawnsold.cfg";
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(name));
			while(true) {
				String line = file.readLine();
				if(line == null)
					break;
				int spot = line.indexOf('=');
				if(spot > - 1) {
					String values = line.substring(spot + 1);
					values = values.replace("\t\t", "\t");
					values = values.replace("\t\t", "\t");
					values = values.trim();
					String[] valuesArray = values.split("\t");
					int id = Integer.valueOf(valuesArray[0]);
					Location l = Location.create(
							Integer.valueOf(valuesArray[1]),
							Integer.valueOf(valuesArray[2]),
							Integer.valueOf(valuesArray[3]));
					if(location.distance(l) <= 64
							&& positionMap
							.get((Integer.valueOf(valuesArray[1]) * 16 + Integer
									.valueOf(valuesArray[2]) * 4)) == null) {
						NPC npc = World
								.getWorld()
								.getNPCManager()
								.addNPC(Integer.valueOf(valuesArray[1]),
										Integer.valueOf(valuesArray[2]),
										Integer.valueOf(valuesArray[3]), id, 60);
						TextUtils.writeToFile(
								"./data/spawns.cfg",
								"spawn = " + id + "	" + l.getX() + "	"
										+ l.getY() + "	" + l.getZ() + "	"
										+ (l.getX() - 2) + "	" + (l.getY() - 2)
										+ "	" + (l.getX() + 2) + "	"
										+ (l.getY() + 2) + "	1	"
										+ NPCDefinition.forId(id).name());
						positionMap.put(
								(Integer.valueOf(valuesArray[1]) * 16 + Integer
										.valueOf(valuesArray[2]) * 4), npc);
					}
	                /*
					 * addNPC(Integer.valueOf(valuesArray[1]),
					 * Integer.valueOf(valuesArray[2]),
					 * Integer.valueOf(valuesArray[3]),id,60);
					 */

				}

			}
		} finally {
			if(file != null)
				file.close();
		}
	}

	public static Map<Integer, NPC> positionMap = new HashMap<Integer, NPC>();
	public static NPC banker;

	public NPCManager() throws IOException {
		//System.out.println("Starting with Loading NPCS!");
		NPCDefinition.init();
		startUp();
		String name = "./data/spawns.cfg";
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader(name));
			while(true) {
				String line = file.readLine();
				if(line == null)
					break;
				int spot = line.indexOf('=');
				if(spot > - 1) {
					String values = line.substring(spot + 1);
					values = values.replace("\t\t", "\t");
					values = values.replace("\t\t", "\t");
					values = values.trim();
					String[] valuesArray = values.split("\t");
					int id = Integer.valueOf(valuesArray[0]);
					/*
					 * if(Integer.valueOf(valuesArray[2]) == 3366){
					 * System.out.println(Integer.valueOf(valuesArray[1]) +
					 * " space " + Integer.valueOf(valuesArray[2]) + " space " +
					 * Integer.valueOf(valuesArray[3])); }
					 */
					NPC npc = addNPC(Integer.valueOf(valuesArray[1]),
							Integer.valueOf(valuesArray[2]),
							Integer.valueOf(valuesArray[3]), id, NPCDefinition.forId(Integer.valueOf(valuesArray[1])).spawnTime() == 30 ? 60 : -1);
					if(Integer.valueOf(valuesArray[6]) > 5)
						npc.walkToXMax = Integer.valueOf(valuesArray[6]);
					if(Integer.valueOf(valuesArray[4]) > 5)
						npc.walkToXMin = Integer.valueOf(valuesArray[4]);
					if(Integer.valueOf(valuesArray[7]) > 5)
						npc.walkToYMax = Integer.valueOf(valuesArray[7]);
					if(Integer.valueOf(valuesArray[5]) > 5)
						npc.walkToYMin = Integer.valueOf(valuesArray[5]);
					if(Integer.valueOf(valuesArray[8]) <= 1)
						npc.randomWalk = true;
					npc.bones = getBones(id, npc.getDefinition().getName());

					if(id == 495 && banker == null)
						banker = npc;
					positionMap.put(
							(Integer.valueOf(valuesArray[1]) * 16 + Integer
									.valueOf(valuesArray[2]) * 4), npc);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(file != null)
				file.close();
		}
		
		HaloweenEvent.init(positionMap);

        for(SpecialArea area : SpecialAreaHolder.getAreas()) {
            if(area instanceof NIGGERUZ)
                ((NIGGERUZ)area).initNpc(this, positionMap);
        }

		name = "./data/npcdrops.cfg";
		file = null;
		int lineInt = 1;
		try {
			file = new BufferedReader(new FileReader(name));
			String line;
			while((line = file.readLine()) != null) {
				int spot = line.indexOf('=');
				if(spot > - 1) {
					int id = 0;
					int i = 1;
					try {
						if(line.contains("/"))
							line = line.substring(spot + 1, line.indexOf("/"));
						else
							line = line.substring(spot + 1);
						String values = line;
						values = values.replaceAll("\t\t", "\t");
						values = values.trim();
						String[] valuesArray = values.split("\t");


						id = Integer.valueOf(valuesArray[0]);
						NPCDefinition def = NPCDefinition.forId(id);
						/**
						 * Commented out old NPCDrops
						 */
						//NPCDefinition.forId(id).dropId = new int[(valuesArray.length + 1)][4];


						for(i = 1; i < valuesArray.length; i++) {
							String[] itemData = valuesArray[i].split("-");
							final int itemId = Integer.valueOf(itemData[0]);
							final int minAmount = Integer.valueOf(itemData[1]);
							final int maxAmount = Integer.valueOf(itemData[2]);
							final int chance = Integer.valueOf(itemData[3]);
							
							def.getDrops().add(NPCDrop.create(itemId, minAmount, maxAmount, chance));
							/*NPCDefinition.forId(id).dropId[i][0] = Integer
									.valueOf(itemData[0]);// itemId
							NPCDefinition.forId(id).dropId[i][1] = Integer
									.valueOf(itemData[1]);// minAm
							NPCDefinition.forId(id).dropId[i][2] = Integer
									.valueOf(itemData[2]);// maxAm
							NPCDefinition.forId(id).dropId[i][3] = Integer
									.valueOf(itemData[3]);// chance*/
						}
					} catch(Exception e) {
						e.printStackTrace();
						System.out.println("error on array: " + i + " npcId: "
								+ id);
					}
				}
				lineInt++;

			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("error on line: " + lineInt + " ");
		} finally {
			if(file != null)
				file.close();
		}
		// DumpNpcDrops.startDump();
	}

	private int getBones(int id, String name) {
		name = name.toLowerCase();
		if(id >= 4278 && id <= 4284)
			return 0;
		if((id >= 2025 && id < 2030) || (id >= 2627 && id < 2745))
			return 0;
		if(name.contains("giant")) {
			return 532;
		} else if(name.contains("jogre")) {
			return 3125;
		} else if(name.contains("zogre")) {
			return 4812;
		} else if(name.contains("baby") && name.contains("dragon")) {
			return 534;
		} else if(name.contains("dragon")) {
			return 536;
		} else if(name.contains("bat")) {
			return 530;
		}
		return 526;
	}

	public static int getCharms(int npcId, String name) {
		if(npcId >= 4278 && npcId <= 4284)
			return 0;
		NPCDefinition nD = NPCDefinition.forId(npcId);
		if(nD.combat() < 10)
			return 0;
		name = name.toLowerCase();
		int random = Misc.random(20);

		if(name.toLowerCase().contains("abyssal")) {
			return 12161; // abyssal charm
		} else if(random >= 1 && random <= 5) {
			return 12158; // gold charm
		} else if(random >= 5 && random <= 9) {
			return 12159; // green charm
		} else if(random >= 10 && random <= 13) {
			return 12160; // crimson charm
		} else if(random == 14) {
			return 12163; // blue charm
		} else
			return 0;
	}

	public static int getTalismine(NPCDefinition nD) {
		if(nD.combat() < 10)
			return 0;
		int random = Misc.random(20);
		if(random != 1)
			return 0;
		if(nD.combat() > 200)
			return getVHighTali();
		if(nD.combat() > 120)
			return getHighTali();
		if(nD.combat() > 80)
			return getMedTali();
		if(nD.combat() > 50)
			return getLowTali();
		return 0;
	}

	private static int getHighTali() {
		// TODO Auto-generated method stub
		int[] array = {1452, 1462,};
		return array[Misc.random(array.length - 1)];
	}

	private static int getLowTali() {
		int[] array = {1438, 1440, 1442, 1444, 1446, 1448,};
		return array[Misc.random(array.length - 1)];
	}

	private static int getMedTali() {
		int[] array = {1454,};
		return array[Misc.random(array.length - 1)];
	}

	private static int getVHighTali() {
		int[] array = {1450, 1456, 1458, 1460, 5516,};
		return array[Misc.random(array.length - 1)];
	}

	public NPC addNPC(Location loc, int npcId, int respawnTime) {
		NPCDefinition nD = NPCDefinition.forId(npcId);
		NPC n = new NPC(nD, respawnTime, loc);
		n.agressiveDis = getAgreDis(npcId);
		n.bones = getBones(npcId, n.getDefinition().getName());
		World.getWorld().npcsWaitingList.add(n);
		return n;
	}

	public NPC addNPC(int x, int y, int z, int npcId, int respawnTime) {
		return addNPC(Location.create(x, y, z), npcId, respawnTime);
	}

	public static final int getAgreDis(int npcId) {
		switch(npcId) {
            case 7135:
            case 7134:
                return 15;
            case 5399:
                return 8;
			case 2881:
			case 2882:
			case 2883:
			case 2892:
				return 50;

			case 6222:
			case 6223:
			case 6225:
			case 6227:

			case 6261:
			case 6263:
			case 6265:
			case 6260:
			case 8133:
			case 6208:
			case 6204:
			case 6206:
			case 6203:
				return 10;
            case 63:
                return 6;

            case 50:
                return 15;
			default:
				return 0;
		}
	}

	public void startUp() {
		try {
			int counter = 0;
			Class[] classes = ClassUtils.getClasses("org.hyperion.rs2.model.combat.attack");
			for(Class cls : classes) {
				if(Attack.class.isAssignableFrom(cls) && cls != Attack.class) {
					try {
						Attack attack = (Attack) cls.newInstance();
						int[] ids = attack.npcIds();
						if(ids[0] == - 1) {
							for(int i = 0; i < attacks.length; i++) {
								if(attacks[i] == null)
									attacks[i] = attack;
							}
						} else {
							for (int id : ids) {
								attacks[id] = attack;
							}
						}
						counter++;
					} catch(Exception e) {
						System.out.println("Failed to load attack: " + cls);
					}
				}
			}
			System.out.println("Loaded " + counter + " NPC Attacks.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean trodOnNpc(NPC npc, int i, int j) {
		int k = 1 + npc.getDefinition().sizeX();
		int l = 1 + npc.getDefinition().sizeY();
		int ai[][] = new int[16][2];
		for(int i1 = 0; i1 < k; i1++) {
			for(int j1 = 0; j1 < l; j1++) {
				ai[i1 + j1][0] = i + i1;
				ai[i1 + j1][1] = j + j1;
			}

		}

		for(NPC npc1 : World.getWorld().getRegionManager().getLocalNpcs(npc)) {
			if(npc != npc1 && ! npc1.isHidden()) {
				int k1 = npc1.getLocation().getX();
				int l1 = npc1.getLocation().getY();
				WalkingQueue.Point point = npc1.getWalkingQueue()
						.getPublicPoint();
				if(point != null) {
					k1 = point.getX();
					l1 = point.getY();
				}
				int i2 = 0;
				while(i2 < npc1.getDefinition().sizeX() + 1) {
					for(int j2 = 0; j2 < npc1.getDefinition().sizeY() + 1; j2++) {
						for(int k2 = 0; k2 < 16; k2++) {
							if(k1 + i2 == ai[k2][0] && l1 + j2 == ai[k2][1]) {
								return true;
							}
						}

					}

					i2++;
				}
			}
		}
		return false;
	}

	public Attack getAttack(NPC n) {
		return attacks[n.getDefinition().getId()];
	}

	public Attack[] attacks = new Attack[12000];// 3000 npcs?

	static {
		CommandHandler.submit(new Command("npcids", Rank.MODERATOR) {
			@Override
			public boolean execute(Player player, String input) throws Exception {
				for(NPC npc : World.getWorld().getNPCs()) {
					if(npc == null)
						continue;
					int distance = player.getLocation().distance(npc.getLocation());
					if(distance < 5) {
						player.getActionSender().sendMessage("Npc: " + npc.getDefinition().getId() + " , " + npc.getDefinition().combat());
					}
				}
				return true;
			}
		});
	}

}