package ma.yassir.matchday.matchday_backend.repository;


import ma.yassir.matchday.matchday_backend.domain.VenueStatusUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VenueStatusUpdateRepository extends JpaRepository<VenueStatusUpdate, UUID> {

    Optional<VenueStatusUpdate> findTopByVenueIdAndDeviceIdOrderByCreatedAtDesc(String venueId, String deviceId);
}