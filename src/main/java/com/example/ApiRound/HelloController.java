package com.example.ApiRound;

import com.example.ApiRound.Service.ClickLogService;
import com.example.ApiRound.Service.CommunityPostService;
import com.example.ApiRound.Service.ItemListService;
import com.example.ApiRound.Service.FavoriteItemService;
import com.example.ApiRound.entity.CommunityPost;
import com.example.ApiRound.entity.ItemList;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HelloController {

    private final ClickLogService clickLogService;
    private final CommunityPostService communityPostService;
    private final ItemListService itemListService;
    private final FavoriteItemService favoriteItemService;

    public HelloController(ClickLogService clickLogService, CommunityPostService communityPostService, ItemListService itemListService, FavoriteItemService favoriteItemService) {
        this.clickLogService = clickLogService;
        this.communityPostService = communityPostService;
        this.itemListService = itemListService;
        this.favoriteItemService = favoriteItemService;
    }

    // Location 페이지 HTML 서빙
    @GetMapping("/location")
    public String location() {
        return "location";
    }

    // HTML 파일 서빙을 위한 루트 매핑
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Main 페이지 HTML 서빙
    @GetMapping("/main")
    public String main(Model model) {
        List<Object[]> topCompanies = clickLogService.getTop3CompaniesLast7Days();
        List<CommunityPost> communityPosts = communityPostService.getAllPosts();
        
        // 의료기관 더미데이터 추가
        List<ItemList> medicalInstitutions = itemListService.getAllItems();

        model.addAttribute("topCompanies", topCompanies);
        model.addAttribute("posts", communityPosts);
        model.addAttribute("medicalInstitutions", medicalInstitutions);

        return "main";
    }




    // Tourism List 페이지 HTML 서빙
    @GetMapping("/tourism_list")
    public String tourismList() {
        return "tourism_list";
    }

}