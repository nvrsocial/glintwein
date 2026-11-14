CREATE TABLE task_comments (
                               id BIGSERIAL PRIMARY KEY,
                               task_id BIGINT NOT NULL,
                               author_id BIGINT,
                               content TEXT NOT NULL,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT fk_task_comments_task
                                   FOREIGN KEY (task_id)
                                       REFERENCES tasks(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_task_comments_author
                                   FOREIGN KEY (author_id)
                                       REFERENCES users(id)
                                       ON DELETE SET NULL
);

CREATE INDEX idx_task_comments_task_id
    ON task_comments(task_id);
