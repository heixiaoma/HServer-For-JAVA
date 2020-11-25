package test5;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Remove {

    public static void main(String[] args) throws Exception{

        List<Integer> a=new ArrayList<>();
        a.add(1);
        a.add(2);
        System.out.println(a.get(a.size()-1));
        a.add(3);
        System.out.println(a.get(a.size()-1));
        System.out.println(a);
    }
}
