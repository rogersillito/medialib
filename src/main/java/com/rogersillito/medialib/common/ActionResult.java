package com.rogersillito.medialib.common;

import lombok.Getter;

@SuppressWarnings("unused")
public class ActionResult {
    protected ActionResult() {
        this.success = true;
        this.failureMessage = null;
        this.exception = null;
    }

    protected ActionResult(String message) {
        this.success = false;
        this.failureMessage = message;
        this.exception = null;
    }

    protected ActionResult(Exception ex) {
        this.success = false;
        this.failureMessage = null;
        this.exception = ex;
    }

    @Getter
    private final boolean success;

    @Getter
    private final String failureMessage;

    @Getter
    private final Exception exception;

    public static ActionResult successResult() {
        return new ActionResult();
    }

    public static ActionResult failureResult(String message) {
        return new ActionResult(message);
    }

    public static ActionResult exceptionResult(Exception ex) {
        return new ActionResult(ex);
    }

    public boolean isException() {
        return this.exception != null;
    }
}

