package test2;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<Integer> a=new ArrayList<>();
        a.add(0,1);
        a.add(0,2);
        a.add(3);
        a.add(0,4);
        System.out.println(a);
    }

}
