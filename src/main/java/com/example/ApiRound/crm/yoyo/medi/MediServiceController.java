package com.example.ApiRound.crm.yoyo.medi;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class MediServiceController {

    private final MediServiceService mediServiceService;

    /**
     * 의료 서비스 관리 페이지
     */
    @GetMapping("/medical-services")
    public String medicalServices(HttpSession session, Model model) {
        // 세션에서 companyId 가져오기
        Integer companyId = (Integer) session.getAttribute("companyId");
        
        // 회사별 의료 서비스 조회
        List<MediServiceEntity> medicalServices = List.of();
        if (companyId != null) {
            medicalServices = mediServiceService.findByCompanyId(companyId);
        }
        
        model.addAttribute("medicalServices", medicalServices);
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyAddress", session.getAttribute("companyAddress"));
        model.addAttribute("sidebarType", "company");
        
        return "crm/company_medical_services";
    }

    /** 전체 조회 API */
    @GetMapping("/api/medical-services")
    public ResponseEntity<List<MediServiceEntity>> getAll() {
        return ResponseEntity.ok(mediServiceService.findAll());
    }

    /** 단일 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<MediServiceEntity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mediServiceService.findById(id));
    }

    /** 등록 (itemId를 path variable로 받는 구조) */
    @PostMapping("/api/medical-services/item/{itemId}")
    public ResponseEntity<MediServiceEntity> create(
            @PathVariable Long itemId,
            @RequestBody MediServiceEntity entity
    ) {
        return ResponseEntity.ok(mediServiceService.create(itemId, entity));
    }

    /** 수정 */
    @PutMapping("/api/medical-services/{id}")
    public ResponseEntity<MediServiceEntity> update(
            @PathVariable Long id,
            @RequestBody MediServiceEntity entity
    ) {
        return ResponseEntity.ok(mediServiceService.update(id, entity));
    }

    /** 삭제 */
    @DeleteMapping("/api/medical-services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
