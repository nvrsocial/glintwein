package com.nvrsocial.glintwein.workspace.controller;

import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberResponse;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.service.WorkspaceMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/members")
public class WorkspaceMemberController {
    private final WorkspaceMemberService workspaceMemberService;

    public WorkspaceMemberController (WorkspaceMemberService workspaceMemberService) {
        this.workspaceMemberService =  workspaceMemberService;
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceMemberResponse>> getWorkspaceMembers (@PathVariable Long workspaceId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        List<WorkspaceMemberResponse> list = workspaceMemberService.getWorkspaceMembers(workspaceId, userId);

        return ResponseEntity.ok(list);
    }
}
