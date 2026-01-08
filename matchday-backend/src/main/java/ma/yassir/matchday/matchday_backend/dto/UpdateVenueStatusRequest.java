package ma.yassir.matchday.matchday_backend.dto;

import jakarta.validation.constraints.NotNull;
import ma.yassir.matchday.matchday_backend.domain.CrowdStatus;

public record UpdateVenueStatusRequest(@NotNull CrowdStatus status) {}
