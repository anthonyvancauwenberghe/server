package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;

public class Yelling {

    public static final String[] COLOUR_SUFFICES = {"369", "mon", "red", "gre", "blu", "yel", "cya", "mag", "whi",
            "bla", "lre", "dre", "dbl", "or1", "or2", "or3", "gr1", "gr2", "gr3", "str", "end"};
    private static final String UNAVAILABLE_TAGS[] = {"Owner", "Mod", "Admin", "Staff", "Manager", "Creator",
            "Distributor", "Sell", "Buy", "Spawn", "Sucks", "Hate", "Flame", "Bitch", "Nigga", "Hoe", "Whore", "Scam",
            "Shit", "Demote", "Trusted", "Fuck", "Slut", "Kappa"

    };

    static {
        CommandHandler.submit(new Command("testcolors", Rank.PLAYER) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                for(final String suffix : COLOUR_SUFFICES){
                    player.getActionSender().sendMessage("@" + suffix + "@[Owner][Graham]:Testing message :" + suffix);
                }
                return false;
            }

        });
    }

    private String yellTitle = "";
    private boolean yellEnabled = true;
    private boolean yellColoursEnabled = true;
    private long yellTimer = 0;

    public static String isValidTitle(final String s) {
        final StringBuilder errorMessage = new StringBuilder("").append("You cannot have ");
        if(s.contains("@"))
            errorMessage.append("@s, ");
        for(final String wrong : UNAVAILABLE_TAGS){
            if(s.toLowerCase().contains(wrong.toLowerCase()))
                errorMessage.append(wrong).append(", ");
        }
        if(errorMessage.toString().length() > 18){
            errorMessage.append(" in your tag");
            return errorMessage.toString();
        }else
            return "";
    }

    public void setYellTitle(final String s) {
        yellTitle = s;
    }

    public String getTag() {
        return yellTitle;
    }

    public void updateYellTimer() {
        yellTimer = System.currentTimeMillis();
    }

    public boolean isYellEnabled() {
        return yellEnabled;
    }

    public void setYellEnabled(final boolean b) {
        yellEnabled = b;
    }

    public boolean isYellColoursEnabled() {
        return yellColoursEnabled;
    }

    public void setYellColoursEnabled(final boolean b) {
        yellColoursEnabled = b;
    }

    public long getYellTimer() {
        return yellTimer;
    }
}
