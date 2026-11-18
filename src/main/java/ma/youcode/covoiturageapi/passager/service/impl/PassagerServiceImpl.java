package ma.youcode.covoiturageapi.passager.service.impl;

import ma.youcode.covoiturageapi.passager.dto.request.PassagerRequestDto;
import ma.youcode.covoiturageapi.passager.dto.response.PassagerResponseDto;
import ma.youcode.covoiturageapi.passager.mapper.PassagerMapper;
import ma.youcode.covoiturageapi.passager.repository.PassagerRepository;
import ma.youcode.covoiturageapi.passager.service.inter.PassagerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PassagerServiceImpl implements PassagerService {

    private final PassagerRepository passagerRepository;
    private final PassagerMapper passagerMapper;

    public PassagerServiceImpl(PassagerRepository passagerRepository, PassagerMapper passagerMapper) {
        this.passagerRepository = passagerRepository;
        this.passagerMapper = passagerMapper;
    }

    @Override
    public PassagerResponseDto create(PassagerRequestDto request) {
        return null;
    }

    @Override
    public List<PassagerResponseDto> findAll() {
        return List.of();
    }

    @Override
    public PassagerResponseDto findById(Long id) {
        return null;
    }

    @Override
    public PassagerResponseDto findByEmail(String email) {
        return null;
    }

    @Override
    public PassagerResponseDto update(Long id, PassagerRequestDto request) {
        return null;
    }
}
