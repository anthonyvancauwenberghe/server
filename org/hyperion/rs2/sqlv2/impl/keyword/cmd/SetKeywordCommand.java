package org.hyperion.rs2.sqlv2.impl.keyword.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.sqlv2.DbHub;
import org.hyperion.rs2.sqlv2.impl.keyword.Keyword;
import org.hyperion.rs2.sqlv2.impl.keyword.Keywords;

public class SetKeywordCommand extends Command {

    public SetKeywordCommand() {
        super("setkeyword", Rank.MODERATOR);
    }

    @Override
    public boolean execute(final Player player, final String input) throws Exception {
        final String[] parts = filterInput(input).split("\\s*");
        if(parts.length != 2){
            player.sendf("Syntax: setkeyword item_id item_keyword");
            return false;
        }
        if(!parts[0].matches("\\d{1,5}")){
            player.sendf("Error parsing item id");
            return false;
        }
        final int id = Integer.parseInt(parts[0]);
        if(ItemDefinition.forId(id) == null){
            player.sendf("The item id you entered is not a valid item!");
            return false;
        }
        final String name = parts[1];
        if(name.isEmpty()){
            player.sendf("Invalid name");
            return false;
        }
        final Keyword keyword = new Keyword(name, id);
        final Keyword existing = Keywords.cacheGet(id);
        if(existing != null){
            Keywords.uncache(existing);
            player.sendf("Removed old keyword for item %d: %s", id, existing.name());
            if(!DbHub.initialized() || !DbHub.getDonationsDb().enabled() || !DbHub.getGameDb().getKeywords().delete(existing)){
                player.sendf("Unable to update the database. The existing keyword has not been removed from the database.");
            }
        }
        Keywords.cache(keyword);
        player.sendf("Added a new keyword for item %d: %s", id, name);
        if(!DbHub.initialized() || !DbHub.getDonationsDb().enabled() || !DbHub.getGameDb().getKeywords().insert(keyword))
            player.sendf("Unable to update the database. The new keyword has not been inserted into the database.");
        return true;
    }
}
