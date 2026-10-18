package com.nvrsocial.glintwein.workspace.repository;

import com.nvrsocial.glintwein.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}