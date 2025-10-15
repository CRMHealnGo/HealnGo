package com.example.ApiRound.crm.yoyo.medi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediServiceRepository extends JpaRepository<MediServiceEntity, Long> {

    // 회사별 의료 서비스 조회
    List<MediServiceEntity> findByItem_OwnerCompany_CompanyId(Integer companyId);
}
