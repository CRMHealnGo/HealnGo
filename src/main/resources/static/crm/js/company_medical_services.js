// ===================== 사이드바 활성 표시 =====================
document.addEventListener('DOMContentLoaded', async () => {
  const allNavLinks = document.querySelectorAll('.nav-link');
  allNavLinks.forEach(link => link.classList.remove('active'));
  const medicalServiceLink = document.querySelector('a[href="/company/medical-services"]');
  if (medicalServiceLink) medicalServiceLink.classList.add('active');

});

// ===================== CSRF/메타/유틸 =====================
function getMeta(name) {
  const el = document.querySelector(`meta[name="${name}"]`);
  return el ? el.content : null;
}
function getHeaders() {
  const headers = { "Content-Type": "application/json" };
  const csrfToken = getMeta("_csrf");
  const csrfHeader = getMeta("_csrf_header");
  if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
  return headers;
}
function getCompanyId() {
  return getMeta("companyId") || window.companyIdFromServer || null;
}

// ===================== API =====================
// (새 API) 아이템 + 서비스 LEFT JOIN 결과
async function fetchCompanyItemsWithServices() {
  const companyId = getCompanyId();
  if (!companyId) return [];
  const res = await fetch(`/company/medical-services/api/company/${companyId}/with-items`, {
    method: "GET"
  });
  if (!res.ok) throw new Error(`[${res.status}] 조회 실패`);
  return res.json();
}

// 기존: 서비스만 목록
async function fetchMediList() {
  const companyId = getCompanyId();
  if (!companyId) return [];
  const res = await fetch(`/company/medical-services/api/company/${companyId}`, { method: "GET" });
  if (!res.ok) throw new Error(`[${res.status}] 목록 조회 실패`);
  return res.json();
}

// ===================== 렌더 =====================
function renderList(rows) {
  const wrap = document.getElementById('listWrap');
  wrap.innerHTML = '';

  if (!rows || rows.length === 0) {
    wrap.innerHTML = `<div class="card"><p class="muted">등록된 지점/서비스가 없습니다.</p></div>`;
    return;
  }

  // itemId 기준으로 묶기
  const byItem = new Map();
  rows.forEach(r => {
    const key = r.itemId;
    if (!byItem.has(key)) {
      byItem.set(key, { itemId: r.itemId, itemName: r.itemName, services: [] });
    }
    if (r.serviceId) {
      byItem.get(key).services.push({
        serviceId: r.serviceId,
        serviceName: r.serviceName,
        price: r.price,
        currency: r.currency,
        updatedAt: r.updatedAt
      });
    }
  });

  byItem.forEach(group => {
    const card = document.createElement('div');
    card.className = 'card';

    const header = document.createElement('div');
    header.className = 'item-header';
    header.innerHTML = `
      <div>
        <strong>${escapeHtml(group.itemName || '(무명 지점)')}</strong>
        <span class="muted"> #${group.itemId}</span>
      </div>
      <div>
        <button class="btn-outline" onclick="openCreate(${group.itemId})">
          <i class="fa fa-plus"></i> 서비스 추가
        </button>
      </div>
    `;
    card.appendChild(header);

    const body = document.createElement('div');
    if (group.services.length === 0) {
      body.innerHTML = `<span class="muted">등록된 서비스가 없습니다.</span>`;
    } else {
      group.services.forEach(s => {
        const pill = document.createElement('div');
        pill.className = 'service-pill';
        const priceStr = (s.price != null ? Number(s.price).toLocaleString() : '-');
        pill.innerHTML = `
          <strong>${escapeHtml(s.serviceName || '(무명 서비스)')}</strong>
          <span class="muted"> · ${priceStr}${s.currency ? ' ' + s.currency : ''}</span>
        `;
        pill.onclick = () => openDetail(s.serviceId);
        body.appendChild(pill);
      });
    }
    card.appendChild(body);
    wrap.appendChild(card);
  });
}

    // 모달 태그 입력 기능 초기화
    initializeEditModal();
});

// 수정 모달 초기화
function initializeEditModal() {
    const tagInput = document.getElementById('editTagInput');
    const tagsContainer = document.getElementById('editTagsContainer');

    if (tagInput && tagsContainer) {
        tagInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                const tagValue = this.value.trim();
                if (tagValue && getEditTags().length < 5) {
                    addEditTag(tagValue);
                    this.value = '';
                }
            }
        });
    }
}

// 버튼에서 데이터 읽어서 모달 열기
function openEditModalFromButton(button) {
    const serviceData = {
        serviceId: button.dataset.serviceId,
        name: button.dataset.name,
        startDate: button.dataset.startDate,
        endDate: button.dataset.endDate,
        price: button.dataset.price,
        genderTarget: button.dataset.genderTarget,
        targetCountry: button.dataset.targetCountry,
        vatIncluded: button.dataset.vatIncluded === 'true',
        isRefundable: button.dataset.refundable === 'true',
        tags: button.dataset.tags
    };
    openEditModal(serviceData);
}

// 수정 모달 열기
function openEditModal(serviceData) {
    const modal = document.getElementById('editModal');
    if (!modal) return;

    // 폼 데이터 채우기
    document.getElementById('editName').value = serviceData.name || '';
    document.getElementById('editStartDate').value = serviceData.startDate || '';
    document.getElementById('editEndDate').value = serviceData.endDate || '';
    document.getElementById('editPrice').value = serviceData.price || 0;

    // 성별 라디오 버튼 설정
    const genderValue = serviceData.genderTarget || 'ALL';
    document.querySelector(`input[name="editGenderTarget"][value="${genderValue}"]`).checked = true;

    // 국가 라디오 버튼 설정
    const countryValue = serviceData.targetCountry || 'KOR';
    document.querySelector(`input[name="editTargetCountry"][value="${countryValue}"]`).checked = true;

    // VAT 라디오 버튼 설정
    const vatValue = serviceData.vatIncluded ? 'true' : 'false';
    document.querySelector(`input[name="editVatIncluded"][value="${vatValue}"]`).checked = true;

    // 환불 가능 여부 라디오 버튼 설정
    const refundValue = serviceData.isRefundable ? 'true' : 'false';
    document.querySelector(`input[name="editIsRefundable"][value="${refundValue}"]`).checked = true;

    // 태그 설정
    clearEditTags();
    if (serviceData.tags) {
        const tags = serviceData.tags.split(',').filter(tag => tag.trim());
        tags.forEach(tag => addEditTag(tag.trim()));
    }

    // 모달 표시
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';

    // 현재 편집 중인 서비스 ID 저장
    modal.dataset.serviceId = serviceData.serviceId;
}

// 수정 모달 닫기
function closeEditModal() {
    const modal = document.getElementById('editModal');
    if (!modal) return;

    modal.style.display = 'none';
    document.body.style.overflow = 'auto';

    // 폼 초기화
    document.getElementById('editForm').reset();
    clearEditTags();
    modal.dataset.serviceId = '';
}

// 태그 추가 (수정 모달용)
function addEditTag(tagValue) {
    const tagsContainer = document.getElementById('editTagsContainer');
    const existingTags = getEditTags();

    if (existingTags.length >= 5) {
        alert('최대 5개까지 태그를 추가할 수 있습니다.');
        return;
    }

    if (existingTags.includes(tagValue)) {
        alert('이미 추가된 태그입니다.');
        return;
    }

    const tagElement = document.createElement('span');
    tagElement.className = 'tag';
    tagElement.innerHTML = `${tagValue} <span class="tag-remove" onclick="removeEditTag(this)">×</span>`;

    // 입력 필드 앞에 삽입
    const tagInput = document.getElementById('editTagInput');
    tagsContainer.insertBefore(tagElement, tagInput);
}

// 태그 제거 (수정 모달용)
function removeEditTag(element) {
    element.parentElement.remove();
}

// 모든 태그 제거 (수정 모달용)
function clearEditTags() {
    const tagsContainer = document.getElementById('editTagsContainer');
    const tags = tagsContainer.querySelectorAll('.tag');
    tags.forEach(tag => tag.remove());
}

// 현재 태그들 가져오기 (수정 모달용)
function getEditTags() {
    const tagsContainer = document.getElementById('editTagsContainer');
    const tags = tagsContainer.querySelectorAll('.tag');
    return Array.from(tags).map(tag => tag.textContent.trim().replace('×', '').trim());
}

// 수정된 서비스 저장
function saveEditedService() {
    const modal = document.getElementById('editModal');
    const serviceId = modal.dataset.serviceId;

    if (!serviceId) {
        alert('서비스 ID를 찾을 수 없습니다.');
        return;
    }

    // 폼 데이터 수집
    const formData = {
        name: document.getElementById('editName').value.trim(),
        startDate: document.getElementById('editStartDate').value || null,
        endDate: document.getElementById('editEndDate').value || null,
        price: parseFloat(document.getElementById('editPrice').value || '0'),
        genderTarget: document.querySelector('input[name="editGenderTarget"]:checked')?.value || 'ALL',
        targetCountry: document.querySelector('input[name="editTargetCountry"]:checked')?.value || 'KOR',
        vatIncluded: document.querySelector('input[name="editVatIncluded"]:checked')?.value === 'true',
        isRefundable: document.querySelector('input[name="editIsRefundable"]:checked')?.value === 'true',
        tags: getEditTags().join(',') || '',
        serviceCategory: ''  // 빈 문자열로 설정
    };

    // 유효성 검사
    if (!formData.name) {
        alert('서비스 제목을 입력해주세요.');
        document.getElementById('editName').focus();
        return;
    }

    if (!formData.startDate || !formData.endDate) {
        alert('서비스 기간을 입력해주세요.');
        return;
    }

    if (new Date(formData.startDate) > new Date(formData.endDate)) {
        alert('시작일은 종료일보다 이전이어야 합니다.');
        return;
    }

    if (formData.price <= 0) {
        alert('올바른 가격을 입력해주세요.');
        document.getElementById('editPrice').focus();
        return;
    }

    // API 호출
    console.log('전송할 데이터:', formData);

    fetch(`/company/api/medical-services/${serviceId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData)
    })
    .then(async response => {
        const text = await response.text();
        console.log('서버 응답 상태:', response.status);
        console.log('서버 응답 내용:', text);

        if (response.ok) {
            alert('서비스가 성공적으로 수정되었습니다.');
            closeEditModal();
            location.reload(); // 페이지 새로고침으로 업데이트된 데이터 표시
        } else {
            throw new Error(`서비스 수정 실패 (${response.status}): ${text}`);
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('서비스 수정 중 오류가 발생했습니다: ' + error.message);
    });
}

// 모달 외부 클릭 시 닫기
document.addEventListener('click', function(e) {
    const modal = document.getElementById('editModal');
    if (e.target === modal) {
        closeEditModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeEditModal();
    }
});

// 버튼에서 서비스 ID 읽어서 삭제하기
function deleteServiceFromButton(button) {
    const serviceId = button.dataset.serviceId;
    deleteService(serviceId);
}

// 서비스 삭제
function deleteService(serviceId) {
    if (!serviceId) {
        alert('서비스 ID를 찾을 수 없습니다.');
        return;
    }

    if (confirm('정말로 이 서비스를 삭제하시겠습니까?\n삭제된 서비스는 복원할 수 없습니다.')) {
        console.log('삭제 요청 - 서비스 ID:', serviceId);

        fetch(`/company/api/medical-services/${serviceId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(async response => {
            const text = await response.text();
            console.log('서버 응답 상태:', response.status);
            console.log('서버 응답 내용:', text);

            if (response.ok) {
                alert('서비스가 성공적으로 삭제되었습니다.');
                location.reload(); // 페이지 새로고침으로 삭제된 항목 제거
            } else {
                throw new Error(`서비스 삭제 실패 (${response.status}): ${text}`);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('서비스 삭제 중 오류가 발생했습니다: ' + error.message);
        });
    }
}

function escapeHtml(str) {
  return String(str ?? '').replace(/[&<>"']/g, s => ({
    '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
  }[s]));
}

// 예시 네비게이션 함수(원하는 페이지로 연결)
function openCreate(itemId) {
  // ex) /company/event-registration?itemId=...
  window.location.href = `/company/event-registration?itemId=${itemId}`;
}
function openDetail(serviceId) {
  // ex) 상세 페이지 라우팅
  window.location.href = `/company/medical-services/detail/${serviceId}`;
}
