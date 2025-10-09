// 공지사항 & 알림 관리 JavaScript

console.log('공지사항 & 알림 관리 JavaScript 로드됨');

document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM 로드 완료');
    
    // 모달 이벤트 설정
    setupModalEvents();
});

// ========== 탭 전환 ==========
function switchTab(tabName) {
    // 모든 탭 버튼과 콘텐츠에서 active 클래스 제거
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // 선택된 탭 활성화
    event.target.classList.add('active');
    if (tabName === 'notice') {
        document.getElementById('noticeTab').classList.add('active');
    } else if (tabName === 'notify') {
        document.getElementById('notifyTab').classList.add('active');
    }
}

// ========== 공지사항 관련 함수 ==========

// 공지사항 검색
function searchNotices() {
    const searchInput = document.getElementById('noticeSearchInput').value.toLowerCase();
    const noticeItems = document.querySelectorAll('.notice-item');
    
    noticeItems.forEach(item => {
        const title = item.querySelector('.notice-title').textContent.toLowerCase();
        const description = item.querySelector('.notice-description').textContent.toLowerCase();
        
        if (title.includes(searchInput) || description.includes(searchInput)) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// 공지사항 필터링
function filterNotices() {
    const statusFilter = document.getElementById('noticeStatusFilter').value;
    const typeFilter = document.getElementById('noticeTypeFilter').value;
    const noticeItems = document.querySelectorAll('.notice-item');
    
    noticeItems.forEach(item => {
        const status = item.getAttribute('data-status');
        const type = item.getAttribute('data-type');
        
        let showStatus = !statusFilter || status === statusFilter;
        let showType = !typeFilter || type === typeFilter;
        
        if (showStatus && showType) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// 공지사항 모달 열기
function openNoticeModal() {
    const modal = document.getElementById('noticeModal');
    const form = document.getElementById('noticeForm');
    
    // 폼 초기화
    form.reset();
    
    // 모달 제목 변경
    modal.querySelector('.modal-header h2').textContent = '공지사항 작성';
    
    modal.style.display = 'block';
}

// 공지사항 모달 닫기
function closeNoticeModal() {
    const modal = document.getElementById('noticeModal');
    modal.style.display = 'none';
}

// 공지사항 수정
function editNotice(button) {
    const item = button.closest('.notice-item');
    const title = item.querySelector('.notice-title').textContent;
    const description = item.querySelector('.notice-description').textContent;
    const type = item.getAttribute('data-type');
    
    const modal = document.getElementById('noticeModal');
    
    // 모달 제목 변경
    modal.querySelector('.modal-header h2').textContent = '공지사항 수정';
    
    // 폼에 데이터 채우기
    document.getElementById('noticeTitle').value = title;
    document.getElementById('noticeContent').value = description;
    document.getElementById('noticeType').value = type;
    
    modal.style.display = 'block';
}

// 공지사항 삭제
function deleteNotice(button) {
    const item = button.closest('.notice-item');
    const title = item.querySelector('.notice-title').textContent;
    
    if (confirm(`"${title}" 공지사항을 삭제하시겠습니까?`)) {
        item.style.opacity = '0';
        item.style.transform = 'scale(0.8)';
        item.style.transition = 'all 0.3s ease';
        
        setTimeout(() => {
            item.remove();
            alert('공지사항이 삭제되었습니다.');
        }, 300);
    }
}

// ========== 알림 관련 함수 ==========

// 알림 검색
function searchNotifications() {
    const searchInput = document.getElementById('notifySearchInput').value.toLowerCase();
    const notifyItems = document.querySelectorAll('.notify-item');
    
    notifyItems.forEach(item => {
        const title = item.querySelector('.notify-title').textContent.toLowerCase();
        const message = item.querySelector('.notify-message').textContent.toLowerCase();
        
        if (title.includes(searchInput) || message.includes(searchInput)) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// 알림 필터링
function filterNotifications() {
    const typeFilter = document.getElementById('notifyTypeFilter').value;
    const dateFrom = document.getElementById('notifyDateFrom').value;
    const dateTo = document.getElementById('notifyDateTo').value;
    const notifyItems = document.querySelectorAll('.notify-item');
    
    notifyItems.forEach(item => {
        const type = item.getAttribute('data-type');
        
        let showType = !typeFilter || type === typeFilter;
        
        // 날짜 필터는 실제 데이터와 연동 필요
        // 현재는 유형만 필터링
        
        if (showType) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// 알림 모달 열기
function openNotifyModal() {
    const modal = document.getElementById('notifyModal');
    const form = document.getElementById('notifyForm');
    
    // 폼 초기화
    form.reset();
    
    modal.style.display = 'block';
}

// 알림 모달 닫기
function closeNotifyModal() {
    const modal = document.getElementById('notifyModal');
    modal.style.display = 'none';
}

// 발송 대상 정보 업데이트
function updateTargetInfo() {
    const target = document.getElementById('notifyTarget').value;
    const targetInfo = document.getElementById('targetInfo');
    
    const infoTexts = {
        'all': '전체 사용자 및 업체에 발송됩니다',
        'users': '모든 사용자에게 발송됩니다',
        'companies': '모든 업체에 발송됩니다',
        'custom': '직접 선택한 대상에게 발송됩니다'
    };
    
    targetInfo.textContent = infoTexts[target] || '';
}

// 예약 발송 시간 입력 토글
function toggleScheduleDateTime() {
    const schedule = document.getElementById('notifySchedule').value;
    const scheduleDateTime = document.getElementById('scheduleDateTime');
    
    if (schedule === 'scheduled') {
        scheduleDateTime.style.display = 'block';
        document.getElementById('notifyDateTime').required = true;
    } else {
        scheduleDateTime.style.display = 'none';
        document.getElementById('notifyDateTime').required = false;
    }
}

// ========== 모달 이벤트 설정 ==========
function setupModalEvents() {
    const noticeModal = document.getElementById('noticeModal');
    const notifyModal = document.getElementById('notifyModal');
    const noticeForm = document.getElementById('noticeForm');
    const notifyForm = document.getElementById('notifyForm');
    
    // 모달 바깥쪽 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target === noticeModal) {
            closeNoticeModal();
        }
        if (event.target === notifyModal) {
            closeNotifyModal();
        }
    });
    
    // 공지사항 폼 제출
    if (noticeForm) {
        noticeForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(noticeForm);
            const data = Object.fromEntries(formData.entries());
            
            console.log('공지사항 저장:', data);
            
            // 여기에 실제 저장 로직 추가 (API 호출 등)
            alert('공지사항이 저장되었습니다.');
            closeNoticeModal();
            
            // 페이지 새로고침 또는 목록 업데이트
            // location.reload();
        });
    }
    
    // 알림 폼 제출
    if (notifyForm) {
        notifyForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            const formData = new FormData(notifyForm);
            const data = Object.fromEntries(formData.entries());
            
            console.log('알림 전송:', data);
            
            // 여기에 실제 전송 로직 추가 (API 호출 등)
            if (data.schedule === 'now') {
                if (confirm('알림을 즉시 전송하시겠습니까?')) {
                    alert('알림이 전송되었습니다.');
                    closeNotifyModal();
                }
            } else {
                alert('알림이 예약되었습니다.');
                closeNotifyModal();
            }
            
            // 페이지 새로고침 또는 목록 업데이트
            // location.reload();
        });
    }
}

