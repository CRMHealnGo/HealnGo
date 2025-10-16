// 이벤트 등록 페이지에서 사이드바 활성 상태 설정
document.addEventListener('DOMContentLoaded', function() {
    // 모든 nav-link에서 active 클래스 제거
    const allNavLinks = document.querySelectorAll('.nav-link');
    allNavLinks.forEach(link => link.classList.remove('active'));
    
    // 의료 서비스 관리 링크에 active 클래스 추가
    const medicalServiceLink = document.querySelector('a[href="/company/medical-services"]');
    if (medicalServiceLink) {
        medicalServiceLink.classList.add('active');
    }
});

// 이벤트 등록 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // 이벤트 제목 글자 수 카운터
    const titleInput = document.querySelector('input[placeholder*="사각턱보톡스"]');
    const charCounter = document.querySelector('.event-char-counter');

    if (titleInput && charCounter) {
        titleInput.addEventListener('input', function() {
            const currentLength = this.value.length;
            charCounter.textContent = `${currentLength} / 100`;
        });
    }

    // 자유 태그 기능
    const freeTagInput = document.getElementById('freeTagInput');
    const freeTagsContainer = document.getElementById('freeTagsContainer');
    let freeTagCount = 0;

    if (freeTagInput) {
        freeTagInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && this.value.trim() && freeTagCount < 5) {
                e.preventDefault();
                addFreeTag(this.value.trim());
                this.value = '';
            }
        });
    }

    // 시술명 태그 기능
    const procedureTagInput = document.getElementById('procedureTagInput');
    const procedureTagsContainer = document.getElementById('procedureTagsContainer');
    let procedureTagCount = 2; // 이미 2개가 있음

    if (procedureTagInput) {
        procedureTagInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && this.value.trim() && procedureTagCount < 8) {
                e.preventDefault();
                addProcedureTag(this.value.trim());
                this.value = '';
            }
        });
    }
});

// 자유 태그 추가
function addFreeTag(tagText) {
    const container = document.getElementById('freeTagsContainer');
    const tagDiv = document.createElement('div');
    tagDiv.className = 'event-tag';
    tagDiv.innerHTML = `
        <span>${tagText}</span>
        <button class="event-tag-remove" onclick="removeTag(this)">×</button>
    `;

    const input = document.getElementById('freeTagInput');
    container.insertBefore(tagDiv, input);

    const freeTagCount = document.querySelectorAll('#freeTagsContainer .event-tag').length;
    if (freeTagCount >= 5) {
        input.style.display = 'none';
    }
}

// 시술명 태그 추가
function addProcedureTag(tagText) {
    const container = document.getElementById('procedureTagsContainer');
    const tagDiv = document.createElement('div');
    tagDiv.className = 'event-tag';
    tagDiv.innerHTML = `
        <span>${tagText}</span>
        <button class="event-tag-remove" onclick="removeTag(this)">×</button>
    `;

    const input = document.getElementById('procedureTagInput');
    container.insertBefore(tagDiv, input);

    const procedureTagCount = document.querySelectorAll('#procedureTagsContainer .event-tag').length;
    if (procedureTagCount >= 8) {
        input.style.display = 'none';
    }
}

// 태그 제거
function removeTag(button) {
    const tag = button.parentElement;
    const container = tag.parentElement;
    tag.remove();

    // 입력 필드 다시 표시
    const input = container.querySelector('.event-tag-input');
    if (input) {
        input.style.display = 'block';
    }
}

// 이벤트 승인 요청 버튼
document.querySelector('.event-approval-btn').addEventListener('click', async function() {
    // 태그 데이터 수집
    const freeTags = Array.from(document.querySelectorAll('#freeTagsContainer .event-tag span')).map(span => span.textContent);
    const procedureTags = Array.from(document.querySelectorAll('#procedureTagsContainer .event-tag span')).map(span => span.textContent);

    // JSON 데이터 구성
    const formData = {
        name: document.querySelector('input[name="name"]')?.value || '',
        startDate: document.querySelector('input[name="startDate"]')?.value || '',
        endDate: document.querySelector('input[name="endDate"]')?.value || '',
        genderTarget: document.querySelector('input[name="genderTarget"]:checked')?.value === 'unisex' ? 'ALL' : 
                     document.querySelector('input[name="genderTarget"]:checked')?.value === 'female' ? 'FEMALE' : 
                     document.querySelector('input[name="genderTarget"]:checked')?.value === 'male' ? 'MALE' : 'ALL',
        targetCountry: document.querySelector('input[name="targetCountry"]:checked')?.value === 'korea' ? 'KOR' : 
                      document.querySelector('input[name="targetCountry"]:checked')?.value === 'japan' ? 'JPN' : 
                      document.querySelector('input[name="targetCountry"]:checked')?.value === 'other' ? 'OTHER' : 'KOR',
        tags: freeTags.join(','),
        serviceCategory: procedureTags.join(','),
        price: parseFloat(document.querySelector('input[name="price"]')?.value || '0'),
        vatIncluded: document.querySelector('input[name="vatIncluded"]:checked')?.value === 'true',
        isRefundable: true,
        currency: 'KRW',
        description: ''
    };

    // 필수 필드 검증
    if (!formData.name.trim()) {
        alert('의료 서비스 제목을 입력해주세요.');
        return;
    }

    if (!formData.price || formData.price <= 0) {
        alert('의료 서비스 가격을 올바르게 입력해주세요.');
        return;
    }

    if (!formData.startDate || !formData.endDate) {
        alert('의료 서비스 기간을 선택해주세요.');
        return;
    }

    try {
        // fetch로 JSON 데이터 전송 - 세션에서 companyId를 사용하여 해당 회사의 item 찾기
        const response = await fetch('/company/api/medical-services', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            alert('의료 서비스 등록이 완료되었습니다.');
        } else {
            const errorText = await response.text();
            console.error('서버 응답:', response.status, errorText);
            alert(`의료 서비스 등록에 실패했습니다.\n상태: ${response.status}\n오류: ${errorText}`);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('서버 오류가 발생했습니다.');
    }
});
