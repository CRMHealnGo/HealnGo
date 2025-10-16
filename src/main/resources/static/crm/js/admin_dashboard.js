// 사용자 차트 초기화
document.addEventListener('DOMContentLoaded', function() {
    const ctx = document.getElementById('userChart');
    if (ctx) {
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['4월', '5월', '6월', '7월', '8월', '9월', '10월'],
                datasets: [{
                    label: '사용객',
                    data: [50, 60, 52, 44, 58, 62, 80],
                    borderColor: '#3498db',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
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
});