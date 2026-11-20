package ma.youcode.covoiturageapi.trip.service.impl;

import ma.youcode.covoiturageapi.car.entity.Car;
import ma.youcode.covoiturageapi.car.repository.CarRepository;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.trip.entity.Trip;
import ma.youcode.covoiturageapi.trip.dto.request.TripRequestDto;
import ma.youcode.covoiturageapi.trip.dto.response.TripResponseDto;
import ma.youcode.covoiturageapi.trip.mapper.TripMapper;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import ma.youcode.covoiturageapi.trip.service.inter.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TripServiceImp implements TripService {

    private final TripRepository tripRepository;
    private final CarRepository carRepository;
    private final TripMapper tripMapper;

    public TripServiceImp(TripRepository tripRepository, CarRepository carRepository, TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.carRepository = carRepository;
        this.tripMapper = tripMapper;
    }

    @Override
    public TripResponseDto create(TripRequestDto request) {
        Car car = carRepository.findById(request.carId()).orElseThrow(() -> new NotFoundException("Car with id : " + request.carId() + " not found"));

        Trip trip = tripMapper.toEntity(request);
        trip.setCar(car);

        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Override
    public List<TripResponseDto> getAll() {
        return List.of();
    }

    @Override
    public TripResponseDto getById(Long id) {
        return null;
    }

    @Override
    public TripResponseDto update(Long id, TripRequestDto request) {
        return null;
    }

    @Override
    public List<TripResponseDto> getByDriverId(Long driverId) {
        return List.of();
    }

    @Override
    public List<TripResponseDto> search(String departureCity, String arrivalCity, LocalDate date) {
        return List.of();
    }

    @Override
    public void delete(Long id) {

    }
}
