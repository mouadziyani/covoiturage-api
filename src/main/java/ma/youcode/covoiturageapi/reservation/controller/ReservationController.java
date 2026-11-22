package ma.youcode.covoiturageapi.reservation.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturageapi.reservation.dto.request.ReservationRequestDto;
import ma.youcode.covoiturageapi.reservation.dto.response.ReservationResponseDto;
import ma.youcode.covoiturageapi.reservation.service.inter.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> create(@Valid @RequestBody ReservationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request));
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> getById(@PathVariable Long reservationId,
                                                          @RequestParam Long passagerId) {
        return ResponseEntity.ok(reservationService.getById(reservationId, passagerId));
    }

    @GetMapping("/passager/{passagerId}")
    public ResponseEntity<List<ReservationResponseDto>> getByPassagerId(@PathVariable Long passagerId) {
        return ResponseEntity.ok(reservationService.getByPassagerId(passagerId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ReservationResponseDto>> getByDriverId(@PathVariable Long driverId) {
        return ResponseEntity.ok(reservationService.getByDriverId(driverId));
    }

    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long reservationId, @RequestParam Long passagerId) {
        reservationService.cancel(reservationId, passagerId);
        return ResponseEntity.noContent().build();
    }
}
