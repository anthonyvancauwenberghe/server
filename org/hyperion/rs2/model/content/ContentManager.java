package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.minigame.ZombieMinigame;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.util.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class ContentManager {

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
    public final int packetHandlers = 23;
    public ContentTemplate prayer = null;//key classes like this may be nessary for skills like this
    @SuppressWarnings("unchecked")
    public Map<Integer, ContentTemplate>[] contentMaps = new Map[packetHandlers];
    private ZombieMinigame zombieMinigame;

    public ZombieMinigame getZombieMinigame() {
        return zombieMinigame;
    }

    public void init() {
        try{
            for(int i = 0; i < packetHandlers; i++){
                contentMaps[i] = null;
                contentMaps[i] = new HashMap<Integer, ContentTemplate>();
            }
            addContent();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public void addContent() {
        try{
            final Class[] classes = ClassUtils.getClasses("org.hyperion.rs2.model.content");
            for(final Class cls : classes){
                if(ContentTemplate.class.isAssignableFrom(cls) && cls != ContentTemplate.class && !cls.isEnum()){
                    try{
                        final ContentTemplate content = (ContentTemplate) cls.newInstance();
                        if(Interface.class.isAssignableFrom(cls)){
                            InterfaceManager.addGlobal((Interface) content);
                        }
                        if(content instanceof SpecialArea || SpecialArea.class.isAssignableFrom(cls)){
                            SpecialAreaHolder.put(cls.getSimpleName(), (SpecialArea) content, true);
                            System.err.println("HIT " + cls.getSimpleName() + " TO ADD CONTENT SPECIAL AREA");
                        }
                        if(cls.getName().contains("prayer"))
                            prayer = content;
                        if(cls.getName().contains("zombieminigame"))
                            zombieMinigame = (ZombieMinigame) content;

                        content.init();
                        for(int i = 0; i < packetHandlers; i++){
                            final int[] j = content.getValues(i);
                            if(j != null){
                                addListener(content, i, j);
                            }
                        }
                    }catch(final Exception e){
                        System.out.println("Failed to load content: " + cls);
                        e.printStackTrace();

                    }
                }
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public void addListener(final ContentTemplate cT, final int type, final int[] ids) {
        for(int i = 0; i < ids.length; i++){
            contentMaps[type].put(ids[i], cT);
        }
    }

    public boolean handlePacket(final int type, final Player player, final int id) {
        return handlePacket(type, player, id, -1, -1, -1);
    }

    public boolean handlePacket(final int type, final Player player, final int id, final int b, final int c, final int d) {
        if(type > packetHandlers - 1)
            return false;
        final ContentTemplate a = (ContentTemplate) contentMaps[type].get(id);
        if(a != null){
            return a.clickObject(player, type, id, b, c, d);
        }else{
            if(Rank.hasAbility(player, Rank.ADMINISTRATOR)){
                player.debugMessage("no template exists for: " + id + " type: " + type);
            }
            return false;
        }
    }
}