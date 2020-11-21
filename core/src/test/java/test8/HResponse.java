package test8;

public interface HResponse {

    boolean isSuccess();

    Object complete();

    Throwable exception();

    interface Listener {
        void complete(Object arg);

        void exception(Throwable t);
    }
}
