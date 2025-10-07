// 관리자 대시보드 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeCharts();
    initializeEventListeners();
});

// 차트 초기화
function initializeCharts() {
    // 사용자 증가 추이 차트
    const userGrowthCtx = document.getElementById('userGrowthChart');
    if (userGrowthCtx) {
        new Chart(userGrowthCtx, {
            type: 'line',
            data: {
                labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
                datasets: [{
                    label: '사용자 수',
                    data: [120, 150, 180, 200, 220, 250],
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }

    // 예약 현황 차트
    const reservationCtx = document.getElementById('reservationChart');
    if (reservationCtx) {
        new Chart(reservationCtx, {
            type: 'bar',
            data: {
                labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
                datasets: [{
                    label: '예약 수',
                    data: [45, 60, 75, 90, 85, 95],
                    backgroundColor: 'rgba(40, 167, 69, 0.8)',
                    borderColor: '#28a745',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 차트 기간 변경
    const chartPeriods = document.querySelectorAll('.admin-chart-period');
    chartPeriods.forEach(select => {
        select.addEventListener('change', function() {
            updateChartData(this.value);
        });
    });

    // 리포트 다운로드 버튼
    const downloadBtn = document.querySelector('.admin-btn-primary');
    if (downloadBtn) {
        downloadBtn.addEventListener('click', function() {
            downloadReport();
        });
    }
}

// 차트 데이터 업데이트
function updateChartData(period) {
    // 실제로는 서버에서 데이터를 가져와야 함
    console.log('차트 기간 변경:', period);
    
    // 임시 데이터
    let userData, reservationData;
    
    switch(period) {
        case 'month':
            userData = [120, 150, 180, 200, 220, 250];
            reservationData = [45, 60, 75, 90, 85, 95];
            break;
        case 'quarter':
            userData = [300, 450, 600];
            reservationData = [180, 270, 360];
            break;
        case 'year':
            userData = [1200, 1500, 1800, 2000, 2200, 2500, 2800, 3000, 3200, 3500, 3800, 4000];
            reservationData = [450, 600, 750, 900, 850, 950, 1000, 1100, 1200, 1300, 1400, 1500];
            break;
    }
    
    // 차트 업데이트 로직 (실제 구현 시 Chart.js의 update() 메서드 사용)
    console.log('사용자 데이터:', userData);
    console.log('예약 데이터:', reservationData);
}

// 리포트 다운로드
function downloadReport() {
    // 실제로는 서버에서 리포트를 생성하고 다운로드 링크를 제공해야 함
    console.log('리포트 다운로드 시작...');
    
    // 임시로 알림 표시
    showNotification('리포트 다운로드가 시작되었습니다.', 'success');
}

// 알림 표시
function showNotification(message, type = 'info') {
    // 알림 요소 생성
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'info-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    // 스타일 추가
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#d4edda' : '#d1ecf1'};
        color: ${type === 'success' ? '#155724' : '#0c5460'};
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

// 실시간 데이터 업데이트 (WebSocket 또는 Polling 사용)
function updateRealTimeData() {
    // 실제로는 서버에서 실시간 데이터를 가져와야 함
    console.log('실시간 데이터 업데이트...');
    
    // 통계 카드 업데이트
    updateStatCards();
    
    // 최근 활동 업데이트
    updateRecentActivities();
}

// 통계 카드 업데이트
function updateStatCards() {
    // 실제로는 서버에서 최신 통계를 가져와야 함
    const statNumbers = document.querySelectorAll('.admin-stat-number');
    statNumbers.forEach(stat => {
        // 숫자 애니메이션 효과
        animateNumber(stat);
    });
}

// 숫자 애니메이션
function animateNumber(element) {
    const target = parseInt(element.textContent.replace(/,/g, ''));
    const duration = 1000;
    const start = 0;
    const increment = target / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            current = target;
            clearInterval(timer);
        }
        element.textContent = Math.floor(current).toLocaleString();
    }, 16);
}

// 최근 활동 업데이트
function updateRecentActivities() {
    // 실제로는 서버에서 최신 활동을 가져와야 함
    console.log('최근 활동 업데이트...');
}

// 페이지 로드 시 실시간 업데이트 시작
setInterval(updateRealTimeData, 30000); // 30초마다 업데이트
