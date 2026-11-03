package com.nvrsocial.glintwein.common.exception;

public class WorkspaceAccessDeniedException extends ForbiddenException {
    public WorkspaceAccessDeniedException(String message) {
        super(message);
    }
}
