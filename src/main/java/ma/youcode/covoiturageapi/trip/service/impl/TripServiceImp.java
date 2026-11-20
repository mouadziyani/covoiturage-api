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
        trip.setDriver(car.getDriver());

        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Override
    public List<TripResponseDto> getAll() {
        return tripRepository.findAll().stream().map(tripMapper::toResponse).toList();
    }

    @Override
    public TripResponseDto getById(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));
        return tripMapper.toResponse(trip);
    }

    @Override
    public TripResponseDto update(Long id, TripRequestDto request) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));
        Car car = carRepository.findById(request.carId()).orElseThrow(() -> new NotFoundException("Car with id : " + request.carId() + " not found"));

        tripMapper.updateEntity(request, trip);
        trip.setCar(car);
        trip.setDriver(car.getDriver());

        return tripMapper.toResponse(tripRepository.save(trip));
    }

    @Override
    public List<TripResponseDto> getByDriverId(Long driverId) {
        return tripRepository.findByCarDriverId(driverId).stream().map(tripMapper::toResponse).toList();
    }

    @Override
    public List<TripResponseDto> search(String departureCity, String arrivalCity, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        return tripRepository.findByDepartureCityIgnoreCaseAndArrivalCityIgnoreCaseAndDepartureDateTimeBetween(departureCity, arrivalCity, startOfDay, endOfDay)
                .stream().map(tripMapper::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new NotFoundException("Trip with id : " + id + " not found"));
        tripRepository.delete(trip);
    }
}
