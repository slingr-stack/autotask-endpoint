package io.slingr.endpoints.autotask.ws;

import io.slingr.endpoints.utils.Base64Utils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * User: fmilone
 * Date: 9/23/13
 */
public class CryptoUtils {

    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

    private static String storedKey = "8md673jg982jkfFye82Fjw7AOq03jnf87NMcusu3WEnwei#jkhnsdi!jkaso8903";
    private SecretKey secretKey;
    private Cipher ecipher;
    private Cipher dcipher;

    private static CryptoUtils instance;

    private static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private CryptoUtils() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException {
        byte[] encodedKey = Base64.decodeBase64(storedKey);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(storedKey.toCharArray(), encodedKey, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        this.secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
        this.ecipher = Cipher.getInstance("AES");
        this.dcipher = Cipher.getInstance("AES");
        this.ecipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
        this.dcipher.init(Cipher.DECRYPT_MODE, this.secretKey);
    }

    public synchronized static CryptoUtils getInstance() {
        if (instance == null) {
            try {
                instance = new CryptoUtils();
            } catch (Exception e) {
                logger.error("Could not initialize crypto utils", e);
            }
        }
        return instance;
    }

    public String encrypt(String plaintext) {
        try {
            return Base64Utils.encode(ecipher.doFinal(plaintext.getBytes("UTF8")));
        } catch (Exception e) {
            return plaintext;
        }
    }

    public String decrypt(String ciphertext) {
        try {
            return new String(dcipher.doFinal(Base64Utils.decodeData(ciphertext)), "UTF8");
        } catch (Exception e) {
            return ciphertext;
        }
    }
}