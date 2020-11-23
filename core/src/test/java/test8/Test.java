package test8;

import io.netty.handler.codec.http.HttpMethod;

import java.util.concurrent.atomic.LongAdder;

public class Test {
    private static LongAdder longAdder = new LongAdder();

    public static void main(String[] args) throws Exception {

        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            HClient.connect("127.0.0.1", 8888).httpMethod(HttpMethod.GET).uri("/req1?id=" + i).exec(new MyCallBack(i));
        }
        while (true) {
            if (longAdder.longValue() == 100000) {
                System.out.println((System.currentTimeMillis() - l) / 1000.0 + "/s");
                break;
            }
        }
    }

    static class MyCallBack implements HResponse.Listener {

        private int i;

        public MyCallBack(int i) {
            this.i = i;
        }

        @Override
        public void complete(HResponse arg) {
            longAdder.increment();
        }

        @Override
        public void exception(Throwable t) {

        }
    }

}
