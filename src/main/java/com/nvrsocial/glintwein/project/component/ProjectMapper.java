package com.nvrsocial.glintwein.project.component;

import com.nvrsocial.glintwein.project.dto.ProjectRequest;
import com.nvrsocial.glintwein.project.dto.ProjectResponse;
import com.nvrsocial.glintwein.project.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(project.getId(), project.getWorkspace().getId(), project.getName(),
                project.getDescription(), project.getCreatedAt());
    }
}
