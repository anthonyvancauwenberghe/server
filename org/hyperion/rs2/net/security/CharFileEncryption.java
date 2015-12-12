package org.hyperion.rs2.net.security;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by Gilles on 11/11/2015.
 */
public class CharFileEncryption {

    private final Key key;

    public CharFileEncryption(final String password) {
        this.key = convertPassword(password);
    }

    public static Key convertPassword(final String password) {
        final Key aesKey = new SecretKeySpec(password.getBytes(), "AES");
        return aesKey;
    }

    public Key getKey() {
        return key;
    }
}