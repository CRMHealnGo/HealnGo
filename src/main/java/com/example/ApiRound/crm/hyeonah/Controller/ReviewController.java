package com.example.ApiRound.crm.hyeonah.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewController {

    @GetMapping("/review")
    public String review() {
        return "crm/review";
    }

    @GetMapping("/company/review")
    public String companyReview(Model model) {
        model.addAttribute("sidebarType", "company");
        return "crm/review";
    }
}
