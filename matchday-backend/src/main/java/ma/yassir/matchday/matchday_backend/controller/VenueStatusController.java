package ma.yassir.matchday.matchday_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import ma.yassir.matchday.matchday_backend.dto.UpdateVenueStatusRequest;
import ma.yassir.matchday.matchday_backend.dto.VenueStatusDto;
import ma.yassir.matchday.matchday_backend.dto.VenueStatusResponse;
import ma.yassir.matchday.matchday_backend.service.VenueStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venue-status")
public class VenueStatusController {
    private final VenueStatusService venueStatusService;

    public VenueStatusController(VenueStatusService venueStatusService) {
        this.venueStatusService = venueStatusService;
    }

    // GET /api/v1/venue-status?city=Rabat
    @Operation(summary = "List all venue crowd statuses (frontend filters by city in V1)")
    @GetMapping
    public VenueStatusResponse list(@RequestParam() String city) {
        List<VenueStatusDto> items  = venueStatusService.getAll();
        return new VenueStatusResponse(city, items);
    }

    // GET /api/v1/venue-status/{venueId}
    @Operation(summary = "Get crowd status for one venue by venueId")
    @GetMapping("/{venueId}")
    public ResponseEntity<VenueStatusDto> getOne(@PathVariable String venueId) {
        return venueStatusService.getByVenueId(venueId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update crowd status (server enforces cooldown per venue + device)")
    @PostMapping("/{venueId}")
    public ResponseEntity<VenueStatusDto> update(@PathVariable String venueId, @RequestHeader("X-Device-Id") String deviceId,
                                                 @Valid @RequestBody UpdateVenueStatusRequest request) {
        VenueStatusDto dto = venueStatusService.updateStatus(venueId, request.status(), deviceId);
        return ResponseEntity.ok(dto);
    }
}
