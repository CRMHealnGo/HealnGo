package com.example.ApiRound.crm.yoyo.medi;

import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;
import com.example.ApiRound.crm.hyeonah.Service.CompanyUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediServiceService {

    private final MediServiceRepository mediServiceRepository;
    private final com.example.ApiRound.repository.ItemListRepository itemListRepository;
    private final CompanyUserService companyUserService;

    /** 전체 조회 */
    public List<MediServiceEntity> findAll() {
        return mediServiceRepository.findAll();
    }

    /** 단일 조회 */
    public MediServiceEntity findById(Long id) {
        return mediServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("서비스를 찾을 수 없습니다. ID: " + id));
    }

    /** 등록 (itemId로 지정) */
    @Transactional
    public MediServiceEntity create(Long itemId, MediServiceEntity entity) {
        ItemList item = itemListRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 Item ID: " + itemId));
        entity.setItem(item);
        return mediServiceRepository.save(entity);
    }

    /** 수정 */
    public MediServiceEntity update(Long id, MediServiceEntity updatedEntity) {
        MediServiceEntity existing = findById(id);
        existing.setName(updatedEntity.getName());
        existing.setDescription(updatedEntity.getDescription());
        existing.setStartDate(updatedEntity.getStartDate());
        existing.setEndDate(updatedEntity.getEndDate());
        existing.setGenderTarget(updatedEntity.getGenderTarget());
        existing.setTags(updatedEntity.getTags());
        existing.setTargetCountry(updatedEntity.getTargetCountry());
        existing.setServiceCategory(updatedEntity.getServiceCategory());
        existing.setPrice(updatedEntity.getPrice());
        existing.setVatIncluded(updatedEntity.getVatIncluded());
        existing.setCurrency(updatedEntity.getCurrency());
        existing.setDiscountRate(updatedEntity.getDiscountRate());
        existing.setIsRefundable(updatedEntity.getIsRefundable());
        return mediServiceRepository.save(existing);
    }

    /** 삭제 */
    public void delete(Long id) {
        mediServiceRepository.deleteById(id);
    }

    /** 회사별 의료 서비스 조회 */
    public List<MediServiceEntity> findByCompanyId(Integer companyId) {
        log.info("====== MediServiceService.findByCompanyId 호출 ======");
        log.info("조회할 companyId: {}", companyId);

        List<MediServiceEntity> result = mediServiceRepository.findAllByCompanyIdWithFetch(companyId);

        log.info("Repository에서 조회된 결과 개수: {}", result.size());
        if (result.isEmpty()) {
            log.warn("⚠️ 해당 companyId({})로 조회된 의료 서비스가 없습니다!", companyId);
        } else {
            log.info("✅ 조회 성공! 서비스 목록:");
            for (MediServiceEntity entity : result) {
                log.info("  - ID: {}, 이름: {}, 가격: {}, Item ID: {}",
                        entity.getServiceId(),
                        entity.getName(),
                        entity.getPrice(),
                        entity.getItem() != null ? entity.getItem().getId() : "null");
                if (entity.getItem() != null && entity.getItem().getOwnerCompany() != null) {
                    log.info("    -> 소유 회사 ID: {}, 회사명: {}",
                            entity.getItem().getOwnerCompany().getCompanyId(),
                            entity.getItem().getOwnerCompany().getCompanyName());
                }
            }
        }
        return result;
    }

    /** 회사 ID로 의료 서비스 등록 (세션 기반 등록에서 사용) */
    @Transactional
    public MediServiceEntity createByCompanyId(Integer companyId, MediServiceEntity entity) {
        log.info("====== MediServiceService.createByCompanyId 호출 ======");
        log.info("회사 ID: {}", companyId);

        // 1) 회사 정보 조회
        CompanyUser company = companyUserService.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회사 ID: " + companyId));
        log.info("회사명: {}", company.getCompanyName());

        // 2) 해당 회사의 대표 item(또는 첫번째 item) 찾기
        //    기존 코드: findByOwnerCompany_CompanyId(companyId) → ❌ 없는 메서드
        //    안전한 메서드로 교체: findFirstByOwnerCompany_CompanyIdOrderByIdAsc(companyId)
        Optional<ItemList> itemOpt = itemListRepository.findFirstByOwnerCompany_CompanyIdOrderByIdAsc(companyId);

        ItemList item;
        if (itemOpt.isPresent()) {
            item = itemOpt.get();
            log.info("기존 item 사용: ID={}, 이름={}", item.getId(), item.getName());
        } else {
            // 3) item이 없으면 새로 생성
            item = new ItemList();
            item.setName(company.getCompanyName() + " 서비스");
            item.setRegion("서울"); // 필요 시 실제 지역으로 교체
            item.setAddress(company.getAddress());
            item.setPhone(company.getPhone());
            item.setCategory("의료서비스");
            item.setOwnerCompany(company);
            item = itemListRepository.save(item);
            log.info("새로운 item 생성: ID={}, 이름={}", item.getId(), item.getName());
        }

        // 4) 서비스 저장
        entity.setItem(item);
        MediServiceEntity savedEntity = mediServiceRepository.save(entity);

        log.info("의료 서비스 등록 완료: ID={}, 이름={}", savedEntity.getServiceId(), savedEntity.getName());
        return savedEntity;
    }
}
