CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               recipient_id BIGINT NOT NULL,
                               actor_id BIGINT,
                               type VARCHAR(30) NOT NULL,
                               message VARCHAR(500) NOT NULL,
                               workspace_id BIGINT NOT NULL,
                               project_id BIGINT,
                               task_id BIGINT,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_notifications_recipient
                                   FOREIGN KEY (recipient_id)
                                       REFERENCES users(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_notifications_actor
                                   FOREIGN KEY (actor_id)
                                       REFERENCES users(id)
                                       ON DELETE SET NULL,

                               CONSTRAINT fk_notifications_workspace
                                   FOREIGN KEY (workspace_id)
                                       REFERENCES workspaces(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_notifications_project
                                   FOREIGN KEY (project_id)
                                       REFERENCES projects(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_notifications_task
                                   FOREIGN KEY (task_id)
                                       REFERENCES tasks(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT chk_notifications_type
                                   CHECK (type IN ('TASK_ASSIGNED', 'TASK_STATUS_CHANGED', 'TASK_COMMENTED', 'WORKSPACE_MEMBER_ADDED'))
);

CREATE INDEX idx_notifications_recipient_created_at
    ON notifications(recipient_id, created_at DESC);
