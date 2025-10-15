// 관리자 헤더 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // 헤더 검색 기능
    const headerSearchInput = document.querySelector('.header-search .search-input');
    const searchSuggestions = document.querySelector('.search-suggestions');
    
    if (headerSearchInput) {
        // 검색 입력 이벤트
        headerSearchInput.addEventListener('input', function() {
            const searchTerm = this.value.trim();
            
            if (searchTerm.length > 0) {
                showSearchSuggestions(searchTerm);
            } else {
                hideSearchSuggestions();
            }
        });
        
        // Enter 키 이벤트
        headerSearchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const searchTerm = this.value.trim();
                if (searchTerm) {
                    // 전역 검색 수행
                    performGlobalSearch(searchTerm);
                }
            }
        });
        
        // 포커스 이벤트
        headerSearchInput.addEventListener('focus', function() {
            if (this.value.trim().length > 0) {
                showSearchSuggestions(this.value.trim());
            }
        });
        
        // 포커스 아웃 이벤트
        document.addEventListener('click', function(e) {
            if (!headerSearchInput.contains(e.target) && !searchSuggestions?.contains(e.target)) {
                hideSearchSuggestions();
            }
        });
    }
    
    // 알림 버튼 기능
    const notificationBtn = document.querySelector('.notification-btn');
    if (notificationBtn) {
        notificationBtn.addEventListener('click', function() {
            // 알림 패널 토글
            toggleNotificationPanel();
            
            // 알림 읽음 처리
            markNotificationsAsRead();
        });
        
        // 알림 개수 업데이트
        updateNotificationCount();
    }
    
    // 사용자 프로필 드롭다운
    const userProfile = document.querySelector('.user-profile');
    const userDropdown = document.querySelector('.user-dropdown');
    
    if (userProfile && userDropdown) {
        userProfile.addEventListener('click', function(e) {
            e.stopPropagation();
            toggleUserDropdown();
        });
        
        // 드롭다운 외부 클릭 시 닫기
        document.addEventListener('click', function(e) {
            if (!userProfile.contains(e.target)) {
                closeUserDropdown();
            }
        });
    }
    
    // 알림 패널 토글
    function toggleNotificationPanel() {
        // 알림 패널이 없다면 생성
        let notificationPanel = document.querySelector('.notification-panel');
        
        if (!notificationPanel) {
            notificationPanel = createNotificationPanel();
            document.body.appendChild(notificationPanel);
        }
        
        notificationPanel.classList.toggle('show');
    }
    
    // 알림 패널 생성
    function createNotificationPanel() {
        const panel = document.createElement('div');
        panel.className = 'notification-panel';
        panel.innerHTML = `
            <div class="notification-header">
                <h3>알림</h3>
                <button class="close-btn">&times;</button>
            </div>
            <div class="notification-content">
                <div class="notification-item">
                    <div class="notification-icon">🔔</div>
                    <div class="notification-text">
                        <strong>새로운 사용자 가입</strong>
                        <p>김민수님이 가입했습니다.</p>
                        <span class="notification-time">5분 전</span>
                    </div>
                </div>
                <div class="notification-item">
                    <div class="notification-icon">⚠️</div>
                    <div class="notification-text">
                        <strong>신고 접수</strong>
                        <p>사용자 신고가 접수되었습니다.</p>
                        <span class="notification-time">10분 전</span>
                    </div>
                </div>
                <div class="notification-item">
                    <div class="notification-icon">📊</div>
                    <div class="notification-text">
                        <strong>일일 리포트</strong>
                        <p>오늘의 통계 리포트가 준비되었습니다.</p>
                        <span class="notification-time">1시간 전</span>
                    </div>
                </div>
            </div>
        `;
        
        // 닫기 버튼 이벤트
        panel.querySelector('.close-btn').addEventListener('click', function() {
            panel.classList.remove('show');
        });
        
        return panel;
    }
    
    // 알림 읽음 처리
    function markNotificationsAsRead() {
        // 실제로는 서버에 알림 읽음 상태 전송
        console.log('알림을 읽음 처리했습니다.');
        updateNotificationCount(0);
    }
    
    // 알림 개수 업데이트
    function updateNotificationCount(count = 3) {
        let badge = notificationBtn.querySelector('.notification-badge');
        
        if (count > 0) {
            if (!badge) {
                badge = document.createElement('span');
                badge.className = 'notification-badge';
                notificationBtn.appendChild(badge);
            }
            badge.textContent = count > 99 ? '99+' : count;
        } else if (badge) {
            badge.remove();
        }
    }
    
    // 사용자 드롭다운 토글
    function toggleUserDropdown() {
        const dropdownMenu = document.querySelector('.user-dropdown-menu');
        
        if (!dropdownMenu) {
            const menu = createUserDropdownMenu();
            userProfile.appendChild(menu);
        } else {
            dropdownMenu.classList.toggle('show');
        }
    }
    
    // 사용자 드롭다운 닫기
    function closeUserDropdown() {
        const dropdownMenu = document.querySelector('.user-dropdown-menu');
        if (dropdownMenu) {
            dropdownMenu.classList.remove('show');
        }
    }
    
    // 사용자 드롭다운 메뉴 생성
    function createUserDropdownMenu() {
        const menu = document.createElement('div');
        menu.className = 'user-dropdown-menu';
        menu.innerHTML = `

            <a href="/company/edit" class="dropdown-item">
                <i class="fas fa-cog"></i> 설정
            </a>
            <a href="/crm/logout" class="dropdown-item">
                <i class="fas fa-sign-out-alt"></i> 로그아웃
            </a>
        `;
        
        return menu;
    }
    
    // 키보드 단축키
    document.addEventListener('keydown', function(e) {
        // Ctrl + K: 검색 포커스
        if (e.ctrlKey && e.key === 'k') {
            e.preventDefault();
            if (headerSearchInput) {
                headerSearchInput.focus();
            }
        }
        
        // Escape: 검색 제안 닫기
        if (e.key === 'Escape') {
            hideSearchSuggestions();
            closeUserDropdown();
        }
    });
});

// 헤더 관련 유틸리티 함수들
const HeaderUtils = {
    // 검색어 하이라이트
    highlightSearchTerm: function(text, searchTerm) {
        if (!searchTerm) return text;
        
        const regex = new RegExp(`(${searchTerm})`, 'gi');
        return text.replace(regex, '<mark>$1</mark>');
    },
    
    // 알림 추가
    addNotification: function(type, title, message, time) {
        // 실제 알림 추가 로직
        console.log('새 알림:', { type, title, message, time });
    },
    
    // 사용자 정보 업데이트
    updateUserInfo: function(userInfo) {
        const userName = document.querySelector('.user-dropdown .user-name');
        const profileImg = document.querySelector('.profile-img');
        
        if (userName) userName.textContent = userInfo.name;
        if (profileImg && userInfo.avatar) profileImg.src = userInfo.avatar;
    }
};