package ma.yassir.matchday.matchday_backend.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "venue_status_update",
        indexes = {
                @Index(name = "idx_venue_update_venue_time", columnList = "venue_id, device_id, created_at")
        })
public class VenueStatusUpdate {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "venue_id", nullable = false)
    private String venueId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CrowdStatus status;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

}
