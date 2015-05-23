package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.shops.PkShop;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Scott Perretta on 4/10/2015.
 */
public class LastManStanding implements ContentTemplate {

    public HashMap<String, Participant> participants = new HashMap<>();

    private ArrayList finishedPlayers = new ArrayList();

    public static Location LOBBY = Location.create(2970, 9678, 0);
    public static Location START = Location.create(3272, 2785, 0);

    public boolean gameStarted = false;
    public boolean canJoin = false;

    public int totalParticipants = 0;

    public static final boolean inLMSArea(int x, int y) {
        return x <= 3274 && y <= 2809 && y >= 2752 && x >= 3205;
    }

    public void startCountdown() {
        World.getWorld().submit(new Event(1000) {
            int counter = 300;
            @Override
            public void execute() {
                if(!canJoin) {
                    super.stop();
                    return;
                }
                if(counter == 0) {
                    stop();
                    return;
                }
                for(NPC npc : World.getWorld().getNPCs()) {
                    if(npc != null)
                        npc.forceMessage("Last Man Standing event starts in "+counter+" seconds! Go to ::lms");
                }
                counter--;
            }

            public void stop() {
                startGame();
                super.stop();
            }
        });
    }

    @Override
    public int[] getValues(int type) {
       /* if (type == 6 || type == 7) {
            int[] j = {2213};
            return j;
        }    */
        return new int[0];
    }

    @Override
    public boolean clickObject(final Player client, final int type, final int oId, final int oX, final int oY, final int a) {
        if (type == 6 || type == 7) {
            if (oId == 2213)
                Bank.open(client, false);
        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
        lastManStanding = this;
    }

    public void invincibleEvent(Participant participant) {
        if (participant == null)
            return;
        participant.getPlayer().getExtraData().put("combatimmunity", System.currentTimeMillis() + 5000L);
        participant.getPlayer().getActionSender().sendMessage("You are now invincible for a short period of time!");
    }

    public static LastManStanding lastManStanding = null;

    public static LastManStanding getLastManStanding() {
        if (lastManStanding == null)
            lastManStanding = new LastManStanding();
        return lastManStanding;
    }

    public void enterLobby(Player player) {
        if (!gameStarted && canJoin) {
            participants.put(player.getName(), new Participant(player, 0, 0));
            Magic.teleport(player, LOBBY, false);
        }
    }

    public void leaveGame(Player player, boolean loseItems) {
        if(participants.remove(player.getName()) != null) {
            Magic.teleport(player, Edgeville.LOCATION, true);
            if (gameStarted) {
                if (loseItems) {
                    player.getPoints().inceasePkPoints(500);
                    DeathDrops.dropItems(player, false);
                }
            }
            if (participants.size() <= 1) {
                endGame();
            }
            player.getActionSender().sendLastManStandingStatus(false);
        }
    }

    public void deathCheck(Player player, Player killer) {
        Participant participant = participants.get(player.getName());
        Participant killerParticipant = participants.get(killer.getName());
        if (participant == null || killerParticipant == null) {
            return;
        }
        participant.addDeaths(1);
        killerParticipant.addKills(1);
        if (participant.getDeaths() == 3) {
            List<Item> items = DeathDrops.dropItems(participant.getPlayer(), false);
            for (Item item : items) {
                if (item == null) {
                    continue;
                }
                int reward = PkShop.getValue(item.getId());
                if (reward == 5000)
                    reward = 0;
                killerParticipant.increaseBountyReward(reward / 4);
            }
            finishedPlayers.add(participant);
            leaveGame(player, true);
            if (participants.size() <= 1) {
                endGame();
            } else if (participants.size() == 2 || participants.size() == 3) {
                participant.getPlayer().getPoints().increasePkPoints(participant.getBountyReward() / 2, true);
            }
            return;
        }
        player.setTeleportTarget(START, false);
        player.getActionSender().sendMessage("You have " + (3 - participant.getDeaths()) + " lives left!");
        invincibleEvent(participant);
    }

    public void startGame() {
        if (participants.size() < 2) {
            for (Participant participant : participants.values()) {
                participant.getPlayer().getActionSender().sendMessage("There must be at least 2 participants in order for this event to start!");
            }
            return;
        }
        gameStarted = true;
        totalParticipants = participants.size();
        for (Participant participant : participants.values()) {
            participant.getPlayer().getActionSender().sendMessage("You have a 20 seconds to find a good location to fight in!");
            Magic.teleport(participant.getPlayer(), START, true);
            invincibleEvent(participant);
        }
    }

    public void endGame() {
        Participant winner = null;
        for (Participant participant : participants.values()) {
            winner = participant;
        }
        gameStarted = false;
        canJoin = false;
        if (winner == null) {
            return;
        }
        int points = winner.getBountyReward() + (500 * totalParticipants);
        winner.getPlayer().getPoints().increasePkPoints(winner.getBountyReward(), false);
        winner.getPlayer().getPoints().increasePkPoints(500 * totalParticipants, false);
        winner.getPlayer().getActionSender().sendMessage("You have won this event and are rewarded " + points + " pk points!");
        Magic.teleport(winner.getPlayer(), Edgeville.LOCATION, true);
        winner.getPlayer().getActionSender().sendLastManStandingStatus(false);
        finishedPlayers.add(winner);
        Collections.sort(finishedPlayers, Collections.reverseOrder());
        for(Player player : World.getWorld().getPlayers()) {
            if(player == null)
                continue;
            if(!player.isInCombat() && !player.getLocation().inPvPArea() && !player.openingTrade) {
                player.getActionSender().sendString(28672, "Top Killers");
                int size = 10;
                if(finishedPlayers.size() < 10)
                    size = finishedPlayers.size();
                for(int i = 0; i < size; i++) {
                    Participant p = (Participant) finishedPlayers.get(i);
                    player.getActionSender().sendString(28685 + i, p.getPlayer().getName() + ": " + p.getKills());
                }
                player.getInterfaceState().interfaceOpened(28671);
            }
        }
        participants.clear();
        finishedPlayers.clear();
    }


}
