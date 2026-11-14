CREATE TABLE task_activities (
                                 id BIGSERIAL PRIMARY KEY,
                                 task_id BIGINT NOT NULL,
                                 actor_id BIGINT,
                                 type VARCHAR(30) NOT NULL,
                                 old_value VARCHAR(255),
                                 new_value VARCHAR(255),
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT fk_task_activities_task
                                     FOREIGN KEY (task_id)
                                         REFERENCES tasks(id)
                                         ON DELETE CASCADE,

                                 CONSTRAINT fk_task_activities_actor
                                     FOREIGN KEY (actor_id)
                                         REFERENCES users(id)
                                         ON DELETE SET NULL,

                                 CONSTRAINT chk_task_activities_type
                                     CHECK (type IN ('CREATED', 'TITLE_CHANGED', 'DESCRIPTION_CHANGED', 'PRIORITY_CHANGED',
                                                     'STATUS_CHANGED', 'ASSIGNEE_CHANGED', 'COMMENT_ADDED'))
);

CREATE INDEX idx_task_activities_task_id
    ON task_activities(task_id);
