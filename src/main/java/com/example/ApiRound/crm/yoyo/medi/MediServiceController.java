package com.example.ApiRound.crm.yoyo.medi;

import java.util.List;
import java.util.Optional;

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

import com.example.ApiRound.crm.hyeonah.Service.CompanyUserService;
import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class MediServiceController {

    private final MediServiceService mediServiceService;
    private final CompanyUserService companyUserService;

    /**
     * 의료 서비스 관리 페이지
     */
    @GetMapping("/medical-services")
    public String medicalServices(HttpSession session, Model model) {
        log.info("========== 의료 서비스 페이지 요청 시작 ==========");
        
        // 세션에서 companyId 가져오기
        Integer companyId = (Integer) session.getAttribute("companyId");
        log.info("세션에서 가져온 companyId: {}", companyId);
        log.info("세션의 companyName: {}", session.getAttribute("companyName"));
        log.info("세션의 companyAddress: {}", session.getAttribute("companyAddress"));
        
        // 회사별 의료 서비스 조회
        List<MediServiceEntity> medicalServices = List.of();
        if (companyId != null) {
            log.info("companyId가 존재함. 서비스 조회 시작...");
            medicalServices = mediServiceService.findByCompanyId(companyId);
            log.info("서비스 조회 완료. 조회된 서비스 개수: {}", medicalServices.size());
            
            // 각 서비스 상세 로그
            for (int i = 0; i < medicalServices.size(); i++) {
                MediServiceEntity service = medicalServices.get(i);
                log.info("서비스[{}]: ID={}, 이름={}, 가격={}, 카테고리={}", 
                    i, service.getServiceId(), service.getName(), service.getPrice(), service.getServiceCategory());
            }
        } else {
            log.warn("세션에 companyId가 없습니다! 서비스를 조회할 수 없습니다.");
        }
        
        model.addAttribute("medicalServices", medicalServices);
        model.addAttribute("companyId", companyId);
        model.addAttribute("companyName", session.getAttribute("companyName"));
        model.addAttribute("companyAddress", session.getAttribute("companyAddress"));
        model.addAttribute("sidebarType", "company");
        addAvatarInfo(model, companyId);
        
        log.info("모델에 추가된 medicalServices 크기: {}", medicalServices.size());
        log.info("========== 의료 서비스 페이지 요청 완료 ==========");
        
        return "crm/company_medical_services";
    }
    
    /**
     * 아바타 정보 추가 헬퍼 메서드
     */
    private void addAvatarInfo(Model model, Integer companyId) {
        if (companyId != null) {
            Optional<CompanyUser> companyOpt = companyUserService.findById(companyId);
            boolean hasAvatar = companyOpt.isPresent() && 
                               companyOpt.get().getAvatarBlob() != null && 
                               companyOpt.get().getAvatarBlob().length > 0;
            model.addAttribute("hasAvatar", hasAvatar);
        } else {
            model.addAttribute("hasAvatar", false);
        }
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

    /** 등록 (세션에서 companyId를 가져와서 해당 회사의 item 찾기) */
    @PostMapping("/api/medical-services")
    public ResponseEntity<MediServiceEntity> createWithSession(
            @RequestBody MediServiceEntity entity,
            HttpSession session
    ) {
        try {
            log.info("========== 의료 서비스 등록 요청 시작 ==========");
            log.info("받은 데이터: {}", entity);
            
            Integer companyId = (Integer) session.getAttribute("companyId");
            log.info("세션에서 가져온 companyId: {}", companyId);
            
            if (companyId == null) {
                log.warn("세션에 companyId가 없습니다!");
                return ResponseEntity.status(401).body(null);
            }
            
            MediServiceEntity result = mediServiceService.createByCompanyId(companyId, entity);
            log.info("의료 서비스 등록 성공: {}", result.getServiceId());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("의료 서비스 등록 중 오류 발생: ", e);
            return ResponseEntity.status(400).body(null);
        }
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
