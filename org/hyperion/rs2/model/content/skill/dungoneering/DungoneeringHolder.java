package org.hyperion.rs2.model.content.skill.dungoneering;

import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.DungeonDifficulty;
import org.hyperion.rs2.model.InterfaceState;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.content.skill.dungoneering.reward.RingPerks;
import org.hyperion.rs2.net.ActionSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 2/20/15
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class DungoneeringHolder {

    public final RingPerks perks = new RingPerks();
    private final int[] combatXPs = new int[7];
    private final Item[] bound = new Item[5];
    private int dungoneeringPoints;
    private Dungeon currentDungeon;
    private Room room;

    public DungoneeringHolder() {
        Arrays.fill(combatXPs, Skills.EXPERIENCE_PER_LEVEL[50] + 5);
    }

    public static void main(final String[] strings) {
        final DungoneeringHolder holder = new DungoneeringHolder();
        holder.bind(Item.create(4151, 1), 0);
        holder.bind(Item.create(8856, 1), 0);

        holder.combatXPs[0] = 1300000;
        holder.combatXPs[6] = 1300500;

        System.out.println(holder.save());

        final DungoneeringHolder h2 = new DungoneeringHolder();

        h2.load(holder.save());

        System.out.println(h2.save());
    }

    public void fireOnLogout(final Player player) {
        if(inDungeon()){
            currentDungeon.remove(player, false);
        }
    }

    public void start(final List<Player> players, final DungeonDifficulty chosen, final DungeonDifficulty.DungeonSize size) {
        final Iterator<Player> it = players.iterator();
        final List<Player> copy = new ArrayList<>();
        while(it.hasNext()){
            final Player p = it.next();
            if(DungeoneeringManager.cantJoin(p) || !p.getLocation().inDungeonLobby() || p.getSkills().getLevel(Skills.DUNGEONEERING) < chosen.min_level){
                p.sendMessage("You didn't meet the requirements to join and have been kicked out!");
                copy.add(p);
            }
        }

        players.removeAll(copy);
        if(players.size() == 0)
            return;
        players.forEach(Trade::declineTrade);
        players.forEach(p -> p.getDungeoneering().loadXP(p.getSkills(), true));
        final Dungeon dungeon = new Dungeon(players, chosen, size);
        dungeon.start();
        players.forEach(p -> p.getDungeoneering().setCurrentDungeon(dungeon));
    }

    public void bind(final Item item, final int slot) {
        bound[slot] = item;
    }

    public void showBindDialogue(final ActionSender actionSender, final InterfaceState state) {
        final String[] strings = Stream.of(bound).map(item -> (item == null ? "Free Slot" : item.getDefinition().getName())).toArray(String[]::new);
        actionSender.sendDialogue("Bind", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT, strings);
        for(int i = 0; i < 5; i++){
            state.setNextDialogueId(i, 7006 + i);
        }
    }

    public Location clickPortal() {
        if(room == null)
            return null;
        if(!room.cleared())
            return null;
        if(room.boss){
            currentDungeon.complete();
            return null;
        }
        final Location location = room.getChild().getSpawnLocation();
        setCurrentRoom(room.getChild());
        return location;
    }

    public Location clickBackPortal() {
        if(room == null)
            return null;
        if(room.getParent() == null)
            return null;
        final Location location = room.getParent().getEndLocation();
        setCurrentRoom(room.getParent());
        return location;
    }

    public void loadXP(final Skills skills, final boolean toSkill) {
        for(int i = 0; i < combatXPs.length; i++){
            if(toSkill){
                skills.setExperience(i, combatXPs[i]);
                skills.setLevel(i, skills.getLevelForExp(i));
            }else
                combatXPs[i] = skills.getExperience(i);

        }
    }

    public boolean buyPerk(final int style) {
        final int price = perks.calcNextPerkCost(style);
        if(dungoneeringPoints < price)
            return false;
        perks.upgradePerk(RingPerks.Perk.forStyle(style));
        dungoneeringPoints -= price;
        return true;
    }

    public void setCurrentRoom(final Room room) {
        this.room = room;
    }

    public boolean inDungeon() {
        return currentDungeon != null;
    }

    public Dungeon getCurrentDungeon() {
        return currentDungeon;
    }

    public void setCurrentDungeon(final Dungeon dungeon) {
        this.currentDungeon = dungeon;
    }

    public Room getRoom() {
        return room;
    }

    public Item[] getBinds() {
        return Arrays.stream(bound).filter(Objects::nonNull).toArray(Item[]::new);
    }

    public int getTokens() {
        return dungoneeringPoints;
    }

    public void setTokens(final int tokens) {
        this.dungoneeringPoints = tokens;
    }


    /**
     * ***********************
     * START OF SAVING
     *************************/

    public String save() {
        final StringBuilder builder = new StringBuilder(dungoneeringPoints + "%");
        for(final Item item : bound){
            if(item != null)
                builder.append(item.getId()).append(",").append(item.getCount()).append(" ");
            else
                builder.append("-1").append(",").append("0").append(" ");
        }
        builder.append("%");
        for(int i = 0; i < combatXPs.length; i++){
            builder.append(combatXPs[i]).append(",");
        }
        builder.append("%").append(perks.perkLevel());
        return builder.toString();
    }

    public void load(final String read) {
        if(read.length() < 2){
            return;
        }
        final String[] split = read.split("%");
        this.dungoneeringPoints = Integer.parseInt(split[0]);
        final String[] items = split[1].split(" ");
        try{
            for(int i = 0; i < items.length; i++){
                final String[] id_count = items[i].split(",");
                bind(Item.create(Integer.valueOf(id_count[0]), Integer.valueOf(id_count[1].trim())), i);
            }
        }catch(final Exception ex){
            ex.printStackTrace();
        }
        final String[] xps = split[2].split(",");
        try{
            for(int i = 0; i < xps.length; i++){
                combatXPs[i] = Integer.parseInt(xps[i]);
            }
        }catch(final Exception ex){
            ex.printStackTrace();
        }
        perks.setPerk(Integer.parseInt(split[3]));

    }

}
