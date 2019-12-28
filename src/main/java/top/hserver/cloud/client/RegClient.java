package top.hserver.cloud.client;

import top.hserver.cloud.CloudManager;

public class RegClient extends Thread {

    public void run() {
        try {
            new ChatClient("127.0.0.1",CloudManager.port).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
