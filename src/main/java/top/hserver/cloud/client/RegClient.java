package top.hserver.cloud.client;

import top.hserver.cloud.CloudManager;

import java.io.InputStream;
import java.util.Properties;

public class RegClient extends Thread {

    public void run() {
        try {
            Properties pps = new Properties();
            InputStream resourceAsStream = CloudManager.class.getResourceAsStream("/application.properties");
            pps.load(resourceAsStream);
            Object host = pps.get("app.cloud.slave.master.host");
            if (host != null) {
                new ChatClient(host.toString(),CloudManager.port).start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
