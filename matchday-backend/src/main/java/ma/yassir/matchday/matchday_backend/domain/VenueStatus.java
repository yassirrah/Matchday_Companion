package ma.yassir.matchday.matchday_backend.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "venue_status")
public class VenueStatus {
    @Id
    @Column(name="venue_id", nullable = false)
    private String venueId;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private CrowdStatus status;

    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name="updated_by_device")
    private String updatedByDevice;


    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public CrowdStatus getStatus() {
        return status;
    }

    public void setStatus(CrowdStatus status) {
        this.status = status;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedByDevice() {
        return updatedByDevice;
    }

    public void setUpdatedByDevice(String updatedByDevice) {
        this.updatedByDevice = updatedByDevice;
    }
}
