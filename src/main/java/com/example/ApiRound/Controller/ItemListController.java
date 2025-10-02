package com.example.ApiRound.Controller;

import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.Service.ItemListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @Controller  // 임시로 비활성화 - ListController와 충돌
@RequestMapping("/list")
public class ItemListController {
    
    @Autowired
    private ItemListService itemListService;
    
    // 전체 아이템 목록 조회 (페이징)
    @GetMapping
    public String getAllItems(Model model,
                             @RequestParam(required = false) String region,
                             @RequestParam(required = false, name = "subRegion") String subRegion,
                             @RequestParam(required = false) String category,
                             @RequestParam(defaultValue = "1") int pageNo,
                             @RequestParam(defaultValue = "15") int amount) {
        
        Pageable pageable = PageRequest.of(pageNo - 1, amount);
        Page<ItemList> items;
        long totalCount;
        
        if (region != null && !region.isEmpty() && category != null && !category.isEmpty()) {
            // 지역 + 카테고리 검색
            items = itemListService.getItemsByRegionAndCategory(region, subRegion, category, pageable);
            totalCount = itemListService.countByRegionAndCategory(region, subRegion, category);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리만 검색
            items = itemListService.getItemsByCategory(category, pageable);
            totalCount = itemListService.countByCategory(category);
        } else {
            // 전체 조회
            items = itemListService.getItemsByCategory(null, pageable);
            totalCount = itemListService.countByCategory(null);
        }
        
        model.addAttribute("items", items.getContent());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("hasNext", items.hasNext());
        model.addAttribute("hasPrev", items.hasPrevious());
        
        return "list";
    }
    
    // 단일 아이템 조회
    @GetMapping("/{id}")
    public String getItemById(@PathVariable Long id, Model model) {
        ItemList item = itemListService.getItemById(id);
        if (item == null) {
            return "error";
        }
        model.addAttribute("item", item);
        return "detail";
    }
    
    // 카테고리별 아이템 목록 (페이징)
    @GetMapping("/category/{category}")
    public String getItemsByCategory(@PathVariable String category,
                                    @RequestParam(defaultValue = "1") int pageNo,
                                    @RequestParam(defaultValue = "15") int amount,
                                    Model model) {
        
        Pageable pageable = PageRequest.of(pageNo - 1, amount);
        Page<ItemList> items = itemListService.getItemsByCategory(category, pageable);
        long totalCount = itemListService.countByCategory(category);
        
        model.addAttribute("items", items.getContent());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("hasNext", items.hasNext());
        model.addAttribute("hasPrev", items.hasPrevious());
        model.addAttribute("category", category);
        
        return "list";
    }
    
    // 지역 + 카테고리별 검색
    @GetMapping("/search")
    public String searchItems(@RequestParam(required = false) String region,
                              @RequestParam(required = false, name = "subRegion") String subRegion,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "1") int pageNo,
                              @RequestParam(defaultValue = "15") int amount,
                              Model model) {
        
        Pageable pageable = PageRequest.of(pageNo - 1, amount);
        Page<ItemList> items = itemListService.getItemsByRegionAndCategory(region, subRegion, category, pageable);
        long totalCount = itemListService.countByRegionAndCategory(region, subRegion, category);
        
        model.addAttribute("items", items.getContent());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("hasNext", items.hasNext());
        model.addAttribute("hasPrev", items.hasPrevious());
        model.addAttribute("region", region);
        model.addAttribute("subRegion", subRegion);
        model.addAttribute("category", category);
        
        return "list";
    }
}
