CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS job_applications (
    id                          BIGSERIAL PRIMARY KEY,
    user_id                     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hr_name                     VARCHAR(100),
    hr_email                    VARCHAR(255),
    company_name                VARCHAR(150) NOT NULL,
    job_role                    VARCHAR(150) NOT NULL,
    applied_date                DATE NOT NULL,
    email_status                VARCHAR(20) NOT NULL,
    reply_received              BOOLEAN NOT NULL DEFAULT FALSE,
    follow_up_sent_count        INT NOT NULL DEFAULT 0,
    follow_up_reminder_enabled  BOOLEAN NOT NULL DEFAULT TRUE,
    application_status          VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    notes                       TEXT,
    import_source               VARCHAR(20) NOT NULL DEFAULT 'MANUAL',
    version                     INT NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_job_applications_user_id ON job_applications(user_id);
CREATE INDEX idx_job_applications_user_company ON job_applications(user_id, company_name);
CREATE INDEX idx_job_applications_user_applied_date ON job_applications(user_id, applied_date);
CREATE INDEX idx_job_applications_user_status ON job_applications(user_id, application_status);

CREATE TABLE IF NOT EXISTS notifications (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    job_application_id  BIGINT REFERENCES job_applications(id) ON DELETE CASCADE,
    type                VARCHAR(30) NOT NULL,
    title               VARCHAR(150) NOT NULL,
    message             TEXT NOT NULL,
    read                BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, read);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  VARCHAR(255) NOT NULL UNIQUE,
    expires_at  TIMESTAMP NOT NULL,
    revoked     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS google_oauth_tokens (
    id                      BIGSERIAL PRIMARY KEY,
    user_id                 BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    encrypted_refresh_token TEXT NOT NULL,
    scopes                  VARCHAR(500),
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS import_batches (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source              VARCHAR(20) NOT NULL,
    total_rows          INT NOT NULL DEFAULT 0,
    success_count       INT NOT NULL DEFAULT 0,
    failure_count       INT NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL,
    error_report_json   TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS reminders;
