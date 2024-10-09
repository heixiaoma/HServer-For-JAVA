package cn.hserver.core.server.util;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JarInputStreamUtil {
    private static final String ALGORITHM = "AES";

    private static SecretKey getKey(byte[] key) {
        return new SecretKeySpec(key, ALGORITHM);
    }

    public static InputStream decrypt(InputStream encryptedInputStream) throws Exception {
        String password = System.getProperty("password");
        if (password != null && password.trim().length() > 0) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey(password.getBytes(StandardCharsets.UTF_8)));
            return new CipherInputStream(encryptedInputStream, cipher);
        } else {
            return encryptedInputStream;
        }
    }


}
