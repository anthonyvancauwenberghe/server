package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.AccountValue;

import java.io.File;
import java.util.Date;

/**
 * @author Arsen Maxyutov.
 */
public class TradeChecker {

    private static final File DIR = new File("./logs/suspicioustrades/");

    /**
     * @param player
     * @param trader
     * @throws Exception
     */
    public TradeChecker(final Player player, final Player trader) {
        final Container receiving = trader.getTrade();
        final Container giving = player.getTrade();
        final int received = AccountValue.getContainerValue(receiving);
        final int gave = AccountValue.getContainerValue(giving);
        //System.out.println("Gave :" + gave + " Rec: " + received);
        if(received * 10 < gave && gave > 500){
            writeLog(player.getName().toLowerCase(), "Traded with: " + trader.getName() + ", gave:" + gave + ", received:" + received);
        }else if(gave * 10 < received && received > 500){
            writeLog(trader.getName().toLowerCase(), "Traded with: " + player.getName() + ", gave:" + received + ", received:" + gave);
        }
    }

    /**
     * This method is called to write the log.
     *
     * @param name
     * @param line
     */
    private void writeLog(final String name, final String line) {
        PlayerSaving.getSaving().saveLog(DIR + "/" + name + ".log", new Date() + "," + line);
    }
}
