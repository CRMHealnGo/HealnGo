package com.example.ApiRound.crm.yoyo.medi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-services")
@RequiredArgsConstructor
public class MediServiceController {

    private final MediServiceService mediServiceService;

    /** 전체 조회 */
    @GetMapping
    public ResponseEntity<List<MediServiceEntity>> getAll() {
        return ResponseEntity.ok(mediServiceService.findAll());
    }

    /** 단일 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<MediServiceEntity> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mediServiceService.findById(id));
    }

    /** 등록 (itemId를 path variable로 받는 구조) */
    @PostMapping("/item/{itemId}")
    public ResponseEntity<MediServiceEntity> create(
            @PathVariable Long itemId,
            @RequestBody MediServiceEntity entity
    ) {
        return ResponseEntity.ok(mediServiceService.create(itemId, entity));
    }

    /** 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<MediServiceEntity> update(
            @PathVariable Long id,
            @RequestBody MediServiceEntity entity
    ) {
        return ResponseEntity.ok(mediServiceService.update(id, entity));
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
