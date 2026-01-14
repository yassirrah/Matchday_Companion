CREATE OR REPLACE FUNCTION set_venue_status_updated_at()
RETURNS trigger AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_venue_status_updated_at on venue_status;

CREATE TRIGGER trg_venue_status_updated_at

BEFORE UPDATE ON venue_status
FOR EACH ROW EXECUTE FUNCTION set_venue_status_updated_at();