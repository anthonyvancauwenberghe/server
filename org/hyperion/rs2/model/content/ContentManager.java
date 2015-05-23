package org.hyperion.rs2.model.content;

import java.util.HashMap;
import java.util.Map;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.minigame.ZombieMinigame;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.util.ClassUtils;

public class ContentManager {
	
	public ContentTemplate prayer = null;//key classes like this may be nessary for skills like this
	private ZombieMinigame zombieMinigame;
	public ZombieMinigame getZombieMinigame() {
		return zombieMinigame;
	}

	public void init() {
		try {
			for(int i = 0; i < packetHandlers; i++) {
				contentMaps[i] = null;
				contentMaps[i] = new HashMap<Integer, ContentTemplate>();
			}
			addContent();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void addContent() {
		try {
			Class[] classes = ClassUtils.getClasses("org.hyperion.rs2.model.content");
			for(Class cls : classes) {
				if(ContentTemplate.class.isAssignableFrom(cls) && cls != ContentTemplate.class && !cls.isEnum()) {
					try {
						ContentTemplate content = (ContentTemplate) cls.newInstance();
                        if(Interface.class.isAssignableFrom(cls)) {
                            InterfaceManager.addGlobal((Interface)content);
                        }
                        if(SpecialArea.class.isAssignableFrom(cls))
                            SpecialAreaHolder.put(cls.getSimpleName(), (SpecialArea)content, false);
						if(cls.getName().contains("prayer"))
							prayer = content;
						if(cls.getName().contains("zombieminigame"))
							zombieMinigame = (ZombieMinigame)content;

						content.init();
						for(int i = 0; i < packetHandlers; i++) {
							int[] j = content.getValues(i);
							if(j != null) {
								addListener(content, i, j);
							}
						}
					} catch(Exception e) {
						System.out.println("Failed to load content: " + cls);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public final int packetHandlers = 23;

	@SuppressWarnings("unchecked")
	public Map<Integer, ContentTemplate>[] contentMaps = new Map[packetHandlers];

	/**
	 * mapId - packet
	 * 0 - actionButtons
	 * 1 - bury, eat item
	 * 2 - item option 1
	 * 3 - item option 2
	 * 4 - item option 3
	 * 5 - item option 4
	 * 6 - object 1
	 * 7 - object 2
	 * 8 - object 3
	 * 9 - npc attack
	 * 10 - npc 1
	 * 11 - npc 2
	 * 12 - npc 3
	 * 13 - item on item
	 * 14 - item on object
	 * 15 - item on npc
	 * 16 - npc died
	 * 17 - item option 6
	 * 18 - magic on item
	 * 19 - item on object (2) - for faming
	 * 20 - dialogue manager
	 * 21 - item option 5
	 * 22 - item option 7
	 */
	
	public static final int OBJECT_CLICK1 = 6;
	public static final int OBJECT_CLICK2 = 7;

	public void addListener(ContentTemplate cT, int type, int[] ids) {
		for(int i = 0; i < ids.length; i++) {
			contentMaps[type].put(ids[i], cT);
		}
	}
	public boolean handlePacket(int type, Player player, int id) {
		return handlePacket(type, player, id, -1, -1, -1);
	}
	public boolean handlePacket(int type, Player player, int id, int b, int c, int d) {
		if(type > packetHandlers - 1)
			return false;
		ContentTemplate a = (ContentTemplate) contentMaps[type].get(id);
		if(a != null) {
			return a.clickObject(player, type, id, b, c, d);
		} else {
			if(Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
				//dont comment this out arsen.....hi martin u cool
				player.debugMessage("no template exists for: "+id+" type: "+type);
			}
			return false;
		}
	}
}