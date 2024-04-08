package cn.hserver.runner;

public class Runner {
    public void startMain(Runnable runner, ClassLoader classLoader) {
        Thread runnerThread = new Thread(runner);
        runnerThread.setContextClassLoader(classLoader);
        runnerThread.setName(Thread.currentThread().getName());
        runnerThread.start();
    }
}
