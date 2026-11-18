package ma.youcode.covoiturageapi.driver.service.impl;

import ma.youcode.covoiturageapi.driver.dto.request.DriverRequestDto;
import ma.youcode.covoiturageapi.driver.dto.response.DriverResponseDto;
import ma.youcode.covoiturageapi.driver.entity.Driver;
import ma.youcode.covoiturageapi.driver.mapper.DriverMapper;
import ma.youcode.covoiturageapi.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import ma.youcode.covoiturageapi.driver.service.inter.DriverService;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    public DriverServiceImpl(DriverRepository driverRepository, DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    @Override
    public DriverResponseDto create(DriverRequestDto request) {

        if (driverRepository.existsByEmail(request.email())) {
            throw new ConflictException("this email : " + request.email() + " already exist");
        }

        if (driverRepository.existsByDrivingLicenseNumber(request.drivingLicenseNumber())) {
            throw new ConflictException("this drivingLicenseNumber : " + request.drivingLicenseNumber() + " already exist");
        }

        Driver driver = driverMapper.toEntity(request);
        Driver createdDriver = driverRepository.save(driver);
        return driverMapper.toResponse(createdDriver);
    }

    @Override
    public List<DriverResponseDto> findAll() {
        return driverRepository.findAll().stream()
                .map(driverMapper::toResponse)
                .toList();
    }

    @Override
    public DriverResponseDto findById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver with id : " + id + " not found"));
        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponseDto update(Long id, DriverRequestDto request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Driver with id : " + id + " not found"));

        if (driverRepository.existsByEmailAndIdNot(request.email(),id)) {
            throw  new ConflictException("this email : " + request.email() + " already used by other driver");
        }

        if (driverRepository.existsByDrivingLicenseNumberAndIdNot(request.drivingLicenseNumber(), id)) {
            throw  new ConflictException("this Driving License Number : " + request.drivingLicenseNumber() + " already used by other driver");
        }

        driverMapper.updateEntity(request, driver);

        return driverMapper.toResponse(driverRepository.save(driver));
    }

    @Override
    public List<DriverResponseDto> search(String word) {
        return driverRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(word , word , word)
                .stream().map(driverMapper::toResponse).toList();
    }

}
