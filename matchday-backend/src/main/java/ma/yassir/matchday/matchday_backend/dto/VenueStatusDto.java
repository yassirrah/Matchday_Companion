package ma.yassir.matchday.matchday_backend.dto;

import ma.yassir.matchday.matchday_backend.domain.CrowdStatus;

import java.time.OffsetDateTime;

public record VenueStatusDto(
        String venueId,
        CrowdStatus status,
        OffsetDateTime updatedAt
) {
}
