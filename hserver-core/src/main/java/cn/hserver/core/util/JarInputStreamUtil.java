package cn.hserver.core.util;


import cn.hserver.core.config.ConstConfig;

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
        String password = ConstConfig.PASSWORD;
        if (password != null && !password.trim().isEmpty()) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey(password.getBytes(StandardCharsets.UTF_8)));
            return new CipherInputStream(encryptedInputStream, cipher);
        } else {
            return encryptedInputStream;
        }
    }


}
