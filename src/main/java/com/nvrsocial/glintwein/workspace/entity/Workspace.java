package com.nvrsocial.glintwein.workspace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter

@Entity
@Table (name = "workspaces")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Workspace () {}

    public Workspace(String name, String description) {
        this.name = name;
        this. description = description;
    }

    @PrePersist
    public void prePersist(){
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
