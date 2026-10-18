package com.nvrsocial.glintwein.common.exception;

public class EmailOrPasswordNotFound extends RuntimeException {
    public EmailOrPasswordNotFound(String message) {
        super(message);
    }
}
