package com.practicaldime.plugins.api;

public interface PlugLifecycle {

    void beforeLoad();

    void onLoadSuccess();

    void onLoadError(Throwable throwable);

    void onLoadComplete();

    void beforeExecute();

    void onExecuteSuccess();

    void onExecuteError(Throwable throwable);

    void onExecuteComplete();

    void beforeUnload();

    void onUnloadError(Throwable throwable);

    void onUnloadSuccess();
}
