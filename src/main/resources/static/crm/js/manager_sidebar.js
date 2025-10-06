// 사이드바 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // 네비게이션 링크 클릭 이벤트
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // 모든 활성 클래스 제거
            navLinks.forEach(l => l.classList.remove('active'));
            navLinks.forEach(l => l.parentElement.classList.remove('active'));
            
            // 현재 클릭된 링크에 활성 클래스 추가
            this.classList.add('active');
            this.parentElement.classList.add('active');
            
            // 실제 페이지 이동 로직 (필요시 구현)
            const href = this.getAttribute('href');
            if (href && href !== '#') {
                // 페이지 이동 로직
                console.log('Navigate to:', href);
            }
        });
    });
    
    // 로그아웃 버튼 클릭 이벤트
    const logoutBtn = document.querySelector('.logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            if (confirm('정말 로그아웃 하시겠습니까?')) {
                // 로그아웃 로직 (필요시 구현)
                console.log('Logout clicked');
                // window.location.href = '/logout';
            }
        });
    }
    
    // 모바일에서 사이드바 토글 (필요시)
    const sidebarToggle = document.querySelector('.sidebar-toggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.querySelector('.sidebar').classList.toggle('open');
        });
    }
    
    // 사용자 드롭다운 토글 (헤더에서)
    const userDropdown = document.querySelector('.user-dropdown');
    if (userDropdown) {
        userDropdown.addEventListener('click', function() {
            // 드롭다운 메뉴 토글 로직 (필요시 구현)
            console.log('User dropdown clicked');
        });
    }
    
    // 알림 버튼 클릭 이벤트
    const notificationBtn = document.querySelector('.notification-btn');
    if (notificationBtn) {
        notificationBtn.addEventListener('click', function() {
            // 알림 패널 토글 로직 (필요시 구현)
            console.log('Notification clicked');
        });
    }
});

// 사이드바 관련 유틸리티 함수들
const SidebarUtils = {
    // 현재 활성 메뉴 설정
    setActiveMenu: function(menuId) {
        const navLinks = document.querySelectorAll('.nav-link');
        navLinks.forEach(link => {
            link.classList.remove('active');
            link.parentElement.classList.remove('active');
            
            if (link.getAttribute('data-menu') === menuId) {
                link.classList.add('active');
                link.parentElement.classList.add('active');
            }
        });
    },
    
    // 사이드바 토글 (모바일용)
    toggleSidebar: function() {
        const sidebar = document.querySelector('.sidebar');
        if (sidebar) {
            sidebar.classList.toggle('open');
        }
    },
    
    // 사이드바 닫기 (모바일용)
    closeSidebar: function() {
        const sidebar = document.querySelector('.sidebar');
        if (sidebar) {
            sidebar.classList.remove('open');
        }
    }
};
