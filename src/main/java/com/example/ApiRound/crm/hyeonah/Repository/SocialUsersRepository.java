package com.example.ApiRound.crm.hyeonah.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ApiRound.crm.hyeonah.entity.SocialUsers;

@Repository
public interface SocialUsersRepository extends JpaRepository<SocialUsers, Integer> {
    
    Optional<SocialUsers> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<SocialUsers> findByEmailAndIsDeleted(String email, Boolean isDeleted);
}

