CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       project_id BIGINT NOT NULL,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL DEFAULT 'TODO',
                       position INTEGER NOT NULL,
                       created_by BIGINT,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                       CONSTRAINT fk_tasks_project
                           FOREIGN KEY (project_id)
                               REFERENCES projects(id)
                               ON DELETE CASCADE,

                       CONSTRAINT fk_tasks_created_by
                           FOREIGN KEY (created_by)
                               REFERENCES users(id)
                               ON DELETE SET NULL,

                       CONSTRAINT chk_tasks_status
                           CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),

                       CONSTRAINT chk_tasks_position
                           CHECK (position >= 0),

                       CONSTRAINT uq_tasks_project_status_position
                           UNIQUE (project_id, status, position)
                           DEFERRABLE INITIALLY DEFERRED
);
