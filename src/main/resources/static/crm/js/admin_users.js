// 사용자 관리 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeEventListeners();
    initializeTable();
    setupModalEvents();
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
    const userInfo = getUserData(userId);
    
    content.innerHTML = `
        <div class="detail-section">
            <h3><i class="fas fa-user"></i> 기본 정보</h3>
            <div class="detail-row">
                <label>사용자 ID:</label>
                <span>${userInfo.id}</span>
            </div>
            <div class="detail-row">
                <label>이름:</label>
                <span><strong>${userInfo.name}</strong></span>
            </div>
            <div class="detail-row">
                <label>이메일:</label>
                <span>${userInfo.email}</span>
            </div>
            <div class="detail-row">
                <label>전화번호:</label>
                <span>${userInfo.phone || '미등록'}</span>
            </div>
            <div class="detail-row">
                <label>상태:</label>
                <span><span class="status-badge ${userInfo.status === '활성' ? 'status-active' : 'status-inactive'}">${userInfo.status}</span></span>
            </div>
        </div>
        
        <div class="detail-section">
            <h3><i class="fas fa-clock"></i> 활동 정보</h3>
            <div class="detail-row">
                <label>가입일:</label>
                <span>${userInfo.joinDate}</span>
            </div>
            <div class="detail-row">
                <label>최근 로그인:</label>
                <span>${userInfo.lastLogin || '정보 없음'}</span>
            </div>
            <div class="detail-row">
                <label>총 예약 수:</label>
                <span>${userInfo.totalReservations || 0}건</span>
            </div>
            <div class="detail-row">
                <label>총 결제 금액:</label>
                <span>${(userInfo.totalSpent || 0).toLocaleString()}원</span>
            </div>
        </div>
        
        <div class="detail-section">
            <h3><i class="fas fa-exclamation-triangle"></i> 신고 정보</h3>
            <div class="detail-row">
                <label>신고 횟수:</label>
                <span>
                    <span class="report-badge ${userInfo.reportCount >= 3 ? 'report-high' : userInfo.reportCount >= 1 ? 'report-medium' : 'report-low'}" id="reportCountBadge-${userInfo.id}">${userInfo.reportCount || 0}회</span>
                    <button class="btn-add-report" onclick="addReport(${userInfo.id})" title="신고 추가">
                        <i class="fas fa-plus"></i>
                    </button>
                </span>
            </div>
            ${userInfo.reportCount > 0 ? `
                <div class="detail-row">
                    <label>최근 신고일:</label>
                    <span id="lastReportDate-${userInfo.id}">${userInfo.lastReportDate || '정보 없음'}</span>
                </div>
            ` : `
                <div class="detail-row" id="lastReportRow-${userInfo.id}" style="display: none;">
                    <label>최근 신고일:</label>
                    <span id="lastReportDate-${userInfo.id}">정보 없음</span>
                </div>
            `}
            <div class="detail-row">
                <button class="btn-view-reports" onclick="viewReportHistory(${userInfo.id})">
                    <i class="fas fa-list"></i>
                    신고 내역 보기
                </button>
            </div>
        </div>
        
        ${userInfo.notes ? `
            <div class="detail-section">
                <h3><i class="fas fa-sticky-note"></i> 관리자 메모</h3>
                <div class="detail-row">
                    <span style="grid-column: 1 / -1;">${userInfo.notes}</span>
                </div>
            </div>
        ` : ''}
    `;
}

// 사용자 데이터 가져오기 (임시)
function getUserData(userId) {
    // 실제로는 서버 API 호출
    const userDataMap = {
        '1': {
            id: 1,
            name: '김철수',
            email: 'kim@example.com',
            phone: '010-1234-5678',
            joinDate: '2024-01-15',
            lastLogin: '2024-10-09 14:30',
            status: '활성',
            reportCount: 0,
            totalReservations: 15,
            totalSpent: 750000,
            notes: ''
        }
    };
    
    return userDataMap[userId] || userDataMap['1'];
}

// 상세 모달 닫기
function closeUserDetailModal() {
    document.getElementById('userDetailModal').style.display = 'none';
}

// 사용자 수정
function editUser(userId) {
    console.log('Edit user:', userId);
    
    // 사용자 데이터 가져오기
    const userData = getUserData(userId);
    
    // 폼에 데이터 채우기
    document.getElementById('editUserId').value = userId;
    document.getElementById('editUserName').value = userData.name;
    document.getElementById('editUserEmail').value = userData.email;
    document.getElementById('editUserPhone').value = userData.phone || '';
    document.getElementById('editUserStatus').value = userData.status === '활성' ? 'active' : 'inactive';
    document.getElementById('editUserNotes').value = userData.notes || '';
    
    // 모달 열기
    document.getElementById('editUserModal').style.display = 'block';
}

// 수정 모달 닫기
function closeEditUserModal() {
    document.getElementById('editUserModal').style.display = 'none';
}

// 사용자 수정 처리
function processEditUser() {
    const userId = document.getElementById('editUserId').value;
    const name = document.getElementById('editUserName').value;
    const email = document.getElementById('editUserEmail').value;
    const phone = document.getElementById('editUserPhone').value;
    const status = document.getElementById('editUserStatus').value;
    const notes = document.getElementById('editUserNotes').value;
    
    console.log('사용자 수정:', { userId, name, email, phone, status, notes });
    
    // 실제로는 서버에 수정 요청
    
    // 테이블 업데이트
    const row = document.querySelector(`tr .user-checkbox[value="${userId}"]`)?.closest('tr');
    if (row) {
        row.querySelector('.user-name').textContent = name;
        row.querySelector('.user-email').textContent = email;
        
        const statusBadge = row.querySelector('.status-badge');
        const statusText = status === 'active' ? '활성' : status === 'suspended' ? '정지' : '비활성';
        statusBadge.textContent = statusText;
        statusBadge.className = `status-badge status-${status}`;
    }
    
    closeEditUserModal();
    alert('사용자 정보가 수정되었습니다.');
}

// 모달 이벤트 설정
function setupModalEvents() {
    const editForm = document.getElementById('editUserForm');
    
    // 수정 폼 제출 이벤트
    if (editForm) {
        editForm.addEventListener('submit', function(e) {
            e.preventDefault();
            processEditUser();
        });
    }
}

// 신고 추가
function addReport(userId) {
    const reason = prompt('신고 사유를 입력하세요:');
    
    if (reason && reason.trim()) {
        console.log('신고 추가:', userId, '사유:', reason);
        
        // 실제로는 서버에 신고 추가 요청
        
        // 신고 횟수 업데이트
        const reportBadge = document.getElementById(`reportCountBadge-${userId}`);
        const lastReportDate = document.getElementById(`lastReportDate-${userId}`);
        const lastReportRow = document.getElementById(`lastReportRow-${userId}`);
        
        if (reportBadge) {
            const currentCount = parseInt(reportBadge.textContent);
            const newCount = currentCount + 1;
            reportBadge.textContent = `${newCount}회`;
            
            // 배지 색상 업데이트
            reportBadge.className = `report-badge ${newCount >= 3 ? 'report-high' : newCount >= 1 ? 'report-medium' : 'report-low'}`;
        }
        
        // 최근 신고일 업데이트
        if (lastReportDate) {
            const now = new Date();
            const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
            lastReportDate.textContent = dateStr;
        }
        
        // 최근 신고일 행 표시
        if (lastReportRow) {
            lastReportRow.style.display = '';
        }
        
        // 테이블의 신고 횟수도 업데이트
        const row = document.querySelector(`tr .user-checkbox[value="${userId}"]`)?.closest('tr');
        if (row) {
            const tableReportBadge = row.querySelector('.report-badge');
            if (tableReportBadge) {
                const currentCount = parseInt(tableReportBadge.textContent);
                const newCount = currentCount + 1;
                tableReportBadge.textContent = newCount;
                tableReportBadge.className = `report-badge ${newCount >= 3 ? 'report-high' : newCount >= 1 ? 'report-medium' : 'report-low'}`;
            }
        }
        
        alert('신고가 추가되었습니다.');
    }
}

// 신고 내역 보기
function viewReportHistory(userId) {
    console.log('신고 내역 보기:', userId);
    
    // 임시 신고 내역 데이터
    const reportHistory = [
        {
            date: '2024-10-05',
            reason: '부적절한 리뷰 작성',
            reporter: '업체: 힝거피부과',
            status: '처리완료'
        },
        {
            date: '2024-09-20',
            reason: '예약 노쇼',
            reporter: '업체: 뷰티클리닉',
            status: '처리완료'
        }
    ];
    
    let historyHtml = '<div class="report-history">';
    historyHtml += '<h4 style="margin-bottom: 15px; font-size: 16px; font-weight: 600;">신고 내역</h4>';
    
    if (reportHistory.length > 0) {
        reportHistory.forEach((report, index) => {
            historyHtml += `
                <div class="report-history-item" style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 10px; border-left: 3px solid #e74c3c;">
                    <div style="display: flex; justify-content: space-between; margin-bottom: 8px;">
                        <strong>${index + 1}. ${report.date}</strong>
                        <span class="status-badge status-active" style="font-size: 11px;">${report.status}</span>
                    </div>
                    <div style="font-size: 14px; color: #2c3e50; margin-bottom: 5px;">
                        <strong>사유:</strong> ${report.reason}
                    </div>
                    <div style="font-size: 13px; color: #7f8c8d;">
                        <strong>신고자:</strong> ${report.reporter}
                    </div>
                </div>
            `;
        });
    } else {
        historyHtml += '<p style="text-align: center; color: #7f8c8d; padding: 20px;">신고 내역이 없습니다.</p>';
    }
    
    historyHtml += '</div>';
    
    alert('신고 내역:\n\n' + reportHistory.map((r, i) => `${i+1}. ${r.date} - ${r.reason}`).join('\n'));
    // 실제로는 모달로 표시하는 것이 더 좋습니다
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

