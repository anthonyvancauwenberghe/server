package org.hyperion.rs2.sqlv2.vote.work;

import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.sqlv2.DbHub;
import org.hyperion.rs2.sqlv2.db.DbConfig;
import org.hyperion.rs2.sqlv2.vote.VoteDao;
import org.hyperion.rs2.sqlv2.vote.WaitingVote;
import org.hyperion.rs2.task.Task;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CheckWaitingVotesTask implements Task {

    public static final SimpleDateFormat FORMAT_PLAYER = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat FORMAT_SQL = new SimpleDateFormat("yyyy-MM-dd");

    private final Player player;

    private int bonus = -1;
    private int votingPoints = 0;
    private int streak;

    public CheckWaitingVotesTask(final Player player) {
        this.player = player;
    }

    private String doBonus() {
        switch(bonus){
            case 0:
                votingPoints *= 2;
                return ("You get double voting reward!");
            case 1:
                long time;
                switch(streak){
                    case 10:
                        time = 2 * Time.ONE_HOUR;
                        break;
                    case 5:
                        time = 90 * Time.ONE_MINUTE;
                        break;
                    case 3:
                        time = Time.ONE_HOUR;
                        break;
                    case 2:
                        time = 45 * Time.ONE_MINUTE;
                        break;
                    case 1:
                        time = 30 * Time.ONE_MINUTE;
                        break;
                    default:
                        time = 15 * Time.ONE_MINUTE;
                        break;
                }
                player.getPermExtraData().put("doubleExperience", System.currentTimeMillis() + time);
                return "You received double experience for " + time / Time.ONE_MINUTE + " minutes!";
            case 2:
                double multiplier;
                switch(streak){
                    case 10:
                        multiplier = 1.5;
                        break;
                    case 5:
                        multiplier = 1.2;
                        break;
                    case 3:
                        multiplier = 1.1;
                        break;
                    case 2:
                        multiplier = 1.05;
                        break;
                    case 1:
                        multiplier = 1.02;
                        break;
                    default:
                        multiplier = 1.01;
                        break;
                }
                player.getPermExtraData().put("increasedDroprate", System.currentTimeMillis() + Time.ONE_HOUR);
                player.getPermExtraData().put("dropRateMultiplier", multiplier);
                return "You received increased droprates for one hour!";
            case 3:
                double reducement;
                switch(streak){
                    case 10:
                        reducement = 0.5;
                        break;
                    case 5:
                        reducement = 0.8;
                        break;
                    case 3:
                        reducement = 0.9;
                        break;
                    case 2:
                        reducement = 0.95;
                        break;
                    case 1:
                        reducement = 0.98;
                        break;
                    default:
                        reducement = 0.99;
                        break;
                }
                player.getPermExtraData().put("loweredYellTimer", System.currentTimeMillis() + Time.ONE_HOUR);
                player.getPermExtraData().put("yellReduction", reducement);
                return "You received a reduced yelldelay for one hour!";
            case 4:
                if(Misc.random(120 / streak) == 1){
                    final int donatorPoints = 1000;
                    player.getPoints().setDonatorPoints(player.getPoints().getDonatorPoints() + donatorPoints);
                    for(final Player p : World.getWorld().getPlayers()){
                        p.sendServerMessage(player.getSafeDisplayName() + " has just received " + donatorPoints + " donator points for voting!");
                    }
                    return "You receive " + donatorPoints + " donator points as a rare bonus!";
                }else{
                    bonus = Misc.random(4);
                    doBonus();
                }
                break;
        }
        return "";
    }

    @Override
    public void execute(final GameEngine context) {
        List<WaitingVote> votes = null;
        if(DbHub.initialized() && DbHub.donationsDb().enabled())
            votes = DbHub.donationsDb().votes().waiting(player);
        if(votes == null){
            if(DbConfig.playerDebug)
                player.sendf("Unable to retrieve voting information at this time. Try again later.");
            return;
        }
        if(votes.isEmpty()){
            player.sendf("You don't have any votes! Type ::vote to start voting");
            return;
        }
        int currentStreak = player.getPermExtraData().getInt("votingStreak");
        boolean runelocus = false;
        boolean rspslist = false;
        boolean topg = false;
        int runelocusVotes = 0;
        int rspslistVotes = 0;
        int topgVotes = 0;
        try(final VoteDao dao = DbHub.donationsDb().votes().open()){
            for(final WaitingVote vote : votes){
                if(!vote.processed()){
                    if(vote.runelocus() && !vote.runelocusProcessed()){
                        runelocusVotes += 2;
                        DbHub.donationsDb().votes().processRunelocus(dao, vote);
                    }
                    if(vote.topg() && !vote.topgProcessed()){
                        topgVotes++;
                        DbHub.donationsDb().votes().processTopg(dao, vote);
                    }
                    if(vote.rspslist() && !vote.rspslistProcessed()){
                        rspslistVotes++;
                        DbHub.donationsDb().votes().processRspslist(dao, vote);
                    }
                    DbHub.donationsDb().votes().process(dao, vote);
                }
                if(vote.date().toString().equalsIgnoreCase(FORMAT_SQL.format(Calendar.getInstance().getTime()).toString())){
                    if(vote.runelocus())
                        runelocus = true;
                    if(vote.rspslist())
                        rspslist = true;
                }else{
                    DbHub.donationsDb().votes().delete(dao, vote);
                }
            }
            if(runelocusVotes == 0 && rspslistVotes == 0 && topgVotes == 0){
                player.sendf("You have no votes to claim. Type ::vote to vote");
                return;
            }
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            final String yesterday = FORMAT_PLAYER.format(cal.getTime());
            final String lastVoted = player.getPermExtraData().getString("lastVoted");
            if(lastVoted != null){
                if(lastVoted.equalsIgnoreCase(yesterday)){
                    currentStreak++;
                    //On the condition that his last vote was not today it'll reset. Otherwise it means he already received his bonus today & he doesn't need a bonus anymore
                }else if(!lastVoted.equalsIgnoreCase(FORMAT_PLAYER.format(Calendar.getInstance().getTime()))){
                    player.sendMessage("Your voting streak has been reset!");
                    currentStreak = 0;
                }
            }
            final int todayVotes = player.getPermExtraData().getInt("todayVoted") + runelocusVotes + topgVotes + rspslistVotes;
            player.getPermExtraData().put("todayVoted", todayVotes);
            if(todayVotes > 20 + (Misc.random(10))){
                player.getInventory().remove(Item.create(3062, player.getInventory().getCount(3062)));
                player.getBank().remove(Item.create(3062, player.getBank().getCount(3062)));
                player.getPoints().setVotingPoints(0);
                player.sendMessage("Stop multivoting so heavily!");
                player.getPermExtraData().put("todayVoted", 0);
                return;
            }
            if(runelocus && rspslist && !FORMAT_PLAYER.format(Calendar.getInstance().getTime()).equalsIgnoreCase(lastVoted)){
                player.getPermExtraData().put("lastVoted", FORMAT_PLAYER.format(Calendar.getInstance().getTime()));
                player.getPermExtraData().put("todayVoted", 0);
                player.getAchievementTracker().voted();
                if(currentStreak >= 31){
                    streak = 10;
                }else if(currentStreak >= 14){
                    streak = 5;
                }else if(currentStreak >= 7){
                    streak = 3;
                }else if(currentStreak >= 4){
                    streak = 2;
                }else if(currentStreak >= 2){
                    streak = 1;
                }
                votingPoints += streak;

                //The bonus gets set
                bonus = Misc.random(4);
            }
            votingPoints += runelocusVotes + rspslistVotes + topgVotes;
            final StringBuilder sb = new StringBuilder();
            if(runelocus && topg && rspslist){
                sb.append(doBonus());
            }else{
                //Now all the processing is done, it's time to add the points and tell him if he can still vote for the streak
                sb.append("You can still vote on ");
                if(!runelocus)
                    sb.append("Runelocus & ");
                if(!rspslist)
                    sb.append("RSPSList");
                if(sb.toString().endsWith(" & ")){
                    sb.replace(sb.length() - 3, sb.length(), "");
                }
                sb.append(".");
            }
            if(!runelocus || !rspslist){
                player.sendf("Alert##Thank you for voting!##You received %d voting point(s)##Remember to vote on all 3 sites to %s streak!##%s", votingPoints, currentStreak != 0 ? "keep your" : "get a", sb.toString());
            }else{
                if(bonus == -1){
                    player.sendMessage("Alert##Thank you for voting again!##You received " + votingPoints + " Strange Box" + (votingPoints == 1 ? "" : "es") + " ##ArteroPK appreciates your support!");
                }else{
                    if(currentStreak != 0){
                        player.sendMessage("Alert##Thank you for voting " + currentStreak + " " + (currentStreak == 1 ? "day" : "days") + " in a row.##You received " + votingPoints + " Strange Box" + (votingPoints == 1 ? "" : "es") + ".##" + sb.toString());
                    }else{
                        player.sendMessage("Alert##Thank you for voting.##You received " + votingPoints + " Strange Box" + (votingPoints == 1 ? "" : "es") + ".##" + sb.toString());
                    }
                }
            }
            final int freeSlots = player.getInventory().freeSlots();
            if(freeSlots >= 1){
                player.getInventory().add(new Item(3062, votingPoints));
            }else{
                player.getBank().add(new BankItem(0, 3062, votingPoints));
                player.sendMessage((votingPoints == 1 ? "A" : votingPoints) + " Strange Box" + (votingPoints == 1 ? " has" : "es have") + " been added to your bank.");
            }
            player.setLastVoted(System.currentTimeMillis());
            player.getPermExtraData().put("votingStreak", currentStreak);
            votingPoints = 0;
            streak = 0;
            bonus = -1;
            DbHub.donationsDb().votes().insertVote(dao, player, runelocusVotes, rspslistVotes, topgVotes);
        }catch(Exception ex){
            if(DbConfig.consoleDebug)
                ex.printStackTrace();
            if(DbConfig.playerDebug)
                player.sendf("There was an error processing your donations!");
        }

    }
}
