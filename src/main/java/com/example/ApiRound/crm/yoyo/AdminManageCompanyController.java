package com.example.ApiRound.crm.yoyo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminManageCompanyController {

    @GetMapping("/companies")
    public String companies(Model model) {
        // 업체 관리 페이지 데이터 설정
        model.addAttribute("totalCompanies", 326);
        model.addAttribute("newThisMonth", 13);
        model.addAttribute("reportsReceived", 6);
        model.addAttribute("underSanction", 2);
        
        return "admin/admin_manage_company";
    }


}
