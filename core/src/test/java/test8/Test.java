package test8;

import io.netty.handler.codec.http.HttpMethod;

public class Test {

    public static void main(String[] args) throws Exception {

        HResponse exec = HClient.connect("127.0.0.1", 8888).httpMethod(HttpMethod.GET).uri("/req1").exec();
        System.out.println("同步：" + exec.getBodyAsString());

        HClient.connect("127.0.0.1", 8888).httpMethod(HttpMethod.GET).uri("/req1").exec(new HResponse.Listener() {
                @Override
                public void complete(HResponse arg) {
                    System.out.println("异步：" + arg.getBodyAsString());
                }

                @Override
                public void exception(Throwable t) {

                }
            });


        }

}
