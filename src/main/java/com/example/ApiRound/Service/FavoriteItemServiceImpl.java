package com.example.ApiRound.Service;

import com.example.ApiRound.entity.FavoriteItem;
import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.repository.FavoriteItemRepository;
import com.example.ApiRound.repository.ItemListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FavoriteItemServiceImpl implements FavoriteItemService {
    
    @Autowired
    private FavoriteItemRepository favoriteItemRepository;
    
    @Autowired
    private ItemListRepository itemListRepository;
    
    @Override
    public void addFavorite(Long userId, Long itemId) {
        FavoriteItem favoriteItem = new FavoriteItem();
        favoriteItem.setUserId(userId);
        favoriteItem.setItemId(itemId);
        favoriteItemRepository.save(favoriteItem);
    }
    
    @Override
    public void removeFavorite(Long userId, Long itemId) {
        favoriteItemRepository.deleteByUserIdAndItemId(userId, itemId);
    }
    
    @Override
    public boolean isFavorite(Long userId, Long itemId) {
        return favoriteItemRepository.existsByUserIdAndItemId(userId, itemId);
    }
    
    @Override
    public List<ItemList> getFavoriteItems(Long userId) {
        return favoriteItemRepository.findFavoriteItemsByUserId(userId);
    }
    
    @Override
    public List<ItemList> getFavoritesByUserId(Long userId) {
        return favoriteItemRepository.findFavoriteItemsByUserId(userId);
    }
}