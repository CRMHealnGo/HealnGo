package com.example.ApiRound.crm.yoyo.adminManage;

import com.example.ApiRound.crm.yoyo.medi.MediServiceRepository;
import com.example.ApiRound.crm.yoyo.medi.MediServiceEntity;
import com.example.ApiRound.entity.ItemList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminManageService {

    private final AdminManageRepository adminManageRepository;
    private final MediServiceRepository mediServiceRepository;

    /**
     * 업체 관리 통계 데이터 조회
     */
    public Map<String, Object> getCompanyStats() {
        log.info("====== AdminManageService.getCompanyStats 호출 ======");

        try {
            // 이번 달 시작일 계산
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

            // 각 통계 데이터 조회
            long totalCompanies = adminManageRepository.countApprovedCompanies();
            long newThisMonth = adminManageRepository.countNewCompaniesThisMonth(startOfMonth);
            long reportsReceived = adminManageRepository.countReportedCompanies();
            long underSanction = adminManageRepository.countSuspendedCompanies();

            log.info("통계 데이터 조회 완료:");
            log.info("  - 전체 업체: {}", totalCompanies);
            log.info("  - 이번 달 신규: {}", newThisMonth);
            log.info("  - 신고 접수: {}", reportsReceived);
            log.info("  - 제재 중: {}", underSanction);

            // 결과 맵 생성
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCompanies", totalCompanies);
            stats.put("newThisMonth", newThisMonth);
            stats.put("reportsReceived", reportsReceived);
            stats.put("underSanction", underSanction);

            return stats;

        } catch (Exception e) {
            log.error("통계 데이터 조회 중 오류 발생: ", e);

            // 오류 시 기본값 반환
            Map<String, Object> defaultStats = new HashMap<>();
            defaultStats.put("totalCompanies", 0L);
            defaultStats.put("newThisMonth", 0L);
            defaultStats.put("reportsReceived", 0L);
            defaultStats.put("underSanction", 0L);

            return defaultStats;
        }
    }

    /**
     * 승인된 업체 목록 조회
     */
    public List<ItemList> getApprovedCompanies() {
        log.info("====== AdminManageService.getApprovedCompanies 호출 ======");

        try {
            List<ItemList> companies = adminManageRepository.findApprovedCompanies();
            log.info("승인된 업체 목록 조회 완료: {}개", companies.size());

            // 각 업체 정보 로깅
            for (ItemList company : companies) {
                log.info("  - 업체 ID: {}, 이름: {}, 회사명: {}",
                        company.getId(),
                        company.getName(),
                        company.getOwnerCompany() != null ? company.getOwnerCompany().getCompanyName() : "N/A");
            }

            return companies;

        } catch (Exception e) {
            log.error("업체 목록 조회 중 오류 발생: ", e);
            return List.of(); // 빈 리스트 반환
        }
    }

    /**
     * 업체명으로 검색
     */
    public List<ItemList> searchCompanies(String searchTerm) {
        log.info("====== AdminManageService.searchCompanies 호출 ======");
        log.info("검색어: {}", searchTerm);

        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getApprovedCompanies();
            }

            List<ItemList> companies = adminManageRepository.searchCompaniesByName(searchTerm.trim());
            log.info("검색 결과: {}개 업체 발견", companies.size());

            return companies;

        } catch (Exception e) {
            log.error("업체 검색 중 오류 발생: ", e);
            return List.of(); // 빈 리스트 반환
        }
    }

    /**
     * 상태별 업체 조회
     */
    public List<ItemList> getCompaniesByStatus(String status) {
        log.info("====== AdminManageService.getCompaniesByStatus 호출 ======");
        log.info("상태: {}", status);

        try {
            List<ItemList> companies = adminManageRepository.findCompaniesByStatus(status);
            log.info("상태별 업체 조회 완료: {}개", companies.size());

            return companies;

        } catch (Exception e) {
            log.error("상태별 업체 조회 중 오류 발생: ", e);
            return List.of(); // 빈 리스트 반환
        }
    }

    /**
     * 업체 상세 정보 조회
     */
    public ItemList getCompanyById(Long companyId) {
        log.info("====== AdminManageService.getCompanyById 호출 ======");
        log.info("업체 ID: {}", companyId);

        try {
            ItemList company = adminManageRepository.findById(companyId).orElse(null);

            if (company != null) {
                log.info("업체 정보 조회 성공: {}", company.getName());
            } else {
                log.warn("업체를 찾을 수 없습니다: {}", companyId);
            }

            return company;

        } catch (Exception e) {
            log.error("업체 상세 조회 중 오류 발생: ", e);
            return null;
        }
    }

    /**
     * 업체별 의료 서비스 목록 조회 (관리자용 - 삭제된 것 포함)
     */
    public List<MediServiceEntity> getMedicalServicesByCompanyId(Integer companyId) {
        log.info("====== AdminManageService.getMedicalServicesByCompanyId 호출 ======");
        log.info("Company ID: {}", companyId);

        try {
            // 삭제된 서비스 포함 전체 조회 (관리자용)
            List<MediServiceEntity> services = mediServiceRepository.findAllByCompanyIdWithFetch(companyId);
            log.info("의료 서비스 조회 완료: {}개 (삭제된 것 포함)", services.size());

            // 각 서비스 정보 로깅
            int activeCount = 0;
            int deletedCount = 0;

            for (MediServiceEntity service : services) {
                boolean isDeleted = service.getDeletedAt() != null;
                String status = isDeleted ? "[삭제됨]" : "[활성]";

                log.info("  - {} 서비스 ID: {}, 이름: {}, 가격: {}",
                        status,
                        service.getServiceId(),
                        service.getName(),
                        service.getPrice());

                if (isDeleted) {
                    deletedCount++;
                } else {
                    activeCount++;
                }
            }

            log.info("통계: 활성 {}개, 삭제됨 {}개, 전체 {}개", activeCount, deletedCount, services.size());

            return services;

        } catch (Exception e) {
            log.error("의료 서비스 조회 중 오류 발생: ", e);
            return List.of(); // 빈 리스트 반환
        }
    }
}
