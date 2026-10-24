package com.nvrsocial.glintwein.workspace.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class WorkspaceMemberRequest {
    @NotBlank
    @Email
    private String email;

    public WorkspaceMemberRequest () {}

    public WorkspaceMemberRequest(String email) {
        this.email = email;
    }
}
