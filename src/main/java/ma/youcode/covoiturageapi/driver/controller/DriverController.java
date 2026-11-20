package ma.youcode.covoiturageapi.driver.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturageapi.driver.dto.request.DriverRequestDto;
import ma.youcode.covoiturageapi.driver.dto.response.DriverResponseDto;
import ma.youcode.covoiturageapi.driver.service.inter.DriverService;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;
import ma.youcode.covoiturageapi.trip.service.inter.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/drivers")
public class DriverController {

    private final DriverService driverService;
    private final TripService tripService;

    public DriverController(DriverService driverService, TripService tripService) {
        this.driverService = driverService;
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<DriverResponseDto> createDriver(@Valid @RequestBody DriverRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(driverService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDto> getDriverById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(driverService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(driverService.update(id, dto));
    }

    @GetMapping("/search")
    public List<DriverResponseDto> search(@RequestParam String word) {
        return driverService.search(word);
    }

    @GetMapping("/{driverId}/trips")
    public ResponseEntity<List<TripResponseDto>> getTripsByDriverId(@PathVariable Long driverId) {
        return ResponseEntity.ok(tripService.getByDriverId(driverId));
    }
}
