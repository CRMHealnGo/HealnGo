package com.example.ApiRound.crm.hyeonah.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, Integer> {
    
    Optional<CompanyUser> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<CompanyUser> findByEmailAndIsActive(String email, Boolean isActive);

}

