package cn.hserver.plugin.node.core;

public class Node {

    public static Class<?> importClass(String classes) {
        try {
            return Node.class.getClassLoader().loadClass(classes);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
