package com.nvrsocial.glintwein.common.exception;

public class WorkspaceMemberAlreadyExistsException extends ConflictException {
    public WorkspaceMemberAlreadyExistsException(String message) {
        super(message);
    }
}
