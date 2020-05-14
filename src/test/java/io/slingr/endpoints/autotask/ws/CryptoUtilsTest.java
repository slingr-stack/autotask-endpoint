package io.slingr.endpoints.autotask.ws;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CryptoUtilsTest {
    @Test
    public void testEncryptDecrypt() {
        String encrypted = CryptoUtils.getInstance().encrypt("test");
        String decrypted = CryptoUtils.getInstance().decrypt(encrypted);
        assertEquals(decrypted, "test");
    }
}
