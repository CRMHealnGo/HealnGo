package com.example.ApiRound.crm.yoyo.medi;

import com.example.ApiRound.entity.ItemList;
import com.example.ApiRound.repository.ItemListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MediServiceService {

    private final MediServiceRepository mediServiceRepository;
    private final com.example.ApiRound.repository.ItemListRepository itemListRepository;

    /** 전체 조회 */
    public List<MediServiceEntity> findAll() {
        return mediServiceRepository.findAll();
    }

    /** 단일 조회 */
    public MediServiceEntity findById(Long id) {
        return mediServiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("서비스를 찾을 수 없습니다. ID: " + id));
    }

    /** 등록 */
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
        return mediServiceRepository.findByItem_OwnerCompany_CompanyId(companyId);
    }
}
