package test8;

public class HResp implements HResponse {
    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Object complete() {
        return null;
    }

    @Override
    public Throwable exception() {
        return null;
    }
}
