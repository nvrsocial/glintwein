package com.nvrsocial.glintwein.activity.component;

import com.nvrsocial.glintwein.activity.dto.TaskActivityResponse;
import com.nvrsocial.glintwein.activity.entity.TaskActivity;
import org.springframework.stereotype.Component;

@Component
public class TaskActivityMapper {
    public TaskActivityResponse toResponse(TaskActivity activity) {
        Long actorId = activity.getActor() == null ? null : activity.getActor().getId();
        String actorUsername = activity.getActor() == null ? null : activity.getActor().getUsername();

        return new TaskActivityResponse(activity.getId(), activity.getTask().getId(), actorId, actorUsername,
                activity.getType(), activity.getOldValue(), activity.getNewValue(), activity.getCreatedAt());
    }
}
