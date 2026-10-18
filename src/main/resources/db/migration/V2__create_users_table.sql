
CREATE TABLE workspaces (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE
    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workspace_members (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    workspace_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE
    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_workspace_member_user
    FOREIGN KEY (user_id)
    REFERENCES users(id),

    CONSTRAINT fk_workspace_member_workspace
    FOREIGN KEY (workspace_id)
    REFERENCES workspaces(id),

    CONSTRAINT uq_workspace_member
    UNIQUE (user_id, workspace_id),

    CONSTRAINT chk_workspace_member_role
    CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'))
);

CREATE INDEX idx_workspace_members_workspace
    ON workspace_members(workspace_id);
