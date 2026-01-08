package ma.yassir.matchday.matchday_backend.dto;

import java.util.List;

public record VenueStatusResponse(
        String city,
        List<VenueStatusDto> items
) {}