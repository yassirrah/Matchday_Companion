package ma.yassir.matchday.matchday_backend.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
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
}
