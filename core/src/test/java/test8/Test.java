package test8;

public class Test {

    public static void main(String[] args) throws Exception {
        HClient.connect("127.0.0.1", 8888).uri("/hserver.html").exec(new HResponse.Listener() {
            @Override
            public void complete(Object arg) {

            }

            @Override
            public void exception(Throwable t) {

            }
        });
    }
}
