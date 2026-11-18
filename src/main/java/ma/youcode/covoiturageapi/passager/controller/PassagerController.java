package ma.youcode.covoiturageapi.passager.controller;

import jakarta.validation.Valid;
import ma.youcode.covoiturageapi.passager.dto.request.PassagerRequestDto;
import ma.youcode.covoiturageapi.passager.dto.response.PassagerResponseDto;
import ma.youcode.covoiturageapi.passager.service.inter.PassagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passagers")
public class PassagerController {

    private final PassagerService passagerService;
    public PassagerController(PassagerService passagerService) {
        this.passagerService = passagerService;
    }

    @PostMapping
    public ResponseEntity<PassagerResponseDto> createPassager(@Valid @RequestBody PassagerRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passagerService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<PassagerResponseDto>> getAll() {
        return ResponseEntity.ok(passagerService.findAll());
    }

}
