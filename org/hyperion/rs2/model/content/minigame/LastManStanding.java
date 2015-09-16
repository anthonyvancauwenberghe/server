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

    private List<Participant> finishedPlayers = new ArrayList<>();
    private static List<Participant> topTenPlayers = new ArrayList<>();

    public static Location START = Location.create(3272, 2785, 0);
    public static Location START_2 = Location.create(3264, 2784, 0);

    public boolean gameStarted = false;
    public boolean canJoin = false;

    public static int counter;

    public int getCounter() {
        return counter;
    }

    public int totalParticipants = 0;

    public static final boolean inLMSArea(int x, int y) {
        return x <= 3274 && y <= 2809 && y >= 2752 && x >= 3205;
    }

    private static void startCountdown() {
        counter = 120;
        World.getWorld().submit(new Event(1000) {
            @Override
            public void execute() {
                if(!getLastManStanding().canJoin) {
                    super.stop();
                    return;
                }
                if(counter == 0) {
                    stop();
                    return;
                }
                if(counter % 30 == 0) {
                    getLastManStanding().participants.forEach((s, participant) -> participant.getPlayer().getActionSender().sendMessage("Last Man Standing starts in " + counter + " seconds!"));
                }
                counter--;
            }

            public void stop() {
                getLastManStanding().startGame();
                counter = 0;
                getLastManStanding().participants.forEach((s, participant) -> participant.getPlayer().getActionSender().sendMessage("Last Man Standing has begun! Attack anyone starting now!"));
                super.stop();
            }
        });
    }

    @Override
    public int[] getValues(int type) {
        if (type == 6 || type == 7) {
            int[] j = {2213};
            return j;
        }
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

    public void invincibleEvent(Participant participant, long time) {
        if (participant == null)
            return;
        participant.getPlayer().getExtraData().put("combatimmunity", System.currentTimeMillis() + time);
        participant.getPlayer().getActionSender().sendMessage("You are now invincible for " + time / 1000 + " seconds, until the event starts!");
    }

    public void invincibleEvent(Participant participant) {
        invincibleEvent(participant, 20000);
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
            invincibleEvent(participants.get(player.getName()), (counter * 1000));
        } else {
            player.getActionSender().sendMessage("You cannot join as the event has started.");
        }
    }

    public static void startLMS() {
        if(LastManStanding.getLastManStanding().canJoin)
            return;
        LastManStanding.getLastManStanding().canJoin = true;
        startCountdown();
    }

    public void leaveGame(Player player, boolean loseItems) {
        if(participants.remove(player.getName()) != null) {
            player.setTeleportTarget(Edgeville.LOCATION, false);
            if (gameStarted) {
                if (loseItems) {
                    player.getPoints().increasePkPoints(500);
                    DeathDrops.dropItems(player, false);
                }
                if (participants.size() <= 1) {
                    endGame();
                }
            }
            player.getActionSender().sendWildLevel(-1);
        }
    }

    public void deathCheck(Player player, Player killer) {
        if(participants == null || player == null || killer == null)
            return;
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
                participant.getPlayer().getActionSender().sendMessage("This event is canceled because there were less than 2 participants.");
                leaveGame(participant.getPlayer(), false);
            }
            canJoin = false;
            participants.clear();
            return;
        }
        canJoin = false;
        gameStarted = true;
        totalParticipants = participants.size();
    }

    public void endGame() {
        Participant winner = null;
        for (Participant participant : participants.values()) {
            winner = participant;
        }
        gameStarted = false;
        canJoin = false;
        if (winner != null) {
            int points = winner.getBountyReward() + (500 * totalParticipants);
            winner.getPlayer().getPoints().increasePkPoints(winner.getBountyReward(), false);
            winner.getPlayer().getPoints().increasePkPoints(500 * totalParticipants, false);
            winner.getPlayer().getActionSender().sendMessage("You have won this event and are rewarded " + points + " pk points!");
            winner.getPlayer().setTeleportTarget(Edgeville.LOCATION, false);
            winner.getPlayer().getActionSender().sendWildLevel(-1);
            finishedPlayers.add(winner);
            Collections.sort(finishedPlayers, Collections.reverseOrder());
            int size = 10;
            if (finishedPlayers.size() < size)
                size = finishedPlayers.size();
            for (int i = 0; i < size; i++) {
                Participant p = finishedPlayers.get(i);
                topTenPlayers.add(p);
                System.out.println(topTenPlayers.size());
            }
            World.getWorld().getPlayers().forEach(player -> player.sendImportantMessage("LMS event has ended. Do ::top10 to look at the top 10 players."));
        }
        participants.clear();
        finishedPlayers.clear();
    }

    public static void loadTopTenInterface(Player player) {
        if(topTenPlayers.size() <= 0) {
            System.out.println(topTenPlayers.size());
            return;
        }
        player.getActionSender().sendString(28672, "Top Killers");
        for (int i = 0; i < topTenPlayers.size(); i++) {
            Participant p = topTenPlayers.get(i);
            player.getActionSender().sendString(28685 + i, p.getPlayer().getSafeDisplayName()+ ": " + p.getKills());
        }
        player.getActionSender().showInterface(28670);
    }


}
