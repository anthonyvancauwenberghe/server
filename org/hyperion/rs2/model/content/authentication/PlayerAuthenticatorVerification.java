package org.hyperion.rs2.model.content.authentication;

import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.net.security.authenticator.Authentication;
import org.hyperion.util.Time;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gilles on 19/02/2016.
 */
public class PlayerAuthenticatorVerification {

    public enum VerifyResponse {
        PIN_ENTERED_TWICE,
        INCORRECT_PIN,
        CORRECT_PIN
    }

    private final static Map<String, Integer> USED_PINS = new HashMap<>();

    public static VerifyResponse verifyPlayer(Player player, int enteredPin) {
        //We use this to make sure that a key cannot be used twice.
        if(USED_PINS.containsKey(player.getSafeDisplayName()))
            if(USED_PINS.get(player.getSafeDisplayName()) == enteredPin)
                return VerifyResponse.PIN_ENTERED_TWICE;

        //Just in case we get here without checking this first.
        if(player.getGoogleAuthenticatorKey() == null || player.getGoogleAuthenticatorKey().trim().isEmpty())
            return VerifyResponse.CORRECT_PIN;

        Authentication.AuthenticationResponse authenticationResponse = Authentication.authenticateKey(player.getGoogleAuthenticatorKey(), player.getGoogleAuthenticatorBackup(), enteredPin);
        if(authenticationResponse == Authentication.AuthenticationResponse.CORRECT_KEY) {
            USED_PINS.put(player.getSafeDisplayName(), enteredPin);

            TaskManager.submit(new Task(Time.THIRTY_SECONDS, "Used pin removal") {
                String username = player.getSafeDisplayName();
                @Override
                protected void execute() {
                    USED_PINS.remove(username);
                    stop();
                }
            });
            return VerifyResponse.CORRECT_PIN;
        }
        return VerifyResponse.INCORRECT_PIN;
    }

    static {
        NewCommandHandler.submit(new NewCommand("authenticator", Rank.HELPER, Time.TEN_SECONDS) {
            @Override
            protected boolean execute(Player player, String[] input) {
                PlayerAuthenticationGenerator.startAuthenticationDialogue(player);
                return true;
            }
        });
    }
}
