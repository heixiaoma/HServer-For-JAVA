package test7;

import test1.bean.User;
import top.hserver.core.server.context.ConstConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    class a{

    }

    class b extends a{

    }

    class c extends b{

    }

    public static void main(String[] args) {

        System.out.println(a.class.isAssignableFrom(c.class));

    }
}
