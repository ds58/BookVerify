package com.ruinscraft.bookverify;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class BookVerifyCrypto {

    private static final String ENCRYPTION_ALGORITHM = "AES";

    private String secret;
    private byte[] secretBytes;
    private Key key;
    private Cipher cipher;

    public BookVerifyCrypto(String secret) {
        this.secret = secret;
        secretBytes = secret.getBytes();
        key = new SecretKeySpec(secretBytes, ENCRYPTION_ALGORITHM);

        try {
            cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    protected static String generateSecret() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public String encrypt(String data) {
        try {
            return encrypt0(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String encrypt0(String data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrData = cipher.doFinal(data.getBytes());
        byte[] encrDataEncoded = Base64.getEncoder().encode(encrData);

        return new String(encrDataEncoded);
    }

    public String decrypt(String data) {
        try {
            return decrypt0(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String decrypt0(String data) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedData = Base64.getDecoder().decode(data.getBytes());
        byte[] decrDecodedData = cipher.doFinal(decodedData);

        return new String(decrDecodedData);
    }

}
