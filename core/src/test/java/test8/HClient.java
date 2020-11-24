package test8;



public class HClient {

    public static HRequest connect(String host, int port) {
        String scheme = "http";
        return new HReq(scheme + "://" + host + ":" + port);
    }
}
