package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.ContentTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 6/9/15
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bork implements ContentTemplate {

    static {
        CommandHandler.submit(new Command("bork", Rank.PLAYER) {

            public boolean execute(final Player player, final String input) {
                return true;
            }

        });
    }

    @Override
    public int[] getValues(int type) {
        return new int[0];
    }
}
