package com.example.ApiRound.repository;

import com.example.ApiRound.entity.ItemList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemListRepository extends JpaRepository<ItemList, Long> {
    
    // 지역과 카테고리로 검색
    @Query("SELECT i FROM ItemList i WHERE " +
           "(:region IS NULL OR :region = '' OR i.region LIKE CONCAT('%', :region, '%') OR i.address LIKE CONCAT('%', :region, '%')) AND " +
           "(:subRegion IS NULL OR :subRegion = '' OR i.subregion LIKE CONCAT('%', :subRegion, '%') OR i.address LIKE CONCAT('%', :subRegion, '%')) AND " +
           "(:category IS NULL OR :category = '' OR i.category LIKE CONCAT('%', :category, '%'))")
    Page<ItemList> findByRegionAndSubregionAndCategory(
            @Param("region") String region,
            @Param("subRegion") String subRegion,
            @Param("category") String category,
            Pageable pageable);
    
    // 카테고리로만 검색
    @Query("SELECT i FROM ItemList i WHERE " +
           "(:category IS NULL OR :category = '' OR i.category LIKE CONCAT('%', :category, '%'))")
    Page<ItemList> findByCategory(@Param("category") String category, Pageable pageable);
    
    // 지역과 카테고리로 카운트
    @Query("SELECT COUNT(i) FROM ItemList i WHERE " +
           "(:region IS NULL OR :region = '' OR i.region LIKE CONCAT('%', :region, '%') OR i.address LIKE CONCAT('%', :region, '%')) AND " +
           "(:subRegion IS NULL OR :subRegion = '' OR i.subregion LIKE CONCAT('%', :subRegion, '%') OR i.address LIKE CONCAT('%', :subRegion, '%')) AND " +
           "(:category IS NULL OR :category = '' OR i.category LIKE CONCAT('%', :category, '%'))")
    long countByRegionAndSubregionAndCategory(
            @Param("region") String region,
            @Param("subRegion") String subRegion,
            @Param("category") String category);
    
    // 카테고리로만 카운트
    @Query("SELECT COUNT(i) FROM ItemList i WHERE " +
           "(:category IS NULL OR :category = '' OR i.category LIKE CONCAT('%', :category, '%'))")
    long countByCategory(@Param("category") String category);
    
    // 모든 카테고리 조회
    @Query("SELECT DISTINCT i.category FROM ItemList i WHERE i.category IS NOT NULL")
    List<String> findAllCategories();
    
    // 회사 ID로 item 찾기
    Optional<ItemList> findByOwnerCompany_CompanyId(Integer companyId);
}
