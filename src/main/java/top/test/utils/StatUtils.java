package top.test.utils;


import javassist.*;

import java.io.ByteArrayInputStream;


public class StatUtils {

  public static void main(String[] args) {
    try {
      Class<?> aClass = StatUtils.class.getClassLoader().loadClass("top.test.action.Hello");
      ClassPool cp = ClassPool.getDefault();
      CtClass cc = cp.get("top.test.action.Hello");
      byte[] bytes = cc.toBytecode();
      String s ="F:\\JAVA\\HServer\\target\\classes\\top\\test\\action\\Hello.class";
      System.out.println(s);
      ClassPath classPath = cp.insertClassPath(s);
      cp.removeClassPath(classPath);
      cc.freeze();
      cc.defrost();
      cc = cp.makeClass(new ByteArrayInputStream(bytes));
      CtMethod m = cc.getDeclaredMethod("removeStat");
      m.addLocalVariable("begin",CtClass.longType);
      m.addLocalVariable("end",CtClass.longType);
      m.insertBefore("begin=System.currentTimeMillis();");
      m.insertAfter("end=System.currentTimeMillis();");
      m.insertAfter("System.out.println(\"类："+cc.getSimpleName()+"方法："+m.getName()+",耗时:\"+(end-begin)+\"毫秒\");\n");
      cc.toClass();
    }catch (Exception e){
      e.printStackTrace();
    }
  }

}
