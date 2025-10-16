package com.example.ApiRound.crm.yoyo.medi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediServiceRepository extends JpaRepository<MediServiceEntity, Long> {

    /** 회사별 의료 서비스 조회 (회사 내부 화면용, Fetch Join) */
    @Query("""
        SELECT m
        FROM MediServiceEntity m
        JOIN FETCH m.item i
        JOIN FETCH i.ownerCompany c
        WHERE c.companyId = :companyId
    """)
    List<MediServiceEntity> findAllByCompanyIdWithFetch(@Param("companyId") Integer companyId);

    /** 상세 패널 1차 조회: 특정 itemId 에 매핑된 서비스들 */
    List<MediServiceEntity> findAllByItem_IdOrderByServiceIdDesc(Long itemId);

    /** 상세 패널 2차 폴백: 같은 회사(companyId)의 모든 서비스(지점 무관) */
    @Query("""
        SELECT m
        FROM MediServiceEntity m
        JOIN m.item i
        JOIN i.ownerCompany c
        WHERE c.companyId = :companyId
        ORDER BY m.serviceId DESC
    """)
    List<MediServiceEntity> findAllByOwnerCompanyId(@Param("companyId") Integer companyId);
    // 회사별 의료 서비스 조회 (활성만)
    @Query("SELECT m FROM MediServiceEntity m " +
            "JOIN FETCH m.item i " +
            "JOIN FETCH i.ownerCompany c " +
            "WHERE c.companyId = :companyId AND m.deletedAt IS NULL")
    List<MediServiceEntity> findActiveByCompanyIdWithFetch(@Param("companyId") Integer companyId);

    // 회사별 의료 서비스 조회 (삭제된 것 포함 - 관리자용)
    @Query("SELECT m FROM MediServiceEntity m " +
            "JOIN FETCH m.item i " +
            "JOIN FETCH i.ownerCompany c " +
            "WHERE c.companyId = :companyId")
    List<MediServiceEntity> findAllByCompanyIdWithFetch(@Param("companyId") Integer companyId);
}
