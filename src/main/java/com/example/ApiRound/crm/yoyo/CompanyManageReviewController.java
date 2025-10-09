package com.example.ApiRound.crm.yoyo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
public class CompanyManageReviewController {

    @GetMapping("/review")
    public String companyReview(Model model) {
        // 리뷰 통계 데이터 (실제로는 서비스에서 가져와야 함)
        model.addAttribute("totalReviews", 26);
        model.addAttribute("pendingReplies", 3);
        model.addAttribute("completedReplies", 16);
        model.addAttribute("averageRating", 4.7);
        
        return "crm/company_review";
    }
}
