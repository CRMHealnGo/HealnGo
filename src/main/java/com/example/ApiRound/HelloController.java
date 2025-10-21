package com.example.ApiRound;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ApiRound.Service.ClickLogService;
import com.example.ApiRound.Service.CommunityPostService;
import com.example.ApiRound.crm.hyeonah.notice.Notice;
import com.example.ApiRound.crm.hyeonah.notice.NoticeService;
import com.example.ApiRound.crm.hyeonah.review.UserReviewDto;
import com.example.ApiRound.crm.hyeonah.review.UserReviewService;
import com.example.ApiRound.entity.CommunityPost;

@Controller
public class HelloController {

    private final ClickLogService clickLogService;
    private final CommunityPostService communityPostService;
    private final NoticeService noticeService;
    private final UserReviewService userReviewService;

    public HelloController(ClickLogService clickLogService, CommunityPostService communityPostService, 
                          NoticeService noticeService, UserReviewService userReviewService) {
        this.clickLogService = clickLogService;
        this.communityPostService = communityPostService;
        this.noticeService = noticeService;
        this.userReviewService = userReviewService;
    }
    @GetMapping("/main")
    public String main(Model model) {
        List<Object[]> topCompanies = clickLogService.getTop3CompaniesLast7Days();
        List<CommunityPost> communityPosts = communityPostService.getAllPosts();
        
        // 최신 공지사항 5개 가져오기
        Pageable pageable = PageRequest.of(0, 5);
        List<Notice> recentNotices = noticeService.getPublishedNotices(pageable).getContent();

        model.addAttribute("topCompanies", topCompanies);
        model.addAttribute("posts", communityPosts);
        model.addAttribute("notices", recentNotices);

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
        
        // 리뷰 목록 조회 (serviceId 기반)
        List<UserReviewDto> reviews = userReviewService.getReviewsByServiceId(serviceId);
        model.addAttribute("reviews", reviews);
        
        // 평균 평점 조회
        Double averageRating = userReviewService.getAverageRatingByServiceId(serviceId);
        model.addAttribute("averageRating", averageRating);
        
        // 리뷰 개수 조회
        Long reviewCount = userReviewService.getReviewCountByServiceId(serviceId);
        model.addAttribute("reviewCount", reviewCount);
        
        return "service_detail";
    }

}