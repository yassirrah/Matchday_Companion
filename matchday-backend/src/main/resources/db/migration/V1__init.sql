CREATE TABLE IF NOT EXISTS venue_status (
                                            venue_id TEXT PRIMARY KEY,
                                            status TEXT NOT NULL CHECK (status IN ('QUIET', 'OK', 'PACKED')),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_by_device TEXT
    );

CREATE TABLE IF NOT EXISTS venue_status_update (
                                                   id UUID PRIMARY KEY,
                                                   venue_id TEXT NOT NULL,
                                                   status TEXT NOT NULL CHECK (status IN ('QUIET', 'OK', 'PACKED')),
    device_id TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
    );

CREATE INDEX IF NOT EXISTS idx_venue_update_venue_time
    ON venue_status_update (venue_id, device_id, created_at DESC);
