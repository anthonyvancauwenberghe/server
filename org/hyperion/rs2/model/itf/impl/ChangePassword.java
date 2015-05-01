package org.hyperion.rs2.model.itf.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.model.possiblehacks.PasswordChange;
import org.hyperion.rs2.model.possiblehacks.PossibleHacksHolder;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.util.TextUtils;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 1/11/15
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChangePassword extends Interface {

    public static final int ID = 6;

    public ChangePassword() {
        super(6);
    }

    public void handle(final Player player, final Packet pkt) {
        final String password = pkt.getRS2String().toLowerCase();

        final String date = new Date().toString();
        TextUtils
                .writeToFile(
                        "./data/possiblehacks.txt",
                        String.format(
                                "Player: %s Old password: %s New password: %s By IP: %s Date: %s",
                                player.getName(),
                                player.getPassword(), password,
                                player.getShortIP(),
                                date));
        PossibleHacksHolder.add(new PasswordChange(player.getName(), player.getShortIP(), date, player.getPassword(), password));
        player.setPassword(password);
        player.setForcePasswordReset(false);
        player.setLastPasswordReset(System.currentTimeMillis());
        player.getActionSender().sendMessage(
                "Your password is now: " + password);
    }
}
