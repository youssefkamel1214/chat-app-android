package controllers;

import java.util.concurrent.Executor;

public class BackExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
