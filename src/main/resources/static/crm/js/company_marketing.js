// 마케팅 페이지 JavaScript

// 탭 전환
function switchTab(tabName) {
    // 모든 탭 비활성화
    document.querySelectorAll('.tab-item').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    // 선택된 탭 활성화
    document.querySelector(`.tab-item[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(`${tabName}-tab`).classList.add('active');
}

// 탭 클릭 이벤트
document.addEventListener('DOMContentLoaded', function() {
    // 탭 클릭
    document.querySelectorAll('.tab-item').forEach(tab => {
        tab.addEventListener('click', function() {
            switchTab(this.dataset.tab);
        });
    });
    
    // 차트 초기화
    initCharts();
    
    // 에셋 탭 전환
    document.querySelectorAll('.asset-tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.asset-tab-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            // 에셋 타입별 콘텐츠 표시 로직
        });
    });
    
    // 도움말 탭 전환
    document.querySelectorAll('.help-tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.help-tab-btn').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.help-content').forEach(c => c.classList.remove('active'));
            
            this.classList.add('active');
            const tabName = this.dataset.helpTab;
            document.getElementById(`${tabName}Tab`).classList.add('active');
        });
    });
    
    // 발송 시점 라디오 버튼
    document.querySelectorAll('input[name="sendTime"]').forEach(radio => {
        radio.addEventListener('change', function() {
            const scheduledTime = document.getElementById('scheduledTime');
            if (this.value === 'scheduled') {
                scheduledTime.style.display = 'block';
            } else {
                scheduledTime.style.display = 'none';
            }
        });
    });
});

// 차트 초기화
function initCharts() {
    // 노출·클릭 추이 차트
    const trendsCtx = document.getElementById('trendsChart');
    if (trendsCtx) {
        new Chart(trendsCtx, {
            type: 'line',
            data: {
                labels: ['1월', '2월', '3월', '4월', '5월', '6월'],
                datasets: [{
                    label: '노출수',
                    data: [85000, 92000, 98000, 105000, 118000, 125000],
                    borderColor: '#3498db',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    tension: 0.4,
                    fill: true,
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }, {
                    label: '클릭수',
                    data: [5200, 5800, 6400, 7100, 7800, 8500],
                    borderColor: '#27ae60',
                    backgroundColor: 'rgba(39, 174, 96, 0.1)',
                    tension: 0.4,
                    fill: true,
                    borderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                aspectRatio: 2.5,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                        labels: {
                            usePointStyle: true,
                            padding: 15,
                            font: {
                                size: 12
                            }
                        }
                    },
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: {
                            size: 13
                        },
                        bodyFont: {
                            size: 12
                        },
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                label += context.parsed.y.toLocaleString();
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString();
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                },
                interaction: {
                    mode: 'nearest',
                    axis: 'x',
                    intersect: false
                }
            }
        });
    }
    
    // 퍼널 차트
    const funnelCtx = document.getElementById('funnelChart');
    if (funnelCtx) {
        new Chart(funnelCtx, {
            type: 'bar',
            data: {
                labels: ['노출', '클릭', '문의', '예약'],
                datasets: [{
                    label: '마케팅 퍼널',
                    data: [125000, 8500, 450, 180],
                    backgroundColor: [
                        'rgba(52, 152, 219, 0.8)',
                        'rgba(46, 204, 113, 0.8)',
                        'rgba(241, 196, 15, 0.8)',
                        'rgba(231, 76, 60, 0.8)'
                    ]
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                aspectRatio: 2,
                indexAxis: 'y',
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        callbacks: {
                            label: function(context) {
                                return context.parsed.x.toLocaleString() + '건';
                            }
                        }
                    }
                },
                scales: {
                    x: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString();
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    y: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }
    
    // 슬롯별 성과 차트
    const slotCtx = document.getElementById('slotPerformanceChart');
    if (slotCtx) {
        new Chart(slotCtx, {
            type: 'bar',
            data: {
                labels: ['홈 히어로', '홈 카드', '카테고리', '상세', '푸시'],
                datasets: [{
                    label: '클릭수',
                    data: [2450, 1890, 1650, 1320, 1190],
                    backgroundColor: 'rgba(52, 152, 219, 0.7)',
                    borderColor: 'rgba(52, 152, 219, 1)',
                    borderWidth: 1,
                    borderRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                aspectRatio: 2,
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        callbacks: {
                            label: function(context) {
                                return '클릭수: ' + context.parsed.y.toLocaleString();
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return value.toLocaleString();
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
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

// 노출 요청 모달
function openPlacementModal() {
    document.getElementById('placementModal').classList.add('active');
}

function closePlacementModal() {
    document.getElementById('placementModal').classList.remove('active');
}

// 쿠폰 모달
function openCouponModal() {
    document.getElementById('couponModal').classList.add('active');
}

function closeCouponModal() {
    document.getElementById('couponModal').classList.remove('active');
}

// 에셋 업로드
function openAssetUpload() {
    alert('파일 업로드 기능 (추후 구현)');
}

// 메시지 발송
function submitMessage(event) {
    event.preventDefault();
    alert('메시지 발송 요청이 제출되었습니다.');
}

// 모달 외부 클릭 시 닫기
window.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
});

