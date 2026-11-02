
CREATE TABLE projects (
                          id BIGSERIAL PRIMARY KEY,
                          workspace_id BIGINT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          description TEXT,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_projects_workspace
                              FOREIGN KEY (workspace_id)
                                  REFERENCES workspaces(id)
                                  ON DELETE CASCADE
);

CREATE INDEX idx_projects_workspace_id
    ON projects(workspace_id);
