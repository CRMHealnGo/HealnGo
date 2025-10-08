// 업체 관리 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    initializeTable();
});

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 검색 입력 엔터키 이벤트
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    }
}

// 테이블 초기화
function initializeTable() {
    // 체크박스 이벤트 설정
    setupCheckboxEvents();
}

// 체크박스 이벤트 설정
function setupCheckboxEvents() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    const companyCheckboxes = document.querySelectorAll('.company-checkbox');
    
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            companyCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
            updateBulkActions();
        });
    }
    
    companyCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateSelectAllCheckbox();
            updateBulkActions();
        });
    });
}

// 전체 선택 체크박스 업데이트
function updateSelectAllCheckbox() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    const companyCheckboxes = document.querySelectorAll('.company-checkbox');
    const checkedCount = document.querySelectorAll('.company-checkbox:checked').length;
    
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = checkedCount === companyCheckboxes.length;
        selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < companyCheckboxes.length;
    }
}

// 일괄 작업 버튼 상태 업데이트
function updateBulkActions() {
    const checkedCount = document.querySelectorAll('.company-checkbox:checked').length;
    const bulkButtons = document.querySelectorAll('.table-actions .btn:not(.btn-secondary)');
    
    bulkButtons.forEach(button => {
        button.disabled = checkedCount === 0;
        button.style.opacity = checkedCount === 0 ? '0.5' : '1';
    });
}

// 검색 수행
function performSearch() {
    const searchInput = document.getElementById('searchInput');
    const searchTerm = searchInput ? searchInput.value.trim() : '';
    
    // URL 파라미터 업데이트
    const url = new URL(window.location);
    if (searchTerm) {
        url.searchParams.set('search', searchTerm);
    } else {
        url.searchParams.delete('search');
    }
    url.searchParams.set('page', '1'); // 검색 시 첫 페이지로 이동
    
    window.location.href = url.toString();
}

// 필터 적용
function applyFilters() {
    const statusFilter = document.getElementById('statusFilter');
    const categoryFilter = document.getElementById('categoryFilter');
    const searchInput = document.getElementById('searchInput');
    
    const url = new URL(window.location);
    
    // 검색어
    const searchTerm = searchInput ? searchInput.value.trim() : '';
    if (searchTerm) {
        url.searchParams.set('search', searchTerm);
    } else {
        url.searchParams.delete('search');
    }
    
    // 상태 필터
    const status = statusFilter ? statusFilter.value : '';
    if (status) {
        url.searchParams.set('status', status);
    } else {
        url.searchParams.delete('status');
    }
    
    // 카테고리 필터
    const category = categoryFilter ? categoryFilter.value : '';
    if (category) {
        url.searchParams.set('category', category);
    } else {
        url.searchParams.delete('category');
    }
    
    url.searchParams.set('page', '1'); // 필터 적용 시 첫 페이지로 이동
    
    window.location.href = url.toString();
}

// 페이지 이동
function goToPage(page) {
    const url = new URL(window.location);
    url.searchParams.set('page', page);
    window.location.href = url.toString();
}

// 전체 선택
function selectAll() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = true;
        selectAllCheckbox.dispatchEvent(new Event('change'));
    }
}

// 일괄 작업
function bulkAction(action) {
    const checkedBoxes = document.querySelectorAll('.company-checkbox:checked');
    const companyIds = Array.from(checkedBoxes).map(checkbox => checkbox.value);
    
    if (companyIds.length === 0) {
        showNotification('선택된 업체가 없습니다.', 'warning');
        return;
    }
    
    let message = '';
    switch(action) {
        case 'approve':
            message = `선택된 ${companyIds.length}개 업체를 승인하시겠습니까?`;
            break;
        case 'reject':
            message = `선택된 ${companyIds.length}개 업체를 거부하시겠습니까?`;
            break;
        case 'suspend':
            message = `선택된 ${companyIds.length}개 업체를 정지하시겠습니까?`;
            break;
    }
    
    if (confirm(message)) {
        // 실제로는 서버에 요청을 보내야 함
        console.log(`${action} action for companies:`, companyIds);
        showNotification(`${companyIds.length}개 업체에 대한 작업이 완료되었습니다.`, 'success');
        
        // 체크박스 초기화
        checkedBoxes.forEach(checkbox => {
            checkbox.checked = false;
        });
        updateSelectAllCheckbox();
        updateBulkActions();
    }
}

// 업체 상세 보기
function viewCompany(companyId) {
    // 실제로는 서버에서 업체 상세 정보를 가져와야 함
    console.log('View company:', companyId);
    
    // 모달에 업체 정보 로드
    loadCompanyDetail(companyId);
    
    // 모달 표시
    const modal = document.getElementById('companyDetailModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

// 업체 상세 정보 로드
function loadCompanyDetail(companyId) {
    const content = document.getElementById('companyDetailContent');
    if (!content) return;
    
    // 임시 업체 정보 (실제로는 서버에서 가져와야 함)
    const companyInfo = {
        id: companyId,
        name: '서울병원',
        owner: '김사업',
        phone: '010-1234-5678',
        email: 'seoul@hospital.com',
        address: '서울시 강남구 테헤란로 123',
        category: '병원',
        joinDate: '2024-01-15',
        status: '승인완료',
        businessNumber: '123-45-67890',
        description: '서울 강남구에 위치한 종합병원입니다.',
        totalReservations: 150,
        totalRevenue: 5000000
    };
    
    content.innerHTML = `
        <div class="company-detail">
            <div class="detail-row">
                <label>업체 ID:</label>
                <span>${companyInfo.id}</span>
            </div>
            <div class="detail-row">
                <label>업체명:</label>
                <span>${companyInfo.name}</span>
            </div>
            <div class="detail-row">
                <label>사업자명:</label>
                <span>${companyInfo.owner}</span>
            </div>
            <div class="detail-row">
                <label>연락처:</label>
                <span>${companyInfo.phone}</span>
            </div>
            <div class="detail-row">
                <label>이메일:</label>
                <span>${companyInfo.email}</span>
            </div>
            <div class="detail-row">
                <label>주소:</label>
                <span>${companyInfo.address}</span>
            </div>
            <div class="detail-row">
                <label>카테고리:</label>
                <span class="category-badge">${companyInfo.category}</span>
            </div>
            <div class="detail-row">
                <label>사업자등록번호:</label>
                <span>${companyInfo.businessNumber}</span>
            </div>
            <div class="detail-row">
                <label>등록일:</label>
                <span>${companyInfo.joinDate}</span>
            </div>
            <div class="detail-row">
                <label>상태:</label>
                <span class="status-badge status-${companyInfo.status === '승인완료' ? 'approved' : companyInfo.status === '승인대기' ? 'pending' : 'rejected'}">${companyInfo.status}</span>
            </div>
            <div class="detail-row">
                <label>설명:</label>
                <span>${companyInfo.description}</span>
            </div>
            <div class="detail-row">
                <label>총 예약 수:</label>
                <span>${companyInfo.totalReservations}건</span>
            </div>
            <div class="detail-row">
                <label>총 매출:</label>
                <span>${companyInfo.totalRevenue.toLocaleString()}원</span>
            </div>
        </div>
    `;
}

// 업체 수정
function editCompany(companyId) {
    console.log('Edit company:', companyId);
    // 실제로는 업체 수정 페이지로 이동하거나 모달을 표시해야 함
    showNotification('업체 수정 기능은 준비 중입니다.', 'info');
}

// 업체 승인
function approveCompany(companyId) {
    if (confirm('이 업체를 승인하시겠습니까?')) {
        // 실제로는 서버에 요청을 보내야 함
        console.log('Approve company:', companyId);
        showNotification('업체가 승인되었습니다.', 'success');
        
        // 테이블에서 상태 업데이트
        updateCompanyStatus(companyId, '승인완료');
    }
}

// 업체 거부
function rejectCompany(companyId) {
    if (confirm('이 업체를 거부하시겠습니까?')) {
        // 실제로는 서버에 요청을 보내야 함
        console.log('Reject company:', companyId);
        showNotification('업체가 거부되었습니다.', 'success');
        
        // 테이블에서 상태 업데이트
        updateCompanyStatus(companyId, '승인거부');
    }
}

// 업체 상태 업데이트
function updateCompanyStatus(companyId, status) {
    const companyRow = document.querySelector(`tr[data-company-id="${companyId}"]`);
    if (companyRow) {
        const statusBadge = companyRow.querySelector('.status-badge');
        if (statusBadge) {
            statusBadge.textContent = status;
            statusBadge.className = `status-badge status-${status === '승인완료' ? 'approved' : status === '승인대기' ? 'pending' : 'rejected'}`;
        }
        
        // 액션 버튼 업데이트
        const actionButtons = companyRow.querySelector('.action-buttons');
        if (actionButtons && status === '승인완료') {
            // 승인/거부 버튼 제거
            const approveBtn = actionButtons.querySelector('.btn-success');
            const rejectBtn = actionButtons.querySelector('.btn-danger');
            if (approveBtn) approveBtn.remove();
            if (rejectBtn) rejectBtn.remove();
        }
    }
}

// 모달 닫기
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
    }
}

// 모달 외부 클릭 시 닫기
window.addEventListener('click', function(event) {
    const modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
});

// 알림 표시
function showNotification(message, type = 'info') {
    // 알림 요소 생성
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    // 스타일 추가
    const bgColor = type === 'success' ? '#d4edda' : type === 'warning' ? '#fff3cd' : '#d1ecf1';
    const textColor = type === 'success' ? '#155724' : type === 'warning' ? '#856404' : '#0c5460';
    
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${bgColor};
        color: ${textColor};
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        z-index: 1000;
        display: flex;
        align-items: center;
        gap: 10px;
        min-width: 300px;
        animation: slideIn 0.3s ease;
    `;
    
    // 알림 내용 스타일
    const content = notification.querySelector('.notification-content');
    content.style.cssText = `
        display: flex;
        align-items: center;
        gap: 8px;
        flex: 1;
    `;
    
    // 닫기 버튼 스타일
    const closeBtn = notification.querySelector('.notification-close');
    closeBtn.style.cssText = `
        background: none;
        border: none;
        cursor: pointer;
        color: inherit;
        padding: 0;
        margin-left: 10px;
    `;
    
    // 애니메이션 스타일 추가
    if (!document.querySelector('#notification-styles')) {
        const style = document.createElement('style');
        style.id = 'notification-styles';
        style.textContent = `
            @keyframes slideIn {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
        `;
        document.head.appendChild(style);
    }
    
    // 알림 추가
    document.body.appendChild(notification);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 3000);
}

