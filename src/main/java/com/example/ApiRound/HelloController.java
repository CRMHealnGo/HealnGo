package com.example.ApiRound;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ApiRound.Service.ClickLogService;
import com.example.ApiRound.Service.CommunityPostService;
import com.example.ApiRound.entity.CommunityPost;

@Controller
public class HelloController {

    private final ClickLogService clickLogService;
    private final CommunityPostService communityPostService;

    public HelloController(ClickLogService clickLogService, CommunityPostService communityPostService) {
        this.clickLogService = clickLogService;
        this.communityPostService = communityPostService;
    }
    @GetMapping("/main")
    public String main(Model model) {
        List<Object[]> topCompanies = clickLogService.getTop3CompaniesLast7Days();
        List<CommunityPost> communityPosts = communityPostService.getAllPosts();

        model.addAttribute("topCompanies", topCompanies);
        model.addAttribute("posts", communityPosts);

        return "main";
    }

    @GetMapping("/hello")
    public String hello(Model model) {
        model.addAttribute("message", "JSP 테스트 성공!");
        return "hello"; // /WEB-INF/views/hello.jsp로 연결됨
    }

    @GetMapping("/plastic-surgery")
    public String surgery(Model model) {
        return "plastic-surgery"; // /WEB-INF/views/plastic-surgery.jsp로 연결됨
    }

    @GetMapping("/skincare")
    public String skincare(Model model) {
        return "skincare"; // /WEB-INF/views/skincare.jsp로 연결됨
    }

    @GetMapping("/location")
    public String location() {
        return "location";
    }

    @GetMapping("/service/detail/{serviceId}")
    public String serviceDetail(@PathVariable Long serviceId, Model model) {
        model.addAttribute("serviceId", serviceId);
        return "service_detail";
    }

}