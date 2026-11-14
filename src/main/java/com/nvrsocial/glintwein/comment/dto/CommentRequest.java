package com.nvrsocial.glintwein.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    @NotBlank
    @Size(max = 5000)
    private String content;

    public CommentRequest() {}

    public CommentRequest(String content) {
        this.content = content;
    }
}
