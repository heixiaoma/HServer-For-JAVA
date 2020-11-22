package test8;

import io.netty.handler.codec.http.HttpMethod;

public class Test {

    public static void main(String[] args) throws Exception {
        long l = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            HClient.connect("127.0.0.1", 8888,4).httpMethod(HttpMethod.GET).uri("/req1").exec(new HResponse.Listener() {
                @Override
                public void complete(Object arg) {

                }

                @Override
                public void exception(Throwable t) {

                }
            });
        }
        System.out.println((System.currentTimeMillis()-l)/1000.0+"/s");


    }
}
