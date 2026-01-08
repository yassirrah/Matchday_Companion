package ma.yassir.matchday.matchday_backend.service;

import jakarta.transaction.Transactional;
import ma.yassir.matchday.matchday_backend.domain.CrowdStatus;
import ma.yassir.matchday.matchday_backend.domain.VenueStatus;
import ma.yassir.matchday.matchday_backend.domain.VenueStatusUpdate;
import ma.yassir.matchday.matchday_backend.dto.VenueStatusDto;
import ma.yassir.matchday.matchday_backend.repository.VenueStatusRepository;
import ma.yassir.matchday.matchday_backend.repository.VenueStatusUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class VenueStatusService {
    private final VenueStatusRepository venueStatusRepository;
    private final VenueStatusUpdateRepository venueStatusUpdateRepository;

    private static final Duration COOLDOWN = Duration.ofMinutes(30);
    private static final Logger log = LoggerFactory.getLogger(VenueStatusService.class.getName());

    public VenueStatusService(VenueStatusRepository venueStatusRepository, VenueStatusUpdateRepository venueStatusUpdateRepository) {
        this.venueStatusRepository = venueStatusRepository;
        this.venueStatusUpdateRepository = venueStatusUpdateRepository;
    }

    public Optional<VenueStatusDto> getByVenueId(String venueId){
        return venueStatusRepository.findById(venueId).map(this::toDto);
    }

    @Transactional
    public VenueStatusDto updateStatus(String venueId, CrowdStatus newStatus, String deviceId){

        if(deviceId == null || deviceId.isBlank()){
            log.warn("Rejected update: missing device Id (venue_id={})", venueId);
            throw new IllegalArgumentException("X-Device-Id header is required");
        }

        OffsetDateTime now = OffsetDateTime.now();

        // ✅ Cooldown check (server-side trust)
        venueStatusUpdateRepository
                .findTopByVenueIdAndDeviceIdOrderByCreatedAtDesc(venueId, deviceId)
                .ifPresent(last -> {
                    Duration elapsed = Duration.between(last.getCreatedAt(), now);
                    if (elapsed.compareTo(COOLDOWN) < 0) {
                        long retryAfter = COOLDOWN.minus(elapsed).getSeconds();
                        log.info("Rejected update (cooldown): venueId={}, deviceId={}, retryAfter={}", venueId, deviceId,retryAfter);
                        throw new CooldownActiveException(Math.max(retryAfter, 1));
                    }
                });

        //appending history
        VenueStatusUpdate update = new VenueStatusUpdate();
        update.setDeviceId(deviceId);
        update.setStatus(newStatus);
        update.setVenueId(venueId);

        venueStatusUpdateRepository.save(update);
        log.info("Accepted update: venueId={}, deviceId={}, status={}",venueId, deviceId, newStatus);

        //updating the venue
        VenueStatus latest = venueStatusRepository.findById(venueId).orElseGet(VenueStatus::new);
        latest.setStatus(newStatus);
        latest.setVenueId(venueId);
        latest.setUpdatedAt(now);
        latest.setUpdatedByDevice(deviceId);
        VenueStatus saved = venueStatusRepository.save(latest);

        return toDto(saved);
    }

    public List<VenueStatusDto> getAll(){
        return venueStatusRepository.findAll().stream().map(this::toDto).toList();
    }

    private VenueStatusDto toDto(VenueStatus venueStatus) {
        return new VenueStatusDto(venueStatus.getVenueId(), venueStatus.getStatus(), venueStatus.getUpdatedAt());
    }
}
