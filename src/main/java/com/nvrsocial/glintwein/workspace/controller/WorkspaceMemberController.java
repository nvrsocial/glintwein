package com.nvrsocial.glintwein.workspace.controller;

import com.nvrsocial.glintwein.workspace.dto.UpdateWorkspaceMemberRoleRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberRequest;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberResponse;
import com.nvrsocial.glintwein.workspace.repository.WorkspaceMemberRepository;
import com.nvrsocial.glintwein.workspace.service.WorkspaceMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/members")
public class WorkspaceMemberController {
    private final WorkspaceMemberService workspaceMemberService;

    public WorkspaceMemberController(WorkspaceMemberService workspaceMemberService) {
        this.workspaceMemberService = workspaceMemberService;
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceMemberResponse>> getWorkspaceMembers(@PathVariable Long workspaceId, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        List<WorkspaceMemberResponse> list = workspaceMemberService.getWorkspaceMembers(workspaceId, userId);

        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<WorkspaceMemberResponse> addMember(@PathVariable Long workspaceId, @Valid @RequestBody WorkspaceMemberRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());

        WorkspaceMemberResponse response = workspaceMemberService.addMember(workspaceId, userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PatchMapping("/{userId}/role")
    public ResponseEntity<WorkspaceMemberResponse> updateMemberRole(@PathVariable Long workspaceId, @PathVariable Long userId, @Valid @RequestBody UpdateWorkspaceMemberRoleRequest request, @AuthenticationPrincipal Jwt jwt) {

        Long currentUserId = Long.valueOf(jwt.getSubject());

        WorkspaceMemberResponse response = workspaceMemberService.updateMemberRole(workspaceId, currentUserId, userId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long workspaceId, @PathVariable Long userId, @AuthenticationPrincipal Jwt jwt) {

        Long currentUserId = Long.valueOf(jwt.getSubject());

        workspaceMemberService.removeMember(workspaceId, currentUserId, userId);

        return ResponseEntity.noContent().build();
    }

}
