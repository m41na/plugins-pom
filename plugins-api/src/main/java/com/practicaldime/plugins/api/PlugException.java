package com.practicaldime.plugins.api;

public class PlugException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PlugException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlugException(String message) {
        super(message);
    }

    public PlugException(Throwable cause) {
        super(cause);
    }
}
