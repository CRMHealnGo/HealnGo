package com.example.ApiRound.crm.yoyo.medi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediServiceRepository extends JpaRepository<MediServiceEntity, Long> {

    // 필요하다면 커스텀 메서드 추가 가능
    // List<MediServiceEntity> findByItem_Id(Long itemId);
}
