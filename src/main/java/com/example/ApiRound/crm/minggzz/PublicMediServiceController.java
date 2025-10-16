package com.example.ApiRound.crm.minggzz;

import com.example.ApiRound.crm.yoyo.medi.MediServiceEntity;
import com.example.ApiRound.crm.yoyo.medi.MediServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
public class PublicMediServiceController {

    private final MediServiceRepository mediServiceRepository;

    /** 선택한 itemId의 서비스 목록 조회 (공개, 세션 불필요) */
    @GetMapping("/{itemId}/services")
    public ResponseEntity<List<MediServiceDto>> getServicesByItem(@PathVariable Long itemId) {
        List<MediServiceDto> list = mediServiceRepository
                .findAllByItem_IdOrderByServiceIdDesc(itemId)
                .stream()
                .map(MediServiceDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    /** 공개 응답용 DTO (엔티티의 지연 로딩 객체를 배제) */
    public static class MediServiceDto {
        public Long serviceId;
        public String name;
        public String description;
        public String serviceCategory;
        public BigDecimal price;
        public String currency;
        public String tags;
        public String genderTarget;   // enum -> 문자열
        public String targetCountry;  // enum -> 문자열
        public LocalDate startDate;
        public LocalDate endDate;

        public static MediServiceDto from(MediServiceEntity e) {
            MediServiceDto d = new MediServiceDto();
            d.serviceId = e.getServiceId();
            d.name = e.getName();
            d.description = e.getDescription();
            d.serviceCategory = e.getServiceCategory();
            d.price = e.getPrice();
            d.currency = e.getCurrency();
            d.tags = e.getTags();
            d.genderTarget = (e.getGenderTarget() != null ? e.getGenderTarget().name() : null);
            d.targetCountry = (e.getTargetCountry() != null ? e.getTargetCountry().name() : null);
            d.startDate = e.getStartDate();
            d.endDate = e.getEndDate();
            return d;
        }
    }
}
