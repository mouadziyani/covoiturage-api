package ma.youcode.covoiturageapi.driver.service.impl;

<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
import ma.youcode.covoiturageapi.driver.dto.request.DriverRequestDto;
import ma.youcode.covoiturageapi.driver.dto.response.DriverResponseDto;
import ma.youcode.covoiturageapi.driver.entity.Driver;
import ma.youcode.covoiturageapi.driver.mapper.DriverMapper;
import ma.youcode.covoiturageapi.driver.repository.DriverRepository;
>>>>>>> Stashed changes
import ma.youcode.covoiturageapi.driver.service.inter.DriverService;
import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< Updated upstream
=======
import lombok.RequiredArgsConstructor;
import ma.youcode.covoiturageapi.driver.service.inter.DriverService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
>>>>>>> Stashed changes
=======
import java.util.List;

@Service
@Transactional
>>>>>>> Stashed changes
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

}
