// 의료 서비스 관리 페이지에서 사이드바 활성 상태 설정
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

