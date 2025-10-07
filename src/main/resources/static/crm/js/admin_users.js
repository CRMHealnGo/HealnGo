// 사용자 관리 페이지 JavaScript

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
    const userCheckboxes = document.querySelectorAll('.user-checkbox');
    
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            userCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
            updateBulkActions();
        });
    }
    
    userCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateSelectAllCheckbox();
            updateBulkActions();
        });
    });
}

// 전체 선택 체크박스 업데이트
function updateSelectAllCheckbox() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    const userCheckboxes = document.querySelectorAll('.user-checkbox');
    const checkedCount = document.querySelectorAll('.user-checkbox:checked').length;
    
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = checkedCount === userCheckboxes.length;
        selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < userCheckboxes.length;
    }
}

// 일괄 작업 버튼 상태 업데이트
function updateBulkActions() {
    const checkedCount = document.querySelectorAll('.user-checkbox:checked').length;
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
    const sortBy = document.getElementById('sortBy');
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
    
    // 정렬
    const sort = sortBy ? sortBy.value : '';
    if (sort) {
        url.searchParams.set('sort', sort);
    } else {
        url.searchParams.delete('sort');
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
    const checkedBoxes = document.querySelectorAll('.user-checkbox:checked');
    const userIds = Array.from(checkedBoxes).map(checkbox => checkbox.value);
    
    if (userIds.length === 0) {
        showNotification('선택된 사용자가 없습니다.', 'warning');
        return;
    }
    
    let message = '';
    switch(action) {
        case 'suspend':
            message = `선택된 ${userIds.length}명의 사용자를 정지하시겠습니까?`;
            break;
        case 'activate':
            message = `선택된 ${userIds.length}명의 사용자를 활성화하시겠습니까?`;
            break;
        case 'delete':
            message = `선택된 ${userIds.length}명의 사용자를 삭제하시겠습니까?`;
            break;
    }
    
    if (confirm(message)) {
        // 실제로는 서버에 요청을 보내야 함
        console.log(`${action} action for users:`, userIds);
        showNotification(`${userIds.length}명의 사용자에 대한 작업이 완료되었습니다.`, 'success');
        
        // 체크박스 초기화
        checkedBoxes.forEach(checkbox => {
            checkbox.checked = false;
        });
        updateSelectAllCheckbox();
        updateBulkActions();
    }
}

// 사용자 상세 보기
function viewUser(userId) {
    // 실제로는 서버에서 사용자 상세 정보를 가져와야 함
    console.log('View user:', userId);
    
    // 모달에 사용자 정보 로드
    loadUserDetail(userId);
    
    // 모달 표시
    const modal = document.getElementById('userDetailModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

// 사용자 상세 정보 로드
function loadUserDetail(userId) {
    const content = document.getElementById('userDetailContent');
    if (!content) return;
    
    // 임시 사용자 정보 (실제로는 서버에서 가져와야 함)
    const userInfo = {
        id: userId,
        name: '김철수',
        email: 'kim@example.com',
        phone: '010-1234-5678',
        joinDate: '2024-01-15',
        lastLogin: '2024-01-20 14:30',
        status: '활성',
        reportCount: 2,
        totalReservations: 15,
        totalSpent: 750000
    };
    
    content.innerHTML = `
        <div class="user-detail">
            <div class="detail-row">
                <label>사용자 ID:</label>
                <span>${userInfo.id}</span>
            </div>
            <div class="detail-row">
                <label>이름:</label>
                <span>${userInfo.name}</span>
            </div>
            <div class="detail-row">
                <label>이메일:</label>
                <span>${userInfo.email}</span>
            </div>
            <div class="detail-row">
                <label>전화번호:</label>
                <span>${userInfo.phone}</span>
            </div>
            <div class="detail-row">
                <label>가입일:</label>
                <span>${userInfo.joinDate}</span>
            </div>
            <div class="detail-row">
                <label>최근 로그인:</label>
                <span>${userInfo.lastLogin}</span>
            </div>
            <div class="detail-row">
                <label>상태:</label>
                <span class="status-badge ${userInfo.status === '활성' ? 'status-active' : 'status-inactive'}">${userInfo.status}</span>
            </div>
            <div class="detail-row">
                <label>신고 횟수:</label>
                <span class="report-badge ${userInfo.reportCount >= 3 ? 'report-high' : userInfo.reportCount >= 1 ? 'report-medium' : 'report-low'}">${userInfo.reportCount}</span>
            </div>
            <div class="detail-row">
                <label>총 예약 수:</label>
                <span>${userInfo.totalReservations}건</span>
            </div>
            <div class="detail-row">
                <label>총 결제 금액:</label>
                <span>${userInfo.totalSpent.toLocaleString()}원</span>
            </div>
        </div>
    `;
}

// 사용자 수정
function editUser(userId) {
    console.log('Edit user:', userId);
    // 실제로는 사용자 수정 페이지로 이동하거나 모달을 표시해야 함
    showNotification('사용자 수정 기능은 준비 중입니다.', 'info');
}

// 사용자 정지
function suspendUser(userId) {
    if (confirm('이 사용자를 정지하시겠습니까?')) {
        // 실제로는 서버에 요청을 보내야 함
        console.log('Suspend user:', userId);
        showNotification('사용자가 정지되었습니다.', 'success');
        
        // 테이블에서 상태 업데이트
        updateUserStatus(userId, '정지');
    }
}

// 사용자 상태 업데이트
function updateUserStatus(userId, status) {
    const userRow = document.querySelector(`tr[data-user-id="${userId}"]`);
    if (userRow) {
        const statusBadge = userRow.querySelector('.status-badge');
        if (statusBadge) {
            statusBadge.textContent = status;
            statusBadge.className = `status-badge ${status === '활성' ? 'status-active' : 'status-inactive'}`;
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
