package com.example.ApiRound.crm.yoyo.adminManage;

import com.example.ApiRound.crm.yoyo.adminManage.AdminManageService;
import com.example.ApiRound.entity.ItemList;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminManageController {

    private final AdminManageService adminManageService;

    @GetMapping("/companies")
    public String companies(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            Model model, 
            HttpSession session) {
        
        log.info("====== AdminManageController.companies 호출 ======");
        log.info("검색어: {}, 상태: {}", search, status);
        
        try {
            // 관리자 정보 추가
            model.addAttribute("managerName", session.getAttribute("managerName"));
            
            // 통계 데이터 조회
            Map<String, Object> stats = adminManageService.getCompanyStats();
            model.addAttribute("totalCompanies", stats.get("totalCompanies"));
            model.addAttribute("newThisMonth", stats.get("newThisMonth"));
            model.addAttribute("reportsReceived", stats.get("reportsReceived"));
            model.addAttribute("underSanction", stats.get("underSanction"));
            
            log.info("통계 데이터 설정 완료: {}", stats);
            
            // 업체 목록 조회
            List<ItemList> companies;
            if (search != null && !search.trim().isEmpty()) {
                companies = adminManageService.searchCompanies(search);
                model.addAttribute("searchTerm", search);
                log.info("검색 결과: {}개 업체", companies.size());
            } else if (status != null && !status.trim().isEmpty()) {
                companies = adminManageService.getCompaniesByStatus(status);
                model.addAttribute("selectedStatus", status);
                log.info("상태별 조회 결과: {}개 업체", companies.size());
            } else {
                companies = adminManageService.getApprovedCompanies();
                log.info("전체 업체 조회 결과: {}개 업체", companies.size());
            }
            
            model.addAttribute("companies", companies);
            
            // 각 업체별 의료 서비스 개수 조회
            for (ItemList company : companies) {
                Integer companyId = company.getOwnerCompany() != null ? company.getOwnerCompany().getCompanyId() : null;
                if (companyId != null) {
                    int serviceCount = adminManageService.getMedicalServiceCount(companyId);
                    model.addAttribute("serviceCount_" + companyId, serviceCount);
                    log.info("업체 {}의 의료 서비스 개수: {}", companyId, serviceCount);
                }
            }
            
            // 검색 및 필터 정보
            model.addAttribute("search", search);
            model.addAttribute("status", status);
            
            log.info("업체 관리 페이지 데이터 설정 완료");
            return "admin/admin_manage_company";
            
        } catch (Exception e) {
            log.error("업체 관리 페이지 로드 중 오류 발생: ", e);
            
            // 오류 시 기본값으로 설정
            model.addAttribute("totalCompanies", 0);
            model.addAttribute("newThisMonth", 0);
            model.addAttribute("reportsReceived", 0);
            model.addAttribute("underSanction", 0);
            model.addAttribute("companies", List.of());
            model.addAttribute("search", search);
            model.addAttribute("status", status);
            model.addAttribute("managerName", session.getAttribute("managerName"));
            
            return "admin/admin_manage_company";
        }
    }
    
    /**
     * 업체별 의료 서비스 목록 조회 API
     */
    @GetMapping("/api/companies/{companyId}/medical-services")
    @ResponseBody
    public ResponseEntity<?> getCompanyMedicalServices(@PathVariable Integer companyId) {
        log.info("====== AdminManageController.getCompanyMedicalServices 호출 ======");
        log.info("Company ID: {}", companyId);
        
        try {
            List<?> services = adminManageService.getMedicalServicesByCompanyId(companyId);
            log.info("의료 서비스 조회 완료: {}개", services.size());
            
            return ResponseEntity.ok(services);
            
        } catch (Exception e) {
            log.error("의료 서비스 조회 중 오류 발생: ", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "서비스 조회 중 오류가 발생했습니다.",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 업체 승인 처리 API
     */
    @PostMapping("/api/companies/{companyId}/approve")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> approveCompany(@PathVariable Integer companyId) {
        log.info("====== AdminManageController.approveCompany 호출 ======");
        log.info("승인할 업체 ID: {}", companyId);
        
        try {
            boolean success = adminManageService.approveCompany(companyId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", "업체가 성공적으로 승인되었습니다.");
                response.put("companyId", companyId);
                log.info("✅ 업체 승인 API 성공: {}", companyId);
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "업체 승인에 실패했습니다. 업체를 찾을 수 없거나 이미 승인되었을 수 있습니다.");
                response.put("companyId", companyId);
                log.warn("⚠️ 업체 승인 API 실패: {}", companyId);
                
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("❌ 업체 승인 API 오류 발생: ", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            errorResponse.put("companyId", companyId);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}