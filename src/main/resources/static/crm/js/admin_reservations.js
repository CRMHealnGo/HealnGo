// 예약 관리 페이지 JavaScript

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
    const reservationCheckboxes = document.querySelectorAll('.reservation-checkbox');
    
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener('change', function() {
            reservationCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
            updateBulkActions();
        });
    }
    
    reservationCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateSelectAllCheckbox();
            updateBulkActions();
        });
    });
}

// 전체 선택 체크박스 업데이트
function updateSelectAllCheckbox() {
    const selectAllCheckbox = document.getElementById('selectAllCheckbox');
    const reservationCheckboxes = document.querySelectorAll('.reservation-checkbox');
    const checkedCount = document.querySelectorAll('.reservation-checkbox:checked').length;
    
    if (selectAllCheckbox) {
        selectAllCheckbox.checked = checkedCount === reservationCheckboxes.length;
        selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < reservationCheckboxes.length;
    }
}

// 일괄 작업 버튼 상태 업데이트
function updateBulkActions() {
    const checkedCount = document.querySelectorAll('.reservation-checkbox:checked').length;
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
    const dateFrom = document.getElementById('dateFrom');
    const dateTo = document.getElementById('dateTo');
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
    
    // 날짜 필터
    const fromDate = dateFrom ? dateFrom.value : '';
    const toDate = dateTo ? dateTo.value : '';
    if (fromDate) {
        url.searchParams.set('dateFrom', fromDate);
    } else {
        url.searchParams.delete('dateFrom');
    }
    if (toDate) {
        url.searchParams.set('dateTo', toDate);
    } else {
        url.searchParams.delete('dateTo');
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
    const checkedBoxes = document.querySelectorAll('.reservation-checkbox:checked');
    const reservationIds = Array.from(checkedBoxes).map(checkbox => checkbox.value);
    
    if (reservationIds.length === 0) {
        showNotification('선택된 예약이 없습니다.', 'warning');
        return;
    }
    
    let message = '';
    switch(action) {
        case 'confirm':
            message = `선택된 ${reservationIds.length}건의 예약을 확정하시겠습니까?`;
            break;
        case 'cancel':
            message = `선택된 ${reservationIds.length}건의 예약을 취소하시겠습니까?`;
            break;
        case 'complete':
            message = `선택된 ${reservationIds.length}건의 예약을 완료 처리하시겠습니까?`;
            break;
    }
    
    if (confirm(message)) {
        // 실제로는 서버에 요청을 보내야 함
        console.log(`${action} action for reservations:`, reservationIds);
        showNotification(`${reservationIds.length}건의 예약에 대한 작업이 완료되었습니다.`, 'success');
        
        // 체크박스 초기화
        checkedBoxes.forEach(checkbox => {
            checkbox.checked = false;
        });
        updateSelectAllCheckbox();
        updateBulkActions();
    }
}

// 예약 상세 보기
function viewReservation(reservationId) {
    // 실제로는 서버에서 예약 상세 정보를 가져와야 함
    console.log('View reservation:', reservationId);
    
    // 모달에 예약 정보 로드
    loadReservationDetail(reservationId);
    
    // 모달 표시
    const modal = document.getElementById('reservationDetailModal');
    if (modal) {
        modal.style.display = 'block';
    }
}

// 예약 상세 정보 로드
function loadReservationDetail(reservationId) {
    const content = document.getElementById('reservationDetailContent');
    if (!content) return;
    
    // 임시 예약 정보 (실제로는 서버에서 가져와야 함)
    const reservationInfo = {
        id: reservationId,
        userName: '김고객',
        userPhone: '010-1234-5678',
        companyName: '서울병원',
        companyPhone: '02-1234-5678',
        service: '진료',
        date: '2024-01-25',
        time: '14:00',
        status: '예약',
        amount: 50000,
        paymentMethod: '카드결제',
        notes: '첫 방문 고객입니다.',
        createdAt: '2024-01-20 10:30',
        updatedAt: '2024-01-20 10:30'
    };
    
    content.innerHTML = `
        <div class="reservation-detail">
            <div class="detail-row">
                <label>예약 ID:</label>
                <span>${reservationInfo.id}</span>
            </div>
            <div class="detail-row">
                <label>고객명:</label>
                <span>${reservationInfo.userName}</span>
            </div>
            <div class="detail-row">
                <label>고객 연락처:</label>
                <span>${reservationInfo.userPhone}</span>
            </div>
            <div class="detail-row">
                <label>업체명:</label>
                <span>${reservationInfo.companyName}</span>
            </div>
            <div class="detail-row">
                <label>업체 연락처:</label>
                <span>${reservationInfo.companyPhone}</span>
            </div>
            <div class="detail-row">
                <label>서비스:</label>
                <span>${reservationInfo.service}</span>
            </div>
            <div class="detail-row">
                <label>예약일시:</label>
                <span>${reservationInfo.date} ${reservationInfo.time}</span>
            </div>
            <div class="detail-row">
                <label>상태:</label>
                <span class="status-badge status-${reservationInfo.status === '완료' ? 'completed' : reservationInfo.status === '예약' ? 'reserved' : 'cancelled'}">${reservationInfo.status}</span>
            </div>
            <div class="detail-row">
                <label>금액:</label>
                <span>${reservationInfo.amount.toLocaleString()}원</span>
            </div>
            <div class="detail-row">
                <label>결제방법:</label>
                <span>${reservationInfo.paymentMethod}</span>
            </div>
            <div class="detail-row">
                <label>특이사항:</label>
                <span>${reservationInfo.notes}</span>
            </div>
            <div class="detail-row">
                <label>예약일시:</label>
                <span>${reservationInfo.createdAt}</span>
            </div>
            <div class="detail-row">
                <label>수정일시:</label>
                <span>${reservationInfo.updatedAt}</span>
            </div>
        </div>
    `;
}

// 예약 수정
function editReservation(reservationId) {
    console.log('Edit reservation:', reservationId);
    // 실제로는 예약 수정 페이지로 이동하거나 모달을 표시해야 함
    showNotification('예약 수정 기능은 준비 중입니다.', 'info');
}

// 예약 확정
function confirmReservation(reservationId) {
    if (confirm('이 예약을 확정하시겠습니까?')) {
        // 실제로는 서버에 요청을 보내야 함
        console.log('Confirm reservation:', reservationId);
        showNotification('예약이 확정되었습니다.', 'success');
        
        // 테이블에서 상태 업데이트
        updateReservationStatus(reservationId, '확정');
    }
}

// 예약 취소
function cancelReservation(reservationId) {
    if (confirm('이 예약을 취소하시겠습니까?')) {
        // 실제로는 서버에 요청을 보내야 함
        console.log('Cancel reservation:', reservationId);
        showNotification('예약이 취소되었습니다.', 'success');
        
        // 테이블에서 상태 업데이트
        updateReservationStatus(reservationId, '취소');
    }
}

// 예약 상태 업데이트
function updateReservationStatus(reservationId, status) {
    const reservationRow = document.querySelector(`tr[data-reservation-id="${reservationId}"]`);
    if (reservationRow) {
        const statusBadge = reservationRow.querySelector('.status-badge');
        if (statusBadge) {
            statusBadge.textContent = status;
            statusBadge.className = `status-badge status-${status === '완료' ? 'completed' : status === '예약' ? 'reserved' : status === '확정' ? 'confirmed' : 'cancelled'}`;
        }
        
        // 액션 버튼 업데이트
        const actionButtons = reservationRow.querySelector('.action-buttons');
        if (actionButtons) {
            // 기존 버튼들 제거
            const confirmBtn = actionButtons.querySelector('.btn-success');
            const cancelBtn = actionButtons.querySelector('.btn-danger');
            if (confirmBtn) confirmBtn.remove();
            if (cancelBtn) cancelBtn.remove();
            
            // 새로운 상태에 따른 버튼 추가
            if (status === '확정') {
                const completeBtn = document.createElement('button');
                completeBtn.className = 'btn btn-sm btn-success';
                completeBtn.innerHTML = '<i class="fas fa-check-double"></i>';
                completeBtn.onclick = () => completeReservation(reservationId);
                actionButtons.appendChild(completeBtn);
            }
        }
    }
}

// 예약 완료 처리
function completeReservation(reservationId) {
    if (confirm('이 예약을 완료 처리하시겠습니까?')) {
        // 실제로는 서버에 요청을 보내야 함
        console.log('Complete reservation:', reservationId);
        showNotification('예약이 완료 처리되었습니다.', 'success');
        
        // 테이블에서 상태 업데이트
        updateReservationStatus(reservationId, '완료');
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
