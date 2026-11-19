package ma.youcode.covoiturageapi.car.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturageapi.car.dto.request.CarRequestDto;
import ma.youcode.covoiturageapi.car.dto.response.CarResponseDto;
import ma.youcode.covoiturageapi.car.service.inter.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vehicules")
public class CarController {
    public final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping
    public ResponseEntity<CarResponseDto> createCar(@Valid @RequestBody CarRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAllCars() {
        return  ResponseEntity.status(HttpStatus.OK).body(carService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCarById(@PathVariable Long id) {
        return  ResponseEntity.status(HttpStatus.OK).body(carService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(@PathVariable Long id, @Valid @RequestBody CarRequestDto dto) {
        return  ResponseEntity.status(HttpStatus.OK).body(carService.update(id, dto));
    }

    @GetMapping("/drivers/{driverId}")
    public  ResponseEntity<List<CarResponseDto>> getCarsByDriverId(@PathVariable Long driverId) {
        return ResponseEntity.status(HttpStatus.OK).body(carService.getByDriverId(driverId));
    }

}
