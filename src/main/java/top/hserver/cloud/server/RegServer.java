package top.hserver.cloud.server;

import top.hserver.cloud.CloudManager;

public class RegServer extends Thread {

    public void run() {
        try {
            new ChatServer(CloudManager.port).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
