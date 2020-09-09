package test6;

public class Test {
    public static void main(String[] args) {
        Class<? super b> superclass = b.class.getSuperclass();
        System.out.println(c.class.isAssignableFrom(b.class));
        Class<?>[] interfaces = b.class.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            System.out.println(anInterface.getName());
        }
    }
}
