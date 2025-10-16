package com.example.ApiRound.Controller;

import com.example.ApiRound.Service.ItemListService;
import com.example.ApiRound.entity.ItemList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ListController {

    private final ItemListService itemListService;

    /** "전국" / "전체" / "" / null  -> 검색조건 미적용(null)로 통일 */
    private String norm(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.isEmpty()) return null;
        if ("전국".equals(s) || "전체".equals(s) || "all".equalsIgnoreCase(s)) return null;
        return s;
    }

    /** SSR 목록 */
    @GetMapping("/list")
    public String getAllItems(Model model,
                              @RequestParam(required = false) String region,
                              @RequestParam(required = false, name = "subRegion") String subRegion,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "1") int pageNo,
                              @RequestParam(defaultValue = "15") int amount) {

        // ✅ 정규화
        String nRegion    = norm(region);
        String nSubRegion = norm(subRegion);
        String nCategory  = norm(category);

        Pageable pageable = PageRequest.of(pageNo - 1, amount, Sort.by(Sort.Direction.DESC, "id"));

        Page<ItemList> page;
        long totalCount;

        boolean hasRegionOrSub = (nRegion != null) || (nSubRegion != null);
        boolean hasCategory    = (nCategory != null);

        if (hasRegionOrSub) {
            page = itemListService.getItemsByRegionAndCategory(nRegion, nSubRegion, nCategory, pageable);
            totalCount = itemListService.countByRegionAndCategory(nRegion, nSubRegion, nCategory);
        } else if (hasCategory) {
            page = itemListService.getItemsByCategory(nCategory, pageable);
            totalCount = itemListService.countByCategory(nCategory);
        } else {
            // 전체
            page = itemListService.getItemsByCategory(null, pageable);
            totalCount = itemListService.countByCategory(null);
        }

        int totalPages = Math.max(page.getTotalPages(), 1);
        int startPage = Math.max(1, pageNo - 2);
        int endPage = Math.min(totalPages, pageNo + 2);

        model.addAttribute("lists", page.getContent());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("amount", amount);

        model.addAttribute("mode", hasRegionOrSub ? "search" : (hasCategory ? "category" : "all"));

        // 표기용 값
        model.addAttribute("region", (nRegion == null ? "전국" : nRegion));
        model.addAttribute("subRegion", nSubRegion);
        model.addAttribute("category", nCategory);

        return "list";
    }

    /** AJAX 목록(JSON) */
    @GetMapping(value = "/api/list", produces = "application/json")
    @ResponseBody
    public ListResponse getListApi(@RequestParam(required = false) String region,
                                   @RequestParam(required = false, name = "subRegion") String subRegion,
                                   @RequestParam(required = false) String category,
                                   @RequestParam(defaultValue = "1") int pageNo,
                                   @RequestParam(defaultValue = "15") int amount) {

        // ✅ 동일 정규화
        String nRegion    = norm(region);
        String nSubRegion = norm(subRegion);
        String nCategory  = norm(category);

        Pageable pageable = PageRequest.of(pageNo - 1, amount, Sort.by(Sort.Direction.DESC, "id"));

        Page<ItemList> page;
        long totalCount;

        boolean hasRegionOrSub = (nRegion != null) || (nSubRegion != null);
        boolean hasCategory    = (nCategory != null);

        if (hasRegionOrSub) {
            page = itemListService.getItemsByRegionAndCategory(nRegion, nSubRegion, nCategory, pageable);
            totalCount = itemListService.countByRegionAndCategory(nRegion, nSubRegion, nCategory);
        } else if (hasCategory) {
            page = itemListService.getItemsByCategory(nCategory, pageable);
            totalCount = itemListService.countByCategory(nCategory);
        } else {
            page = itemListService.getItemsByCategory(null, pageable);
            totalCount = itemListService.countByCategory(null);
        }

        int totalPages = Math.max(page.getTotalPages(), 1);
        int startPage = Math.max(1, pageNo - 2);
        int endPage = Math.min(totalPages, pageNo + 2);

        return new ListResponse(
                page.getContent(),
                totalCount,
                pageNo,
                totalPages,
                startPage,
                endPage,
                amount
        );
    }

    @Data
    @AllArgsConstructor
    public static class ListResponse {
        private java.util.List<ItemList> lists;
        private long totalCount;
        private int pageNo;
        private int totalPages;
        private int startPage;
        private int endPage;
        private int amount;
    }
}
