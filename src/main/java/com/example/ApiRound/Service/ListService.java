package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ItemList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 병원 정보 관련 서비스
 */
public interface ListService {

    // 전체 병원 목록 조회
    List<ItemList> getAllList();

    // ID로 병원 조회
    ItemList getListById(Long id);

    // 병원 등록
    ItemList addList(ItemList hospital);

    // 카테고리별 병원 목록
    List<ItemList> getListByCategory(String category);

    // 지역 + 구 + 카테고리 검색
    List<ItemList> getListByRegionAndCategory(String region, String subRegion, String category);

    // 페이징된 병원 목록 조회
    Page<ItemList> getListPaged(Pageable pageable);

    // 카테고리별 페이징된 병원 목록 조회
    Page<ItemList> getListByCategoryPaged(String category, Pageable pageable);

    // 지역 + 구 + 카테고리 페이징된 검색
    Page<ItemList> getListByRegionAndCategoryPaged(String region, String subRegion, String category, Pageable pageable);

    // 전체 병원 수 조회
    long getTotalCount();

    // 카테고리별 병원 수 조회
    long getCountByCategory(String category);

    // 지역 + 구 + 카테고리별 병원 수 조회
    long getCountByRegionAndCategory(String region, String subRegion, String category);

    // 모든 카테고리 조회
    List<String> getAllCategories();
}