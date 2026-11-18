package ma.youcode.covoiturageapi.driver.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturageapi.driver.dto.request.DriverRequestDto;
import ma.youcode.covoiturageapi.driver.dto.response.DriverResponseDto;
import ma.youcode.covoiturageapi.driver.service.inter.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    public ResponseEntity<DriverResponseDto> createDriver(@Valid @RequestBody DriverRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<DriverResponseDto>> getAll() {
        return ResponseEntity.ok(driverService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponseDto> getDriverById(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable Long id, @Valid @RequestBody DriverRequestDto dto) {
        return ResponseEntity.ok(driverService.update(id, dto));
    }

    @GetMapping("/search")
    public List<DriverResponseDto> search(@RequestParam String word) {
        return driverService.search(word);
    }
}
