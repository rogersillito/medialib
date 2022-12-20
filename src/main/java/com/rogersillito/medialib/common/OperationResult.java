package com.rogersillito.medialib.common;

import lombok.Getter;

@SuppressWarnings("unused")
public class OperationResult<T> {
    protected OperationResult(T data) {
        this.success = true;
        this.failureMessage = null;
        this.exception = null;
        this.data = data;
    }

    protected OperationResult(String message) {
        this.success = false;
        this.failureMessage = message;
        this.exception = null;
        this.data = null;
    }

    protected OperationResult(Exception ex) {
        this.success = false;
        this.failureMessage = null;
        this.exception = ex;
        this.data = null;
    }

    @Getter
    private final T data;

    @Getter
    private final boolean success;

    @Getter
    private final String failureMessage;

    @Getter
    private final Exception exception;

    public static <T> OperationResult<T> successResult(T data) {
        return new OperationResult<>(data);
    }

    public static <T> OperationResult<T> failureResult(String message) {
        return new OperationResult<>(message);
    }

//    public static <T> OperationResult<T> exceptionResult(Exception ex) {
//        return new OperationResult<>(ex);
//    }
}
