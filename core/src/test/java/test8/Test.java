package test8;

import io.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.util.concurrent.atomic.LongAdder;

public class Test {
    private static LongAdder longAdder = new LongAdder();

    public static void main(String[] args) throws Exception {
        File file = new File("F:\\Web前端\\task\\README.md");
        HClient.connect("127.0.0.1", 8888).httpMethod(HttpMethod.POST).data("file", file.getName(), file, "multipart/form-data").uri("/file").exec(new MyCallBack());
    }

    static class MyCallBack implements HResponse.Listener {

        @Override
        public void complete(HResponse arg) {
            System.out.println(arg.getBodyAsString());
        }

        @Override
        public void exception(Throwable t) {
            t.printStackTrace();
        }
    }

}
