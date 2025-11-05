-- Create audit_email table used by the audit JPA entity
CREATE TABLE IF NOT EXISTS audit_email (
  id BIGSERIAL PRIMARY KEY,
  job_name VARCHAR(200) NOT NULL,
  reference_id VARCHAR(100),
  payload TEXT,
  subject TEXT,
  message_id VARCHAR(200),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

