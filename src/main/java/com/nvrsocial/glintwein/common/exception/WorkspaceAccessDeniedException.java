package com.nvrsocial.glintwein.common.exception;

public class WorkspaceAccessDeniedException extends RuntimeException {
    public WorkspaceAccessDeniedException(String message) {
        super(message);
    }
}
