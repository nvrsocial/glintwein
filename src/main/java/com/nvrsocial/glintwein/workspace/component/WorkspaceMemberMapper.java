package com.nvrsocial.glintwein.workspace.component;

import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.workspace.dto.WorkspaceMemberResponse;
import com.nvrsocial.glintwein.workspace.entity.WorkspaceMember;
import org.springframework.stereotype.Component;

@Component
public class WorkspaceMemberMapper {
    public WorkspaceMemberResponse toResponse(WorkspaceMember member) {
        return new WorkspaceMemberResponse(member.getUser().getId(), member.getUser().getUsername(), member.getUser().getEmail(), member.getRole(), member.getJoinedAt());
    }
}
