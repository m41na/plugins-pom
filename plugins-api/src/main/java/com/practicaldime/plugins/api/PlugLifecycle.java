package com.practicaldime.plugins.api;

public interface PlugLifecycle {

    void beforeLoad();

    void onLoadSuccess();

    void onLoadError(Throwable throwable);

    void beforeExecute();

    void onExecuteSuccess();

    void onExecuteError(Throwable throwable);

    void beforeUnload();

    void onUnloadSuccess();

    void onUnloadError(Throwable throwable);
}
