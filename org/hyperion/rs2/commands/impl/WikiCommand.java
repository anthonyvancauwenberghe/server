package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.util.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 7/13/15
 * Time: 6:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class WikiCommand extends Command {

    private static final Map<String, String> KEY_TO_URL = new HashMap<>();

    static {
        try(final BufferedReader reader = new BufferedReader(new FileReader("./data/wikilinks.txt"))) {
            for(String line = ""; (line = reader.readLine()) != null;) {
                if(line.isEmpty()) break;
                String[] split = line.split("-");
                KEY_TO_URL.put(split[0], split[1]);
            }
        } catch(Exception e) {

        }

        CommandHandler.submit(new Command("wikishortcut", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                final String[] args = filterInput(input).split(",");
                KEY_TO_URL.put(args[0], args[1]);
                try (final BufferedWriter writer = new BufferedWriter(new FileWriter("", true))) {
                    writer.write(args[0]+"-"+args[1]);
                } catch(Exception e) {

                }
                return true;
            }
        });
    }

    public WikiCommand() {
        super("wiki", Rank.PLAYER);
    }

    public boolean execute(final Player player, final String input) {
        String key = filterInput(input);
        if(KEY_TO_URL.containsKey(key))
            key = KEY_TO_URL.get(key);
        String site = TextUtils.titleCase(key).replace(" ", "%20");
        player.sendf("l4unchur13 http://www.arteropk.wikia.com/wiki/%s", site);
        return true;
    }




}
