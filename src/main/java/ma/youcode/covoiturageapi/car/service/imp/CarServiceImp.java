package ma.youcode.covoiturageapi.car.service.imp;

import ma.youcode.covoiturageapi.driver.entity.Driver;
import ma.youcode.covoiturageapi.driver.repository.DriverRepository;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.car.dto.request.CarRequestDto;
import ma.youcode.covoiturageapi.car.dto.response.CarResponseDto;
import ma.youcode.covoiturageapi.car.entity.Car;
import ma.youcode.covoiturageapi.car.mappper.CarMapper;
import ma.youcode.covoiturageapi.car.repository.CarRepository;
import ma.youcode.covoiturageapi.car.service.inter.CarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarServiceImp implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final DriverRepository driverRepository;

    public CarServiceImp(CarRepository carRepository, DriverRepository driverRepository, CarMapper carMapper) {
        this.carRepository = carRepository;
        this.carMapper = carMapper;
        this.driverRepository = driverRepository;
    }

    @Override
    public CarResponseDto create(CarRequestDto request) {
        if (carRepository.existsByRegistrationNumber(request.registrationNumber())){
            throw new ConflictException("this registration number : " + request.registrationNumber() + " already exist");
        }
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(()->new  NotFoundException("driver not found"));

        Car car = carMapper.toEntity(request);
        car.setDriver(driver);
        Car createCar = carRepository.save(car);

        return carMapper.toResponse(createCar);
    }

    @Override
    public List<CarResponseDto> getAll() {
        return carRepository.findAll().stream()
                .map(carMapper::toResponse)
                .toList();
    }

    @Override
    public CarResponseDto getById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Car with id : " + id + " not found"));
        return carMapper.toResponse(car);
    }

    @Override
    @Transactional
    public CarResponseDto update(Long id, CarRequestDto request) {
        Car car = carRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Car with id : " + id + " not found"));

        if (carRepository.existsByRegistrationNumberAndIdNot(request.registrationNumber(), id)){
            throw  new ConflictException("this Registration Number : " + request.registrationNumber() + " already used by other car");
        }

        carMapper.updateEntity(request, car);

        return carMapper.toResponse(carRepository.save(car));
    }

    @Override
    public List<CarResponseDto> getByDriverId(Long driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new  NotFoundException("driver with id : " + driverId + " not found");
        }
        return carRepository.findByDriverId(driverId).stream()
                .map(carMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Car with id : " + id + " not found"));

        carRepository.delete(car);

    }
}
