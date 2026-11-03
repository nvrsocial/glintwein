package com.nvrsocial.glintwein.common.exception;

public abstract class ForbiddenException extends RuntimeException {
    protected ForbiddenException(String message) {
        super(message);
    }
}
