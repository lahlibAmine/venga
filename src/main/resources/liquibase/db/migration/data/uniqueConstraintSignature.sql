CREATE UNIQUE INDEX idx_unique_signature
    ON collaborateur (signature)
    WHERE signature IS NOT NULL AND signature <> '';
