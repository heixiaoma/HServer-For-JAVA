package test3;

import top.hserver.core.ioc.annotation.validate.*;
import top.hserver.core.server.util.ValidateUtil;


public class FieldTest {
    @Length(1)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static void main(String[] args) throws Exception {

        FieldTest fieldTest = new FieldTest();
        fieldTest.setName("d");
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            try {
                ValidateUtil.validate(fieldTest);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println(System.currentTimeMillis()-l);
    }
}
