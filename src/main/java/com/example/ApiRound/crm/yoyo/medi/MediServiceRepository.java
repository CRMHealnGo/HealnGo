package com.example.ApiRound.crm.yoyo.medi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediServiceRepository extends JpaRepository<MediServiceEntity, Long> {

    // 회사별 의료 서비스 조회
    @Query("SELECT m FROM MediServiceEntity m " +
            "JOIN FETCH m.item i " +
            "JOIN FETCH i.ownerCompany c " +
            "WHERE c.companyId = :companyId AND m.deletedAt IS NULL")
    List<MediServiceEntity> findActiveByCompanyIdWithFetch(@Param("companyId") Integer companyId);
}
