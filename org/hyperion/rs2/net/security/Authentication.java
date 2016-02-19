package org.hyperion.rs2.net.security;

import org.hyperion.Configuration;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.net.security.authenticator.GoogleAuthenticator;
import org.hyperion.rs2.net.security.authenticator.GoogleAuthenticatorKey;
import org.hyperion.rs2.net.security.authenticator.GoogleAuthenticatorQRGenerator;
import org.hyperion.rs2.savingnew.PlayerSaving;

/**
 * Created by Gilles on 19/02/2016.
 */
public final class Authentication {

    private final static GoogleAuthenticator authenticator = new GoogleAuthenticator();

    /**
     * Authorizes a code against Google's algorithm.
     * @param player The player to authenticate.
     * @return <code>True</code> if the code matches, <code>false</code> if not.
     */
    public static boolean authenticatePlayer(Player player, int enteredPin) {

        if(player.getGoogleAuthenticatorBackup().contains(enteredPin)) {
            player.getGoogleAuthenticatorBackup().remove(new Integer(enteredPin));
            PlayerSaving.save(player);
            return true;
        }

        return authenticator.authorize(player.getGoogleAuthenticatorKey(), enteredPin);
    }

    /**
     * Generates a set of credentials for the given player.
     * @param player The player.
     */
    private static GoogleAuthenticatorKey generateCredentials(Player player) {
        GoogleAuthenticatorKey credentials = authenticator.createCredentials();
        player.setGoogleAuthenticatorKey(credentials.getKey());
        player.setGoogleAuthenticatorBackup(credentials.getScratchCodes());
        PlayerSaving.save(player);
        return credentials;
    }

    /**
     * Generates a URL to a QR code the user can scan on their smart-phone.
     * @param player The player.
     * @return The URL.
     */
    public static void generateGoogleAuthenticatorQR(Player player) {
        if(player.getGoogleAuthenticatorKey() != null)
            throw new IllegalStateException("Player already has a key.");
        GoogleAuthenticatorKey googleAuthenticatorKey = generateCredentials(player);
        player.getActionSender().sendWebpage(GoogleAuthenticatorQRGenerator.getOtpAuthURL(Configuration.getString(Configuration.ConfigurationObject.NAME), player.getSafeDisplayName(), googleAuthenticatorKey));
        player.sendImportantMessage("VERY IMPORTANT - THESE ARE YOUR BACKUP KEYS");
        PlayerSaving.save(player);
        player.getGoogleAuthenticatorBackup().stream().map(key -> Integer.toString(key)).forEach(player::sendImportantMessage);
    }
}
