package org.hyperion.rs2.commands.impl;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.SQLRequest;
import org.hyperion.rs2.sql.requests.QueryRequest;

/**
 * @author Jack Daniels.
 */
public class KeywordCommand extends Command {

    /**
     * Constructs a new Keyword Command.
     *
     * @param startsWith
     */
    public KeywordCommand(final String startsWith) {
        super(startsWith, Rank.MODERATOR);
    }

    @Override
    public boolean execute(final Player player, String input) {
        input = filterInput(input);
        final String[] parts = input.split(" ");
        final String keyword = parts[0];
        if(SpawnCommand.getId(keyword) != null){
            player.getActionSender().sendMessage("Keyword was already set before..");
            if(Rank.hasAbility(player, Rank.ADMINISTRATOR)){
                try{
                    final int id = Integer.parseInt(parts[1]);
                    final int oldId = SpawnCommand.getId(keyword);
                    if(id == oldId){
                        player.getActionSender().sendMessage("That id was already set.");
                        return false;
                    }
                    SpawnCommand.setKeyword(keyword, id);
                    save(keyword, id);
                    return false;
                }catch(final Exception e){
                    player.getActionSender().sendMessage("Command could not be parsed.");
                }
            }
        }
        try{
            final int id = Integer.parseInt(parts[1]);
            SpawnCommand.setKeyword(keyword, id);
            save(keyword, id);
        }catch(final Exception e){
            player.getActionSender().sendMessage("Command could not be parsed.");
        }
        return true;
    }

    /**
     * Saves the command to the <code>SAVE_FILE</code>.
     *
     * @param keyword
     * @param id
     * @throws Exception
     */
    private void save(final String keyword, final int id) throws Exception {
        final SQLRequest request = new QueryRequest("INSERT INTO `keywords`(`keyword`, `id`) VALUES ('" + keyword + "'," + id + ")");
        if(Server.getConfig().getBoolean("logssql"))
            World.getWorld().getLogsConnection().offer(request);
    }


}
