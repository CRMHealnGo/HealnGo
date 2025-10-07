// 업체 모드 대시보드 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // 사이드바 네비게이션 설정
    setupNavigation();
    
    // 로그아웃 이벤트
    setupLogout();
    
    // 통계 카드 애니메이션
    animateStats();
    
    // 실시간 데이터 업데이트
    setupRealTimeUpdates();
});

// 사이드바 네비게이션 설정
function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            // 모든 아이템에서 active 클래스 제거
            navItems.forEach(nav => nav.classList.remove('active'));
            
            // 클릭된 아이템에 active 클래스 추가
            this.classList.add('active');
            
            // 실제 네비게이션 로직
            const navText = this.querySelector('span').textContent;
            handleNavigation(navText);
        });
    });
}

// 네비게이션 처리
function handleNavigation(navText) {
    const routes = {
        'Dashboard': '/company/dashboard',
        '의료 서비스 관리': '/company/medical-services',
        '예약 관리': '/company/reservations',
        '후기 관리': '/company/reviews',
        '문의 & 채팅': '/company/inquiries',
        '마케팅': '/company/marketing',
        '리포트 & 통계': '/company/reports',
        '업체 정보 수정': '/company/company-info',
        '도움말/고객센터': '/company/help'
    };
    
    const route = routes[navText];
    if (route) {
        console.log('네비게이션:', navText, '→', route);
        // 실제 페이지 이동 (추후 구현)
        // window.location.href = route;
    }
}

// 로그아웃 설정
function setupLogout() {
    const logoutLink = document.querySelector('.logout-link');
    
    if (logoutLink) {
        logoutLink.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('정말 로그아웃 하시겠습니까?')) {
                // 로그아웃 로직 (추후 구현)
                console.log('업체 모드 로그아웃');
                // window.location.href = '/logout';
            }
        });
    }
}

// 통계 카드 애니메이션
function animateStats() {
    const statCards = document.querySelectorAll('.stat-card');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, { threshold: 0.1 });
    
    statCards.forEach(card => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(card);
    });
}

// 실시간 데이터 업데이트
function setupRealTimeUpdates() {
    // 5분마다 데이터 새로고침
    setInterval(updateDashboardData, 300000);
    
    // 실시간 알림 (WebSocket 또는 Server-Sent Events)
    setupRealTimeNotifications();
}

// 대시보드 데이터 업데이트
function updateDashboardData() {
    console.log('대시보드 데이터 업데이트 중...');
    
    // AJAX로 최신 데이터 가져오기
    fetch('/company/api/dashboard-data')
        .then(response => response.json())
        .then(data => {
            updateStatsCards(data.stats);
            updateReservationList(data.reservations);
            updateReviewList(data.reviews);
        })
        .catch(error => {
            console.error('데이터 업데이트 실패:', error);
        });
}

// 통계 카드 업데이트
function updateStatsCards(stats) {
    if (stats) {
        const statCards = document.querySelectorAll('.stat-content h3');
        if (statCards[0]) statCards[0].textContent = stats.totalReservations || '0';
        if (statCards[1]) statCards[1].textContent = stats.todayReservations || '0';
        if (statCards[2]) statCards[2].textContent = stats.pendingReservations || '0';
        if (statCards[3]) statCards[3].textContent = stats.averageRating || '0.0';
    }
}

// 예약 리스트 업데이트
function updateReservationList(reservations) {
    if (reservations && reservations.length > 0) {
        const reservationList = document.querySelector('.reservation-list');
        if (reservationList) {
            // 기존 내용 제거
            reservationList.innerHTML = '';
            
            // 새로운 예약 데이터 추가
            reservations.forEach(reservation => {
                const reservationItem = createReservationItem(reservation);
                reservationList.appendChild(reservationItem);
            });
        }
    }
}

// 예약 아이템 생성
function createReservationItem(reservation) {
    const item = document.createElement('div');
    item.className = 'reservation-item';
    
    item.innerHTML = `
        <div class="reservation-info">
            <h4>${reservation.customerName}</h4>
            <p>${reservation.service}</p>
            <span class="reservation-date">${reservation.date} ${reservation.time}</span>
        </div>
        <div class="reservation-status status-${reservation.status}">
            ${reservation.status}
        </div>
    `;
    
    return item;
}

// 후기 리스트 업데이트
function updateReviewList(reviews) {
    if (reviews && reviews.length > 0) {
        const reviewList = document.querySelector('.review-list');
        if (reviewList) {
            // 기존 내용 제거
            reviewList.innerHTML = '';
            
            // 새로운 후기 데이터 추가
            reviews.forEach(review => {
                const reviewItem = createReviewItem(review);
                reviewList.appendChild(reviewItem);
            });
        }
    }
}

// 후기 아이템 생성
function createReviewItem(review) {
    const item = document.createElement('div');
    item.className = 'review-item';
    
    const stars = Array.from({length: 5}, (_, i) => 
        `<i class="fas fa-star ${i < review.rating ? 'active' : ''}"></i>`
    ).join('');
    
    item.innerHTML = `
        <div class="review-header">
            <h4>${review.customerName}</h4>
            <div class="rating">
                ${stars}
            </div>
        </div>
        <p class="review-content">${review.content}</p>
        <span class="review-date">${review.date}</span>
    `;
    
    return item;
}

// 실시간 알림 설정
function setupRealTimeNotifications() {
    // 새로운 예약 알림
    const newReservationSound = new Audio('/resources/sounds/notification.mp3');
    
    // 새로운 후기 알림
    const newReviewSound = new Audio('/resources/sounds/review.mp3');
    
    // 실시간 알림 처리 (추후 WebSocket 구현)
    console.log('실시간 알림 설정 완료');
}

// 예약 상태 변경
function changeReservationStatus(reservationId, newStatus) {
    fetch(`/company/api/reservations/${reservationId}/status`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: newStatus })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log('예약 상태 변경 완료:', newStatus);
            // UI 업데이트
            updateDashboardData();
        }
    })
    .catch(error => {
        console.error('예약 상태 변경 실패:', error);
    });
}

// 후기 답글 작성
function replyToReview(reviewId, reply) {
    fetch(`/company/api/reviews/${reviewId}/reply`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ reply: reply })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            console.log('후기 답글 작성 완료');
            updateDashboardData();
        }
    })
    .catch(error => {
        console.error('후기 답글 작성 실패:', error);
    });
}

// 반응형 처리
function handleResize() {
    const sidebar = document.querySelector('.sidebar');
    const mainContent = document.querySelector('.main-content');
    
    if (window.innerWidth <= 768) {
        sidebar.style.position = 'static';
        sidebar.style.height = 'auto';
        mainContent.style.marginLeft = '0';
        mainContent.style.width = '100%';
    } else {
        sidebar.style.position = 'fixed';
        sidebar.style.height = '100vh';
        mainContent.style.marginLeft = '280px';
        mainContent.style.width = 'calc(100% - 280px)';
    }
}

window.addEventListener('resize', handleResize);

// 초기화
document.addEventListener('DOMContentLoaded', function() {
    handleResize();
});
