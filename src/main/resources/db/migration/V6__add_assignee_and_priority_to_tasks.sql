ALTER TABLE tasks
    ADD COLUMN assignee_id BIGINT,
    ADD COLUMN priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM';

ALTER TABLE tasks
    ADD CONSTRAINT fk_tasks_assignee
        FOREIGN KEY (assignee_id)
            REFERENCES users(id)
            ON DELETE SET NULL,

    ADD CONSTRAINT chk_tasks_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'));

CREATE INDEX idx_tasks_assignee_id
    ON tasks(assignee_id);
