package com.example.ApiRound.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ApiRound.Service.ItemListService;
import com.example.ApiRound.entity.ItemList;

@Controller
@RequestMapping("/list")
public class ListController {

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
        String mode = "search";
        
        if (region != null && !region.isEmpty() && !region.equals("전국") && category != null && !category.isEmpty()) {
            // 특정 지역 + 카테고리 검색
            items = itemListService.getItemsByRegionAndCategory(region, subRegion, category, pageable);
            totalCount = itemListService.countByRegionAndCategory(region, subRegion, category);
        } else if (category != null && !category.isEmpty()) {
            // 카테고리만 검색 (전국 또는 지역이 전국인 경우)
            items = itemListService.getItemsByCategory(category, pageable);
            totalCount = itemListService.countByCategory(category);
        } else {
            // 전체 조회
            items = itemListService.getItemsByCategory(null, pageable);
            totalCount = itemListService.countByCategory(null);
        }
        
        // 페이징 계산
        int totalPages = items.getTotalPages();
        int startPage = Math.max(1, pageNo - 2);
        int endPage = Math.min(totalPages, pageNo + 2);
        
        // 디버깅 로그 추가
        System.out.println("=== ListController Debug ===");
        System.out.println("region: " + region);
        System.out.println("subRegion: " + subRegion);
        System.out.println("category: " + category);
        System.out.println("items.getContent() size: " + items.getContent().size());
        System.out.println("totalCount: " + totalCount);
        System.out.println("============================");
        
        model.addAttribute("lists", items.getContent());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("amount", amount);
        model.addAttribute("mode", mode);
        model.addAttribute("region", region != null ? region : "전국");
        model.addAttribute("subRegion", subRegion);
        model.addAttribute("category", category);
        
        return "list";
    }
    
    // 단일 아이템 조회
    @GetMapping("/{id}")
    public String getItemById(@PathVariable Long id, Model model) {
        ItemList item = itemListService.getListById(id);
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
        
        // 페이징 계산
        int totalPages = items.getTotalPages();
        int startPage = Math.max(1, pageNo - 2);
        int endPage = Math.min(totalPages, pageNo + 2);
        
        model.addAttribute("lists", items.getContent());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("amount", amount);
        model.addAttribute("mode", "category");
        model.addAttribute("region", "전국");
        model.addAttribute("subRegion", "");
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