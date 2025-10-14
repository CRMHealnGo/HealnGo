package com.example.ApiRound.crm.hyeonah.Service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ApiRound.crm.hyeonah.Repository.CompanyUserRepository;
import com.example.ApiRound.crm.hyeonah.dto.CompanyUserDto;
import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;

@Service
@Transactional
public class CompanyUserServiceImpl implements CompanyUserService {
    
    private final CompanyUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public CompanyUserServiceImpl(
            CompanyUserRepository repository,
            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public CompanyUser register(CompanyUserDto dto) {
        // 이메일 중복 확인
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        
        CompanyUser company = CompanyUser.builder()
                .email(dto.getEmail())
                .passwordHash(encodedPassword)
                .companyName(dto.getCompanyName())
                .bizNo(dto.getBizNo())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .isActive(true)
                .build();
        
        return repository.save(company);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyUser> findByEmail(String email) {
        return repository.findByEmailAndIsActive(email, true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<CompanyUser> login(String email, String password) {
        Optional<CompanyUser> companyOpt = repository.findByEmailAndIsActive(email, true);
        
        if (companyOpt.isEmpty()) {
            return Optional.empty();
        }
        
        CompanyUser company = companyOpt.get();
        
        // 비밀번호 검증
        if (passwordEncoder.matches(password, company.getPasswordHash())) {
            return Optional.of(company);
        }
        
        return Optional.empty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
    
    @Override
    public void updatePassword(String email, String newPassword) {
        Optional<CompanyUser> companyOpt = repository.findByEmail(email);
        
        if (companyOpt.isEmpty()) {
            throw new IllegalArgumentException("업체를 찾을 수 없습니다.");
        }
        
        CompanyUser company = companyOpt.get();
        String encodedPassword = passwordEncoder.encode(newPassword);
        company.setPasswordHash(encodedPassword);
        repository.save(company);
    }
}

