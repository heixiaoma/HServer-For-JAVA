package test5;

public class TestBytes {

    public static void main(String[] args) {

        byte[] a = new byte[]{12, 45, 78, 0, 0, 0};
        int i = 0;
        for (; i < a.length; i++) {
            if (a[i] == 0) {
                break;
            }
        }
        byte[] b = new byte[i];
        System.arraycopy(a,0,b,0,i);



    }

}
