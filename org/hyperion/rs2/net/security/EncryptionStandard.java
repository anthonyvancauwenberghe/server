package org.hyperion.rs2.net.security;

import org.apache.commons.codec.binary.Base64;
import org.hyperion.Server;

import javax.crypto.Cipher;
import java.security.Key;

/**
 * Created by Gilles on 29/10/2015.
 **/

public class EncryptionStandard {

    public static String encryptPassword(final String password) {
        return encrypt(password, Server.getCharFileEncryption().getKey());
    }

    public static String decryptPassword(final String password) {
        return decrypt(password, Server.getCharFileEncryption().getKey());
    }

    public static String encrypt(final String plainText, final Key encryptionKey) {
        try{
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            final byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.encodeBase64String(encryptedBytes);
        }catch(final Exception e){
            e.printStackTrace();
            return plainText;
        }
    }

    public static String decrypt(final String encrypted, final Key encryptionKey) {
        try{
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
            final byte[] plainBytes = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(plainBytes);
        }catch(final Exception e){
            e.printStackTrace();
            return encrypted;
        }
    }
}
