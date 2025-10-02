package com.example.ApiRound.repository;

import com.example.ApiRound.entity.FavoriteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteItemRepository extends JpaRepository<FavoriteItem, Long> {
    
    // 사용자의 즐겨찾기 목록 조회
    List<FavoriteItem> findByUserId(Long userId);
    
    // 특정 아이템이 즐겨찾기인지 확인
    boolean existsByUserIdAndItemId(Long userId, Long itemId);
    
    // 즐겨찾기 삭제
    void deleteByUserIdAndItemId(Long userId, Long itemId);
    
    // 사용자의 즐겨찾기 아이템 ID 목록 조회
    @Query("SELECT f.itemId FROM FavoriteItem f WHERE f.userId = :userId")
    List<Long> findItemIdsByUserId(@Param("userId") Long userId);
    
    // 사용자의 즐겨찾기 아이템 목록 조회 (JOIN)
    @Query("SELECT i FROM ItemList i JOIN FavoriteItem f ON i.id = f.itemId WHERE f.userId = :userId")
    List<com.example.ApiRound.entity.ItemList> findFavoriteItemsByUserId(@Param("userId") Long userId);
}
