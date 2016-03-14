package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">
import org.hyperion.Server;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.util.TextUtils;

import java.util.Arrays;
import java.util.List;
//</editor-fold>
/**
 * Created by DrHales on 2/29/2016.
 */
public class HeadModeratorCommands implements NewCommandExtension {
    //<editor-fold defaultstate="collapsed"desc="Rank">
    private final Rank rank = Rank.HEAD_MODERATOR;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("resetdeaths", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setDeathCount(0);
                        return true;
                    }
                },
                new NewCommand("resetkills", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setKillCount(0);
                        return true;
                    }
                },
                new NewCommand("resetelo", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.getPoints().setEloRating(1200);
                        return true;
                    }
                },
                new NewCommand("update", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Server.update(120, String.format("%sRestart Request", player.getName()));
                        return true;
                    }
                },
                new NewCommand("sethp", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > -1 && integer < Integer.MAX_VALUE, "Integer", "HP Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        int level = Integer.parseInt(input[1].trim());
                        target.getSkills().setLevel(Skills.HITPOINTS, level);
                        target.sendf("%s set your hitpoints to %,d.", TextUtils.optimizeText(player.getName()), level);
                        return true;
                    }
                },
                new NewCommand("unlock", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.getPermExtraData().put("passchange", System.currentTimeMillis());
                        target.getExtraData().put("needpasschange", false);
                        target.getExtraData().put("cantchangepass", false);
                        target.getExtraData().put("cantdoshit", false);
                        target.sendMessage("You have been unlocked by an admin");
                        return true;
                    }
                },
                new NewCommand("checkclans", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ClanManager.clans.values().stream().filter(clan -> clan.getPlayers().size() > 0 && !clan.getName().toLowerCase().startsWith("party")).forEach(clan -> {
                            player.sendf("Clan: %s, Owner: %s, Members: %d", clan.getName(), clan.getOwner(), clan.getPlayers().size());
                        });
                        return true;
                    }
                },
                new NewCommand("spawnobject", rank, new CommandInput<Integer>(integer -> GameObjectDefinition.forId(integer) != null, "Integer", "Object ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 21, "Integer", "Object Face"), new CommandInput<Integer>(integer -> integer > -1 && integer < 23, "Integer", "Object Type")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        final int type = Integer.parseInt(input[1].trim());
                        final int face = Integer.parseInt(input[2].trim());
                        player.getActionSender().sendCreateObject(id, type, face, player.getPosition());
                        return true;
                    }
                },
                new NewCommand("givekorasi", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Item item = new Item(19780, 1);
                        if (!target.getInventory().add(item)) {
                            target.getBank().add(item);
                        } else {
                            target.getInventory().add(item);
                        }
                        return true;
                    }
                },
                new NewCommand("givevigour", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Item item = new Item(19669, 1);
                        if (!target.getInventory().add(item)) {
                            target.getBank().add(item);
                        } else {
                            target.getInventory().add(item);
                        }
                        return true;
                    }
                },
                new NewCommand("resetkdr", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setKillCount(0);
                        target.setDeathCount(0);
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}
