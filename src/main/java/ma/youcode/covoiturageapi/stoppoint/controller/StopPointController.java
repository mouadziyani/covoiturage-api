package ma.youcode.covoiturageapi.stoppoint.controller;

import ma.youcode.covoiturageapi.stoppoint.dto.response.StopPointResponseDto;
import ma.youcode.covoiturageapi.stoppoint.service.inter.StopPointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stop-points")
public class StopPointController {
    private final StopPointService stopPointService;

    public StopPointController(StopPointService stopPointService) {
        this.stopPointService = stopPointService;
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<StopPointResponseDto>> getByTripId(@PathVariable Long tripId) {
        return ResponseEntity.ok(stopPointService.getByTripId(tripId));
    }
}
