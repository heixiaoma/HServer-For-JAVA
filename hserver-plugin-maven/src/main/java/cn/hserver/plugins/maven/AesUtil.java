package cn.hserver.plugins.maven;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AesUtil {
    private static final String ALGORITHM = "AES";


    private static SecretKey getKey(byte[] key) {
        return new SecretKeySpec(key, ALGORITHM);
    }

    public static CipherInputStream encrypt(InputStream inputStream, String password) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password.getBytes(StandardCharsets.UTF_8)));
        return new CipherInputStream(inputStream,cipher);
    }


}
