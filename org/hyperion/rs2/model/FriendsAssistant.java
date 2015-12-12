package org.hyperion.rs2.model;

import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.net.Packet.Type;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;

public class FriendsAssistant {

    public static int lastChatId = 1;

    public static void initialize(final Player p) {
        for(final long friend : p.getFriends().toArray()){
            if(friend != 0)
                updateList(p, friend);
        }
        refreshGlobalList(p, false);
        sendStatus(p, 2);
    }

    public static void refreshGlobalList(final Player p, final boolean offline) {//login method, send all players online for everyone else not u
        for(final Player c : World.getWorld().getPlayers()){
            if(c == null || c == p)
                continue;
            if(c.getFriends().contains(p.getNameAsLong()) && !isIgnore(p, c.getNameAsLong())){
                if((p.chatStatus[1] == 1 && !p.getFriends().contains(c.getNameAsLong())) || offline || p.isHidden() || c.chatStatus[1] == 2){
                    sendPlayerOnline(c, p.getNameAsLong(), 0);
                    continue;
                }
                sendPlayerOnline(c, p.getNameAsLong(), 10);
            }
        }
    }

    public static void sendPlayerOnline(final Player p, final long playerOn, final int world) {
        p.write(new PacketBuilder(50).putLong(playerOn).put((byte) world).toPacket());
    }

    public static void sendPm(final Player p, final long to, final byte[] chatText, final int chatTextSize) {
        if(p == null)
            return;
        for(final Player c : World.getWorld().getPlayers()){
            if(c == null)
                continue;
            if(c.getNameAsLong() == to){
                try{
                    sendPM(c, p.getNameAsLong(), chatText, chatTextSize, (int) Rank.getPrimaryRankIndex(p));
                }catch(final Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendPM(final Player p, final long from, final byte[] chatText, final int chatTextSize, final int rights) {
        if(lastChatId == 10000){
            lastChatId = 1;
        }
        final String text = TextUtils.pmText(chatTextSize, chatText);
        final String fromName = NameUtils.longToName(from);
        final Player fromPlayer = World.getWorld().getPlayer(fromName);
        if(fromPlayer == null)
            return;
        fromPlayer.getLogManager().add(LogEntry.privateChat(fromName, p.getName(), text));
        p.getLogManager().add(LogEntry.privateChat(fromName, p.getName(), text));
        p.write(new PacketBuilder(196, Type.VARIABLE).putLong(from).putInt(lastChatId++).put((byte) rights).put(chatText, 0, chatTextSize).toPacket());
    }

    public static void updateList(final Player p, final long friend) {
        for(final Player c : World.getWorld().getPlayers()){
            if(c == null)
                continue;
            if(c.getNameAsLong() == friend && c.chatStatus[1] != 2 && (c.getFriends().contains(p.getNameAsLong()) || c.chatStatus[1] == 0) && !isIgnore(c, p.getNameAsLong())){
                //sure there online send the packet
                if(!p.isHidden())
                    sendPlayerOnline(p, c.getNameAsLong(), 10);
                else
                    sendPlayerOnline(p, c.getNameAsLong(), 0);
                return;
            }
        }
        sendPlayerOnline(p, friend, 0);
    }

    public static void addFriend(final Player p, final long friend) {
        p.getFriends().add(friend);
        updateList(p, friend);
        refreshGlobalList(p, false);
    }

    public static void removeFriend(final Player p, final long friend) {
        p.getFriends().remove(friend);
        refreshGlobalList(p, false);
    }


    public static void addIgnore(final Player p, final long friend) {
        p.ignores.add(friend);
        updateList(p, friend);
        refreshGlobalList(p, false);
    }

    public static void removeIgnore(final Player p, final long friend) {
        p.ignores.remove(friend);
        updateList(p, friend);
        refreshGlobalList(p, false);
    }


    public static void sendStatus(final Player p, final int status) {
        p.write(new PacketBuilder(221).put((byte) status).toPacket());
    }


    public static boolean isIgnore(final Player p, final long ingore) {
        for(final long ingore2 : p.ignores){
            if(ingore == ingore2)
                return true;
        }
        return false;
    }


}