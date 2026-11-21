package ma.youcode.covoiturageapi.passager.service.impl;

import ma.youcode.covoiturageapi.exception.ConflictException;
import ma.youcode.covoiturageapi.exception.NotFoundException;
import ma.youcode.covoiturageapi.passager.dto.request.PassagerRequestDto;
import ma.youcode.covoiturageapi.passager.dto.response.PassagerResponseDto;
import ma.youcode.covoiturageapi.passager.entity.Passager;
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
        if (passagerRepository.existsByEmail(request.email())) {
            throw new ConflictException("this email : " + request.email() + " already exist");
        }
        Passager passager = passagerMapper.toEntity(request);
        Passager saved = passagerRepository.save(passager);

        return passagerMapper.toResponse(saved);
    }

    @Override
    public List<PassagerResponseDto> findAll() {
        return passagerRepository.findAll().stream()
                .map(passagerMapper::toResponse)
                .toList();
    }

    @Override
    public PassagerResponseDto findById(Long id) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Passager with id : " + id + " not found"));
        return passagerMapper.toResponse(passager);
    }

    @Override
    public PassagerResponseDto findByEmail(String email) {
        Passager passager = passagerRepository.findByEmail(email)
                .orElseThrow(()->new  NotFoundException("Passager with email : " + email + " not found"));
        return passagerMapper.toResponse(passager);
    }

    @Override
    public PassagerResponseDto update(Long id, PassagerRequestDto request) {
        Passager passager = passagerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Passager with id : " + id + " not found"));

        if (passagerRepository.existsByEmailAndIdNot(request.email(),id)) {
            throw  new ConflictException("this email : " + request.email() + " already used by other passager");
        }

        passagerMapper.updateEntity(request , passager);
        return passagerMapper.toResponse(passagerRepository.save(passager));
    }
}
