package org.hyperion.rs2.model.content.clan;

import org.apache.mina.core.buffer.IoBuffer;
import org.hyperion.Server;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc2.Dicing;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ClanManager {

    private static final byte KEY = (byte) 245;
    public static Map<String, Clan> clans = new HashMap<String, Clan>();

    public static void joinClanChat(final Player player, String clanName, final boolean onLogin) {
        if(Server.OLD_SCHOOL)
            return;
        clanName = clanName.replace("_", " ");
        if(!canEnter(player, clanName))
            return;
        Clan clan = clans.get(clanName.toLowerCase());
        if(clan == null){
            clan = new Clan(player.getName(), clanName);
            clans.put(clanName.toLowerCase(), clan);
        }
        if(clan.isKicked(player.getName())){
            player.sendClanMessage("You are currently kicked from this clan chat.");
            return;
        }
        leaveChat(player, true, false);
        if(!onLogin){
            sendLoginMessage(player, clanName);
        }
        if(clan.isFull() && !Rank.hasAbility(player, Rank.ADMINISTRATOR) && !clanName.equalsIgnoreCase(player.getName())){
            player.sendClanMessage("This clan chat is full.");
            return;
        }
        checkClanRank(player, clan);
        clan.add(player);
        player.getActionSender().sendClanInfo();
        updateClanInfo(player, clan);

    }

    private static void updateClanInfo(final Player player, final Clan clan) {
        player.getActionSender().sendString(18139, "Talking in: " + TextUtils.titleCase(clan.getName()));
        player.getActionSender().sendString(18140, "Owner: " + TextUtils.ucFirst(clan.getOwner()));
        for(final Player p : clan.getPlayers()){
            player.getActionSender().addClanMember(p.getPlayersNameInClan());
            if(p != player)
                p.getActionSender().addClanMember(player.getPlayersNameInClan());
        }
    }

    private static void checkClanRank(final Player player, final Clan clan) {
        for(final ClanMember cm : clan.getRankedMembers()){
            if(cm.getName().equalsIgnoreCase(player.getName())){
                player.setClanRank(cm.getRank());
                break;
            }
        }

        if(Rank.hasAbility(player, Rank.DEVELOPER))
            player.setClanRank(7);
        else if(Rank.hasAbility(player, Rank.MODERATOR))
            player.setClanRank(6);
        if(player.getName().equalsIgnoreCase(clan.getOwner())){
            player.setClanRank(5);
        }
    }

    private static void sendLoginMessage(final Player player, final String clanName) {
        player.sendClanMessage("Talking in clan '" + clanName + "'.");
    }

    public static boolean existsClan(final String name) {
        return clans.get(name.toLowerCase()) != null;
    }

    public static void joinClanChat(final Player player, final long name) {
        final String clanName;
        try{
            clanName = NameUtils.longToName(name);
        }catch(final Exception e){
            return;
        }
        joinClanChat(player, clanName, false);
    }

	/*public static void leaveChat(Player player, boolean resetClanName, boolean keepRank) {
        Clan c = clans.get(player.getClanName());
		if(c != null) {
			for(Player p : c.getPlayers()) {
		        p.getActionSender().removeClanMember(player.getPlayersNameInClan());
			}
			player.setClanRank(0);
			c.remove(player);
			player.sendClanMessage("You leave your current clan chat.");

		}
        player.getActionSender().sendString(18139, "Talking in: -");
        player.getActionSender().sendString(18140, "Owner: None");
		if(! keepRank)
			player.setClanRank(0);
		if(resetClanName)
			player.resetClanName();
	}*/

    public static boolean canEnter(final Player player, final String nameStr) {
        if(player.getClanName().equalsIgnoreCase(nameStr)){
            player.sendClanMessage("You are already in this clan chat.");
            return false;
        }
        if((!FightPits.teamBlue.contains(player) && nameStr.equalsIgnoreCase("Team Blue")) || (!FightPits.teamRed.contains(player) && nameStr.equalsIgnoreCase("Team Red")))
            return false;
        if(nameStr.equalsIgnoreCase("staff") && !Rank.hasAbility(player, Rank.MODERATOR)){
            player.sendClanMessage("Only staff can join this clan chat.");
            return false;
        }
        return true;
    }

    public static void leaveChat(final Player player, final boolean resetClanName, final boolean keepRank) {
        final Clan c = clans.get(player.getClanName());
        if(c != null){
            c.remove(player);
            player.getActionSender().sendMessage("You left your current clan chat.");
            for(final Player p : c.getPlayers()){
                p.getActionSender().removeClanMember(player.getPlayersNameInClan());
            }
        }
        clearClanChat(player);
        if(!keepRank)
            player.setClanRank(0);
        if(resetClanName)
            player.resetClanName();
    }

    public static void clearClanChat(final Player player) {
        player.getActionSender().sendString(18139, "Talking in: Not in clan");
        player.getActionSender().sendString(18140, "Owner: None");

        for(int i = 18144; i <= 18444; i++){
            player.getActionSender().sendString(i, "");
        }
    }

    public static void sendClanMessage(final Player player, String message, final boolean toMe) {
        // message = message+":clan:";

        if(handleInternalCommands(message, player))
            return;

        message = message.replace("req:", "req");

        String displayRank = player.getClanRankName().isEmpty() ? " " : "[" + player.getClanRank() + "] ";
        if(player.isClanMainOwner())
            displayRank = "[O] ";
        //message = "[@blu@" + TextUtils.titleCase(player.getClanName()) + "@bla@] " + displayRank + player.getSafeDisplayName() + ": @dre@" + TextUtils.ucFirst(message.toLowerCase().replaceAll("@", ""));
        player.getLogManager().add(LogEntry.clanchat(player.getClanName(), message));
        message = "" + displayRank + player.getSafeDisplayName() + ":" + TextUtils.ucFirst(message.replaceAll("@", "") + ":clan:");
        if(Objects.equals(player.getClanName(), "")){
            player.sendClanMessage("You need to join a clan chat before you can send messages.");
            return;
        }

        final Clan clan = clans.get(player.getClanName());
        if(clan == null)
            return;
        for(final Player client : clan.getPlayers()){
            client.getActionSender().sendMessage(message);
        }

    }

    public static void sendDiceMessage(final Player player, final Clan clan, final int thrown) {
        String message = "You roll @red@" + thrown + "@bla@ on the percentile dice.";
        player.getActionSender().sendMessage(message);
        message = "Clan Chat mate @369@" + player.getName() + "@bla@ rolled @red@" + thrown + "@bla@ on the percentile dice.";
        for(final Player client : clan.getPlayers()){
            if(client.getName().equals(player.getName()))
                continue;
            client.getActionSender().sendMessage(message);
        }
        player.forceMessage("ROLLED: " + thrown + "!");
    }

    public static boolean handleCommands(final Player player, String s, final String[] as) {
        final String s1 = as[0].toLowerCase();
        //System.out.println(player.getClanName());
        if(s.startsWith("cc ")){
            ClanManager.sendClanMessage(player, s.replace("cc ", ""), true);
            return true;
        }

        if(s1.equalsIgnoreCase("changeclanowner") && Rank.hasAbility(player, Rank.ADMINISTRATOR)){
            try{
                s = s.replace("changeclanowner ", "");
                final String clanName = s.substring(0, s.indexOf(":"));
                final String owner = s.substring(s.indexOf(":") + 1, s.length());
                final Clan clan = clans.get(clanName);
                if(clan != null){
                    player.sendClanMessage("Changing " + clan.getName() + " owner from " + clan.getOwner() + " to " + owner);
                    clan.setOwner(owner);
                }
            }catch(final Exception ex){
                player.sendMessage("Use as ::changeclanowner clanName:newOwnerName");
            }


        }

        if(Rank.hasAbility(player, Rank.OWNER)){
            if(s1.equalsIgnoreCase("enabledicing")){
                Dicing.canDice = true;
            }
            if(s1.equalsIgnoreCase("disabledicing"))
                Dicing.canDice = false;
        }

        if(s1.equalsIgnoreCase("givedice") && Rank.hasAbility(player, Rank.GLOBAL_MODERATOR)){
            final String targ = s.substring(8).trim();
            final Player target = World.getWorld().getPlayer(targ);
            if(target != null){
                target.getInventory().add(Item.create(15098));
            }
        }

        return false;
    }

    public static boolean handleInternalCommands(final String message, final Player player) {

        if(message.equalsIgnoreCase("makediceclan") && Rank.hasAbility(player, Rank.OWNER)){
            final Clan clan = clans.get(player.getClanName());
            clan.makeDiceClan();
            player.sendf("Dice clan: %s", clan.isDiceClan());
            return true;
        }
        if(message.startsWith("demote")){
            final String name = message.replace("demote ", "");
            player.getActionSender().sendMessage("Promoting " + name);
            final Clan clan = ClanManager.clans.get(player.getClanName());
            if(!clan.getOwner().equalsIgnoreCase(player.getName())){
                player.sendClanMessage("Only the main owner can demote people.");
                return true;
            }
            final Player p = World.getWorld().getPlayer(name);
            if(p == null){
                player.getActionSender().sendMessage("This player is offline");
                return true;
            }
            if(!player.getClanName().equals(p.getClanName())){
                player.sendClanMessage("This player is not in your clan chat");
                return true;
            }
            final String clanName = p.getClanName();
            final int old = p.getClanRank();
            ClanManager.leaveChat(p, true, true);
            if(old > 0){
                clan.addRankedMember(new ClanMember(p.getName(), 0));
                sendClanMessage(player, "@bla@ " + name + " has been demoted", true);
            }else{
                player.getActionSender().sendMessage("This player already has the lowest");
                ClanManager.joinClanChat(p, clanName, false);
                return true;
            }
            ClanManager.joinClanChat(p, clanName, false);
            player.sendClanMessage("Player has been succesfully demoted.");
            return true;
        }

        if(message.startsWith("promote")){
            final String name = message.replace("promote ", "");
            player.getActionSender().sendMessage("Promoting " + name);
            final Clan clan = ClanManager.clans.get(player.getClanName());
            if(!player.isClanMainOwner() && player.getClanRank() != 7){
                player.sendClanMessage("Only clan chat owners are able to give ranks.");
                return true;
            }
            final Player p = World.getWorld().getPlayer(name);
            if(p == null){
                player.sendClanMessage("This player is offline");
                return true;
            }
            if(!player.getClanName().equals(p.getClanName())){
                player.sendClanMessage("This player is not in your clan chat");
                return true;
            }
            final String clanName = p.getClanName();
            final int old = p.getClanRank();
            if(old < 5){
                if(Dicing.diceClans.contains(clanName) && old >= 3){
                    player.sendClanMessage("This player has the maximum rank for a dice clan");
                    return true;
                }
                ClanManager.leaveChat(p, true, true);
                p.setClanRank(old + 1);
                clan.addRankedMember(new ClanMember(p.getName(), p.getClanRank()));
                sendClanMessage(player, "@bla@ " + TextUtils.titleCase(name) + " has been promoted to " + p.getClanRankName(), true);
            }else{
                player.sendClanMessage("This player already has the highest rank possible");
                return true;
            }
            ClanManager.joinClanChat(p, clanName, false);
            player.sendClanMessage("Player has been succesfully promoted.");
            return true;
        }

        if(message.startsWith("ban")){
            final String name = message.replace("ban ", "");
            final Clan clan = ClanManager.clans.get(player.getClanName());
            final Player other = World.getWorld().getPlayer(name);
            if(player.getClanRank() < 4){
                player.sendClanMessage("You are not a high enough rank to ban members");
                return true;
            }
            if(other != null && other.getClanRank() > player.getClanRank()){
                player.sendClanMessage("You cannot do this with someone of a higher rank");
                return true;
            }
            if(clan.kick(name, false)){
                player.sendClanMessage("Player has been kicked succesfully");
                sendClanMessage(player, "@bla@ " + name + " has been KICKED from the channel", true);
            }
            return true;
        }

        if(message.startsWith("ipban")){
            final String name = message.replace("ipban ", "");
            final Clan clan = ClanManager.clans.get(player.getClanName());
            final Player other = World.getWorld().getPlayer(name);
            if(player.getClanRank() < 4){
                player.sendClanMessage("You are not a high enough rank to ipban members");
                return true;
            }
            if(other != null && other.getClanRank() > player.getClanRank()){
                player.sendClanMessage("You cannot do this with someone of a higher rank");
                return true;
            }
            if(clan.kick(name, true)){
                player.sendClanMessage("Player has been kicked succesfully");
                sendClanMessage(player, "@bla@ " + name + " has been IP-BANNED from the channel", true);
            }
            return true;
        }


        if(message.startsWith("unban")){
            final String name = message.replace("unban ", "");
            final Clan clan = ClanManager.clans.get(player.getClanName());
            if(player.getClanRank() < 3){
                player.sendClanMessage("You need to be a higher rank to unban");
                return true;
            }

            if(clan.unban(name)){
                sendClanMessage(player, "@bla@ " + name + " has been unbanned from the channel", true);
            }
            return true;
        }

        if(message.startsWith("listbans") && Rank.isStaffMember(player)){
            final String name = message.replace("unban ", "");
            final Clan clan = ClanManager.clans.get(player.getClanName());

            clan.listBans(player);

            return true;
        }
        return false;
    }

    public static void save() {
        try{
            final OutputStream os = new FileOutputStream("data/clanData.bin");
            final IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            for(final Clan clan : clans.values())
                if(!clan.getName().toLowerCase().startsWith("party "))
                    clan.save(buf);
            buf.flip();
            final byte[] data = new byte[buf.limit()];
            buf.get(data);
            os.write(data);
            os.flush();
            os.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public static void load() {

        try{
            final File f = new File("./data/clanData.bin");
            final InputStream is = new FileInputStream(f);
            final IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            while(true){
                final byte[] temp = new byte[1024];
                final int read = is.read(temp, 0, temp.length);
                if(read == -1){
                    break;
                }else{
                    buf.put(temp, 0, read);
                }
            }
            buf.flip();
            while(buf.hasRemaining()){
                try{
                    final Clan clan = Clan.read(buf);
                    clans.put(clan.getName().toLowerCase(), clan);
                }catch(final Exception ex){

                }
            }
        }catch(final Exception ex){

        }

        System.out.println("Loaded " + clans.size() + " clans");
    }

}
