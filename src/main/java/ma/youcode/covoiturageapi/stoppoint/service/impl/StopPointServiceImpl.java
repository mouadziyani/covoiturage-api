package ma.youcode.covoiturageapi.stoppoint.service.impl;

import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.stoppoint.dto.response.StopPointResponseDto;
import ma.youcode.covoiturageapi.stoppoint.mapper.StopPointMapper;
import ma.youcode.covoiturageapi.stoppoint.repository.StopPointRepository;
import ma.youcode.covoiturageapi.stoppoint.service.inter.StopPointService;
import ma.youcode.covoiturageapi.trip.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StopPointServiceImpl implements StopPointService {
    private final StopPointRepository stopPointRepository;
    private final TripRepository tripRepository;
    private final StopPointMapper stopPointMapper;

    public StopPointServiceImpl(StopPointRepository stopPointRepository, TripRepository tripRepository,
                                StopPointMapper stopPointMapper) {
        this.stopPointRepository = stopPointRepository;
        this.tripRepository = tripRepository;
        this.stopPointMapper = stopPointMapper;
    }
}
