package top.hserver.core.server.util;

import java.util.UUID;

public class UUIDUtil {

    public static String getUid(){
        return UUID.randomUUID().toString();
    }

}
