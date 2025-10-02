package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.repository.ItemListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListServiceImpl implements ListService {
    
    @Autowired
    private ItemListRepository itemListRepository;
    
    @Override
    public List<ItemList> getAllList() {
        return itemListRepository.findAll();
    }
    
    @Override
    public ItemList getListById(Long id) {
        return itemListRepository.findById(id).orElse(null);
    }
    
    @Override
    public ItemList addList(ItemList hospital) {
        return itemListRepository.save(hospital);
    }
    
    @Override
    public List<ItemList> getListByCategory(String category) {
        return itemListRepository.findByCategory(category, Pageable.unpaged()).getContent();
    }
    
    @Override
    public List<ItemList> getListByRegionAndCategory(String region, String subRegion, String category) {
        return itemListRepository.findByRegionAndSubregionAndCategory(region, subRegion, category, Pageable.unpaged()).getContent();
    }
    
    @Override
    public Page<ItemList> getListPaged(Pageable pageable) {
        return itemListRepository.findAll(pageable);
    }
    
    @Override
    public Page<ItemList> getListByCategoryPaged(String category, Pageable pageable) {
        return itemListRepository.findByCategory(category, pageable);
    }
    
    @Override
    public Page<ItemList> getListByRegionAndCategoryPaged(String region, String subRegion, String category, Pageable pageable) {
        return itemListRepository.findByRegionAndSubregionAndCategory(region, subRegion, category, pageable);
    }
    
    @Override
    public long getTotalCount() {
        return itemListRepository.count();
    }
    
    @Override
    public long getCountByCategory(String category) {
        return itemListRepository.countByCategory(category);
    }
    
    @Override
    public long getCountByRegionAndCategory(String region, String subRegion, String category) {
        return itemListRepository.countByRegionAndSubregionAndCategory(region, subRegion, category);
    }
    
    @Override
    public List<String> getAllCategories() {
        return itemListRepository.findAllCategories();
    }
}