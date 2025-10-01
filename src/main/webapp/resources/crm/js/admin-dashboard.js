// 관리자 대시보드 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // 사용자 차트 생성
    createUserChart();
    
    // 캘린더 생성
    createCalendar();
    
    // 사이드바 네비게이션 이벤트
    setupNavigation();
    
    // 검색 기능
    setupSearch();
});

// 사용자 차트 생성
function createUserChart() {
    const ctx = document.getElementById('userChart').getContext('2d');
    
    const userChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['4월', '5월', '6월', '7월', '8월', '9월', '10월'],
            datasets: [{
                label: '외국인 사용객',
                data: [36, 24, 30, 55, 73, 95, 89],
                borderColor: '#e74c3c',
                backgroundColor: 'rgba(231, 76, 60, 0.1)',
                tension: 0.4,
                fill: false
            }, {
                label: '한국인 사용객',
                data: [50, 60, 52, 44, 59, 80, 70],
                borderColor: '#3498db',
                backgroundColor: 'rgba(52, 152, 219, 0.1)',
                tension: 0.4,
                fill: false
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        stepSize: 20
                    }
                }
            },
            elements: {
                point: {
                    radius: 4,
                    hoverRadius: 6
                }
            }
        }
    });
}

// 캘린더 생성
function createCalendar() {
    const calendarGrid = document.querySelector('.calendar-grid');
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = 9; // 10월 (0부터 시작하므로 9)
    
    // 10월 1일
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const startDate = new Date(firstDay);
    startDate.setDate(startDate.getDate() - firstDay.getDay());
    
    // 캘린더 그리드 초기화
    calendarGrid.innerHTML = '';
    
    // 6주 * 7일 = 42일
    for (let i = 0; i < 42; i++) {
        const date = new Date(startDate);
        date.setDate(startDate.getDate() + i);
        
        const dayElement = document.createElement('div');
        dayElement.className = 'calendar-day';
        dayElement.textContent = date.getDate();
        
        // 오늘 날짜 표시
        if (date.getDate() === 19 && date.getMonth() === month) {
            dayElement.classList.add('today');
            dayElement.textContent = '오늘';
        }
        
        // 특정 날짜에 마크 표시
        const markedDates = [2, 9, 11, 17];
        if (markedDates.includes(date.getDate()) && date.getMonth() === month) {
            dayElement.classList.add('marked');
        }
        
        // 10월이 아닌 날짜는 회색으로
        if (date.getMonth() !== month) {
            dayElement.style.color = '#bdc3c7';
        }
        
        calendarGrid.appendChild(dayElement);
    }
}

// 사이드바 네비게이션 설정
function setupNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    
    navItems.forEach(item => {
        item.addEventListener('click', function() {
            // 모든 아이템에서 active 클래스 제거
            navItems.forEach(nav => nav.classList.remove('active'));
            
            // 클릭된 아이템에 active 클래스 추가
            this.classList.add('active');
            
            // 실제 네비게이션 로직 (추후 구현)
            const navText = this.querySelector('span').textContent;
            console.log('네비게이션:', navText);
        });
    });
    
    // 로그아웃 이벤트
    const logoutLink = document.querySelector('.logout-link');
    if (logoutLink) {
        logoutLink.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('정말 로그아웃 하시겠습니까?')) {
                // 로그아웃 로직 (추후 구현)
                console.log('로그아웃');
            }
        });
    }
}

// 검색 기능 설정
function setupSearch() {
    const searchInput = document.querySelector('.search-input');
    
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                const searchTerm = this.value.trim();
                if (searchTerm) {
                    console.log('검색:', searchTerm);
                    // 검색 로직 (추후 구현)
                }
            }
        });
    }
}

// 사용자 프로필 드롭다운
function setupUserProfile() {
    const userProfile = document.querySelector('.user-profile');
    
    if (userProfile) {
        userProfile.addEventListener('click', function() {
            // 프로필 드롭다운 토글 (추후 구현)
            console.log('프로필 드롭다운');
        });
    }
}

// 알림 기능
function setupNotifications() {
    const notification = document.querySelector('.notification');
    
    if (notification) {
        notification.addEventListener('click', function() {
            // 알림 패널 토글 (추후 구현)
            console.log('알림 패널');
        });
    }
}

// 데이터 새로고침 함수
function refreshDashboard() {
    // 차트 데이터 새로고침
    if (window.userChart) {
        window.userChart.update();
    }
    
    // 캘린더 새로고침
    createCalendar();
    
    console.log('대시보드 데이터 새로고침');
}

// 실시간 데이터 업데이트 (5분마다)
setInterval(refreshDashboard, 300000);

// 반응형 처리
function handleResize() {
    const chart = document.getElementById('userChart');
    if (chart && window.userChart) {
        window.userChart.resize();
    }
}

window.addEventListener('resize', handleResize);

// 초기화 완료 후 실행
document.addEventListener('DOMContentLoaded', function() {
    setupUserProfile();
    setupNotifications();
});
