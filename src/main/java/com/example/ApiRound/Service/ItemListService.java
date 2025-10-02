package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ItemList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemListService {
    
    // 모든 아이템 조회
    List<ItemList> getAllItems();
    
    // ID로 아이템 조회
    ItemList getItemById(Long id);
    
    // 지역과 카테고리로 검색 (페이징)
    Page<ItemList> getItemsByRegionAndCategory(String region, String subRegion, String category, Pageable pageable);
    
    // 카테고리로만 검색 (페이징)
    Page<ItemList> getItemsByCategory(String category, Pageable pageable);
    
    // 지역과 카테고리로 카운트
    long countByRegionAndCategory(String region, String subRegion, String category);
    
    // 카테고리로만 카운트
    long countByCategory(String category);
    
    // 모든 카테고리 조회
    List<String> getAllCategories();
    
    // 아이템 저장
    ItemList saveItem(ItemList item);
    
    // 아이템 삭제
    void deleteItem(Long id);
    
    // 전체 병원 목록 조회
    List<ItemList> getAllList();
    
    // ID로 병원 조회
    ItemList getListById(Long id);
    
    // 지역 + 구 + 카테고리 검색
    List<ItemList> getListByRegionAndCategory(String region, String subRegion, String category);
    
    // 카테고리별 병원 목록
    List<ItemList> getListByCategory(String category);
    
    // 즐겨찾기 목록 조회
    List<ItemList> getFavoriteItems(Long userId);
    
    // 즐겨찾기 추가
    void addFavorite(Long userId, Long itemId);
    
    // 즐겨찾기 삭제
    void removeFavorite(Long userId, Long itemId);
}
