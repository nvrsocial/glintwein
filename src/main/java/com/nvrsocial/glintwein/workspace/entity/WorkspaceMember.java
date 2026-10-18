package com.nvrsocial.glintwein.workspace.entity;

import com.nvrsocial.glintwein.user.entity.User;
import com.nvrsocial.glintwein.workspace.enums.WorkspaceRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

@Entity
@Table(name = "workspace_members")
public class WorkspaceMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private WorkspaceRole role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public WorkspaceMember () {}

    public WorkspaceMember(User user, Workspace workspace, WorkspaceRole role) {
        this.user = user;
        this.workspace = workspace;
    }

    @PrePersist
    public void prePersist(){
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }
}
