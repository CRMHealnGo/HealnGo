package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.repository.ItemListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemListServiceImpl implements ItemListService {
    
    @Autowired
    private ItemListRepository itemListRepository;
    
    @Override
    public List<ItemList> getAllItems() {
        return itemListRepository.findAll();
    }
    
    @Override
    public ItemList getItemById(Long id) {
        return itemListRepository.findById(id).orElse(null);
    }
    
    @Override
    public Page<ItemList> getItemsByRegionAndCategory(String region, String subRegion, String category, Pageable pageable) {
        return itemListRepository.findByRegionAndSubregionAndCategory(region, subRegion, category, pageable);
    }
    
    @Override
    public Page<ItemList> getItemsByCategory(String category, Pageable pageable) {
        return itemListRepository.findByCategory(category, pageable);
    }
    
    @Override
    public long countByRegionAndCategory(String region, String subRegion, String category) {
        return itemListRepository.countByRegionAndSubregionAndCategory(region, subRegion, category);
    }
    
    @Override
    public long countByCategory(String category) {
        return itemListRepository.countByCategory(category);
    }
    
    @Override
    public List<String> getAllCategories() {
        return itemListRepository.findAllCategories();
    }
    
    @Override
    public ItemList saveItem(ItemList item) {
        return itemListRepository.save(item);
    }
    
    @Override
    public void deleteItem(Long id) {
        itemListRepository.deleteById(id);
    }
    
    @Override
    public List<ItemList> getAllList() {
        return itemListRepository.findAll();
    }
    
    @Override
    public ItemList getListById(Long id) {
        return itemListRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<ItemList> getListByRegionAndCategory(String region, String subRegion, String category) {
        return itemListRepository.findByRegionAndSubregionAndCategory(region, subRegion, category, Pageable.unpaged()).getContent();
    }
    
    @Override
    public List<ItemList> getListByCategory(String category) {
        return itemListRepository.findByCategory(category, Pageable.unpaged()).getContent();
    }
    
    @Override
    public List<ItemList> getFavoriteItems(Long userId) {
        // TODO: 즐겨찾기 관련 로직 구현
        return null;
    }
    
    @Override
    public void addFavorite(Long userId, Long itemId) {
        // TODO: 즐겨찾기 추가 로직 구현
    }
    
    @Override
    public void removeFavorite(Long userId, Long itemId) {
        // TODO: 즐겨찾기 삭제 로직 구현
    }
}
