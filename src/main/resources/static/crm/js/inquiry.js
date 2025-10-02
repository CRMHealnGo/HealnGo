// 문의&신고 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 문의 데이터 초기화
    initializeInquiries();
    
    // 문의 아이템 클릭 이벤트
    setupInquiryClick();
    
    // 페이지네이션 이벤트
    setupPagination();
    
    // 새 문의 작성 이벤트
    setupNewInquiry();
    
    // 문의 제출 이벤트
    setupInquirySubmission();
});

// 문의 데이터 초기화
function initializeInquiries() {
    const inquiries = [
        {
            id: 5,
            number: '005',
            question: '예약은 어떻게 하면 되나요?',
            detail: '',
            date: 'Created Sep 12, 2020',
            type: 'inquiry'
        },
        {
            id: 4,
            number: '004',
            question: '이용 문의',
            detail: '서비스 이용 방법에 대해 문의드립니다.',
            date: 'Created Sep 12, 2020',
            type: 'inquiry'
        },
        {
            id: 3,
            number: '003',
            question: '결제 관련 문의',
            detail: '결제가 제대로 되지 않습니다. 확인 부탁드립니다.',
            date: 'Created Sep 10, 2020',
            type: 'inquiry'
        },
        {
            id: 2,
            number: '002',
            question: '계정 문제 신고',
            detail: '계정에 문제가 있어 신고합니다.',
            date: 'Created Sep 8, 2020',
            type: 'report'
        },
        {
            id: 1,
            number: '001',
            question: '서비스 개선 제안',
            detail: '더 나은 서비스를 위한 제안사항입니다.',
            date: 'Created Sep 5, 2020',
            type: 'inquiry'
        }
    ];
    
    renderInquiryList(inquiries);
    addPagination();
}

// 문의 목록 렌더링
function renderInquiryList(inquiries) {
    const container = document.querySelector('.inquiry-list');
    if (!container) return;
    
    let html = '';
    inquiries.forEach(inquiry => {
        html += createInquiryItem(inquiry);
    });
    
    container.innerHTML = html;
}

// 문의 아이템 생성
function createInquiryItem(inquiry) {
    return `
        <div class="inquiry-item" data-inquiry-id="${inquiry.id}">
            <div class="inquiry-header-row">
                <div class="inquiry-number">${inquiry.number}</div>
                <div class="inquiry-date">${inquiry.date}</div>
            </div>
            <div class="inquiry-question">${inquiry.question}</div>
            <div class="inquiry-detail" style="display: none;">
                ${inquiry.detail}
                <div class="inquiry-options">
                    <label class="inquiry-option">
                        <input type="checkbox" name="inquiry-type" value="inquiry">
                        문의
                    </label>
                    <label class="inquiry-option">
                        <input type="checkbox" name="inquiry-type" value="report">
                        신고
                    </label>
                </div>
                <div class="inquiry-actions">
                    <button class="inquiry-btn cancel" onclick="cancelInquiry(${inquiry.id})">취소</button>
                    <button class="inquiry-btn submit" onclick="submitInquiry(${inquiry.id})">등록</button>
                </div>
            </div>
        </div>
    `;
}

// 문의 아이템 클릭 이벤트
function setupInquiryClick() {
    document.addEventListener('click', function(e) {
        const inquiryItem = e.target.closest('.inquiry-item');
        if (inquiryItem && !e.target.closest('.inquiry-actions') && !e.target.closest('.inquiry-options')) {
            const inquiryId = inquiryItem.dataset.inquiryId;
            toggleInquiryDetail(inquiryId);
        }
    });
}

// 문의 상세 토글
function toggleInquiryDetail(inquiryId) {
    const inquiryItem = document.querySelector(`[data-inquiry-id="${inquiryId}"]`);
    if (!inquiryItem) return;
    
    const detail = inquiryItem.querySelector('.inquiry-detail');
    const isExpanded = inquiryItem.classList.contains('expanded');
    
    // 모든 문의 아이템 닫기
    document.querySelectorAll('.inquiry-item').forEach(item => {
        item.classList.remove('expanded');
        const itemDetail = item.querySelector('.inquiry-detail');
        if (itemDetail) {
            itemDetail.style.display = 'none';
        }
    });
    
    // 클릭된 아이템 토글
    if (!isExpanded) {
        inquiryItem.classList.add('expanded');
        if (detail) {
            detail.style.display = 'block';
        }
    }
}

// 페이지네이션 추가
function addPagination() {
    const container = document.querySelector('.inquiry-container');
    if (!container) return;
    
    const pagination = document.createElement('div');
    pagination.className = 'pagination';
    pagination.innerHTML = `
        <button onclick="goToPage(1)" class="active">1</button>
        <button onclick="goToPage(2)">2</button>
        <button onclick="goToPage(3)">3</button>
        <button onclick="goToPage(4)">4</button>
        <button onclick="goToPage(5)">5</button>
        <button onclick="nextPage()">></button>
        <button onclick="lastPage()">>></button>
    `;
    container.appendChild(pagination);
}

// 페이지네이션 이벤트
function setupPagination() {
    // 페이지네이션 이벤트는 HTML에서 onclick으로 처리
}

// 페이지 이동
function goToPage(page) {
    // 모든 페이지 버튼에서 active 클래스 제거
    document.querySelectorAll('.pagination button').forEach(btn => {
        btn.classList.remove('active');
    });
    
    // 선택된 페이지 버튼에 active 클래스 추가
    const targetButton = event.target;
    targetButton.classList.add('active');
    
    console.log(`페이지 ${page}로 이동`);
    // 여기서 실제 페이지 로딩 로직을 구현할 수 있습니다
}

// 다음 페이지
function nextPage() {
    const currentPage = document.querySelector('.pagination button.active');
    const nextButton = currentPage.nextElementSibling;
    if (nextButton && !nextButton.disabled) {
        nextButton.click();
    }
}

// 마지막 페이지
function lastPage() {
    const lastButton = document.querySelector('.pagination button:last-of-type');
    if (lastButton) {
        lastButton.click();
    }
}

// 새 문의 작성 이벤트
function setupNewInquiry() {
    // 새 문의 작성 버튼 추가
    addNewInquiryButton();
    
    // 새 문의 폼 추가
    addNewInquiryForm();
}

// 새 문의 작성 버튼 추가
function addNewInquiryButton() {
    const button = document.createElement('button');
    button.className = 'new-inquiry-btn';
    button.innerHTML = '<i class="fas fa-plus"></i>';
    button.onclick = toggleNewInquiryForm;
    document.body.appendChild(button);
}

// 새 문의 폼 추가
function addNewInquiryForm() {
    const container = document.querySelector('.inquiry-container');
    if (!container) return;
    
    const form = document.createElement('div');
    form.className = 'new-inquiry-form';
    form.innerHTML = `
        <h3 class="form-title">새 문의 작성</h3>
        <div class="form-group">
            <label class="form-label">문의 유형</label>
            <select class="form-input" name="inquiry-type">
                <option value="inquiry">문의</option>
                <option value="report">신고</option>
            </select>
        </div>
        <div class="form-group">
            <label class="form-label">제목</label>
            <input type="text" class="form-input" name="inquiry-title" placeholder="문의 제목을 입력하세요">
        </div>
        <div class="form-group">
            <label class="form-label">내용</label>
            <textarea class="form-textarea" name="inquiry-content" placeholder="문의 내용을 입력하세요"></textarea>
        </div>
        <div class="form-actions">
            <button class="inquiry-btn cancel" onclick="closeNewInquiryForm()">취소</button>
            <button class="inquiry-btn submit" onclick="submitNewInquiry()">등록</button>
        </div>
    `;
    
    container.appendChild(form);
}

// 새 문의 폼 토글
function toggleNewInquiryForm() {
    const form = document.querySelector('.new-inquiry-form');
    if (form) {
        form.classList.toggle('show');
    }
}

// 새 문의 폼 닫기
function closeNewInquiryForm() {
    const form = document.querySelector('.new-inquiry-form');
    if (form) {
        form.classList.remove('show');
        // 폼 초기화
        form.querySelectorAll('input, textarea, select').forEach(input => {
            input.value = '';
        });
    }
}

// 문의 제출 이벤트
function setupInquirySubmission() {
    // 문의 제출 이벤트는 HTML에서 onclick으로 처리
}

// 문의 취소
function cancelInquiry(inquiryId) {
    const inquiryItem = document.querySelector(`[data-inquiry-id="${inquiryId}"]`);
    if (inquiryItem) {
        inquiryItem.classList.remove('expanded');
        const detail = inquiryItem.querySelector('.inquiry-detail');
        if (detail) {
            detail.style.display = 'none';
        }
    }
}

// 문의 제출
function submitInquiry(inquiryId) {
    const inquiryItem = document.querySelector(`[data-inquiry-id="${inquiryId}"]`);
    if (!inquiryItem) return;
    
    const checkboxes = inquiryItem.querySelectorAll('input[name="inquiry-type"]:checked');
    if (checkboxes.length === 0) {
        alert('문의 유형을 선택해주세요.');
        return;
    }
    
    const selectedTypes = Array.from(checkboxes).map(cb => cb.value);
    console.log(`문의 ${inquiryId} 제출:`, selectedTypes);
    
    alert('문의가 등록되었습니다.');
    
    // 문의 아이템 닫기
    cancelInquiry(inquiryId);
}

// 새 문의 제출
function submitNewInquiry() {
    const form = document.querySelector('.new-inquiry-form');
    if (!form) return;
    
    const type = form.querySelector('select[name="inquiry-type"]').value;
    const title = form.querySelector('input[name="inquiry-title"]').value.trim();
    const content = form.querySelector('textarea[name="inquiry-content"]').value.trim();
    
    if (!title) {
        alert('제목을 입력해주세요.');
        return;
    }
    
    if (!content) {
        alert('내용을 입력해주세요.');
        return;
    }
    
    console.log('새 문의 제출:', { type, title, content });
    
    alert('새 문의가 등록되었습니다.');
    
    // 폼 닫기 및 초기화
    closeNewInquiryForm();
}
