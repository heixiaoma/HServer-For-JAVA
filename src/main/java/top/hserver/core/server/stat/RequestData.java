package top.hserver.core.server.stat;


import java.text.SimpleDateFormat;
import java.util.Date;



public class RequestData {
    private  String ip;
    private  String url;
    private long sentBytes;
    private long receivedBytes;
    private long speed;
    private String time;

    public RequestData(String ip, String url, long sentBytes, long receivedBytes, long speed) {
        this.ip = ip;
        this.url = url;
        this.sentBytes = sentBytes;
        this.receivedBytes = receivedBytes;
        this.speed = speed;
        this.time=CurrentTimeStamp();
    }

    public  String CurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        String formatCurrentTime = sdfDate.format(currentTime);
        return formatCurrentTime;
    }

    // ----- getters -----
    public String getIp() {
        return ip;
    }

    public String getUrl() {
        return url;
    }

    public long getSentBytes() {
        return sentBytes;
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public long getSpeed() {
        return speed;
    }

    public String getTime() {
        return time;
    }
}
