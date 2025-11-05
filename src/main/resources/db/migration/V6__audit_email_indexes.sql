-- Add indexes for audit_email to speed lookups by reference_id and job_name
CREATE INDEX IF NOT EXISTS idx_audit_email_reference_id ON audit_email (reference_id);
CREATE INDEX IF NOT EXISTS idx_audit_email_job_name ON audit_email (job_name);

