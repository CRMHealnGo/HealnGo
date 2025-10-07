// 예약 관리 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    let currentDate = new Date();
    let currentMonth = currentDate.getMonth();
    let currentYear = currentDate.getFullYear();

    // 예약 데이터 (Google Calendar API에서 가져옴)
    let reservations = {};
    
    // Google Calendar API에서 이벤트 데이터 가져오기
    loadGoogleEvents();

    // 달력 초기화
    initCalendar();
    
    // 모달 이벤트 리스너 추가
    setupModalEvents();
    
    // Google Calendar API에서 이벤트 데이터 가져오기
    function loadGoogleEvents() {
        fetch('/api/google/events')
            .then(response => response.json())
            .then(events => {
                // Google Calendar 이벤트를 달력 형식으로 변환
                reservations = {};
                events.forEach(event => {
                    const startDate = new Date(event.start);
                    const dateKey = formatDateKey(startDate);
                    
                    if (!reservations[dateKey]) {
                        reservations[dateKey] = { count: 0, events: [] };
                    }
                    
                    reservations[dateKey].count++;
                    reservations[dateKey].events.push({
                        title: event.summary,
                        time: formatTime(startDate),
                        status: event.status
                    });
                });
                
                // 달력 업데이트
                updateCalendar();
                updateRecentUpdates(events);
            })
            .catch(error => {
                console.error('Google Calendar API 오류:', error);
                // 오류 발생 시 기본 더미 데이터 사용
                reservations = {
                    '2024-10-02': { count: 3, type: 'bar' },
                    '2024-10-11': { count: 2, type: 'bar' },
                    '2024-10-15': { count: 1, type: 'dot' },
                    '2024-10-17': { count: 2, type: 'bar' }
                };
                updateCalendar();
            });
    }
    
    // 날짜를 키 형식으로 변환 (YYYY-MM-DD)
    function formatDateKey(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
    
    // 시간을 HH:MM 형식으로 변환
    function formatTime(date) {
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${hours}:${minutes}`;
    }

    // 이전 달 버튼
    document.getElementById('prevMonth').addEventListener('click', function() {
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        updateCalendar();
    });

    // 다음 달 버튼
    document.getElementById('nextMonth').addEventListener('click', function() {
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        updateCalendar();
    });

    function initCalendar() {
        updateCalendar();
    }

    function updateCalendar() {
        const monthNames = [
            '1월', '2월', '3월', '4월', '5월', '6월',
            '7월', '8월', '9월', '10월', '11월', '12월'
        ];

        // 월/년도 업데이트
        document.getElementById('currentMonth').textContent = 
            `${currentYear}년 ${monthNames[currentMonth]}`;

        // 달력 날짜 생성
        generateCalendarDays();
    }

    function generateCalendarDays() {
        const daysContainer = document.getElementById('calendarDays');
        daysContainer.innerHTML = '';

        // 현재 달의 첫 번째 날과 마지막 날
        const firstDay = new Date(currentYear, currentMonth, 1);
        const lastDay = new Date(currentYear, currentMonth + 1, 0);
        const firstDayOfWeek = firstDay.getDay(); // 0 = 일요일
        const daysInMonth = lastDay.getDate();

        // 이전 달의 마지막 날들
        const prevMonth = new Date(currentYear, currentMonth, 0);
        const prevMonthDays = prevMonth.getDate();

        // 이전 달 날짜들 추가
        for (let i = firstDayOfWeek - 1; i >= 0; i--) {
            const day = prevMonthDays - i;
            const dayElement = createDayElement(day, true);
            daysContainer.appendChild(dayElement);
        }

        // 현재 달 날짜들 추가
        for (let day = 1; day <= daysInMonth; day++) {
            const dayElement = createDayElement(day, false);
            
            // 예약이 있는 날짜 확인
            const dateString = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            const reservation = reservations[dateString];
            
            if (reservation) {
                dayElement.classList.add('has-reservation');
                if (reservation.type === 'bar') {
                    dayElement.classList.add('reservation-bar');
                }
            }

            // 오늘 날짜 표시
            const today = new Date();
            if (currentYear === today.getFullYear() && 
                currentMonth === today.getMonth() && 
                day === today.getDate()) {
                dayElement.classList.add('today');
            }

            daysContainer.appendChild(dayElement);
        }

        // 다음 달 날짜들 추가 (달력을 완성하기 위해)
        const remainingDays = 42 - (firstDayOfWeek + daysInMonth); // 6주 * 7일 = 42
        for (let day = 1; day <= remainingDays; day++) {
            const dayElement = createDayElement(day, true);
            daysContainer.appendChild(dayElement);
        }
    }

    function createDayElement(day, isOtherMonth) {
        const dayElement = document.createElement('div');
        dayElement.className = 'day';
        if (isOtherMonth) {
            dayElement.classList.add('other-month');
        }
        dayElement.textContent = day;
        
        // 날짜 클릭 이벤트
        dayElement.addEventListener('click', function() {
            selectDate(day, isOtherMonth);
        });

        return dayElement;
    }

    function selectDate(day, isOtherMonth) {
        // 모든 날짜에서 선택 상태 제거
        document.querySelectorAll('.day').forEach(d => {
            d.classList.remove('selected');
        });

        // 클릭된 날짜에 선택 상태 추가
        event.target.classList.add('selected');

        if (!isOtherMonth) {
            const selectedDate = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
            console.log('선택된 날짜:', selectedDate);
            
            // 일정 추가 모달 열기
            openEventModal(selectedDate);
        }
    }

    function showDateDetails(date) {
        // 선택된 날짜의 상세 정보를 보여주는 함수
        // 실제로는 모달이나 사이드 패널을 통해 예약 상세 정보를 표시
        console.log('날짜 상세 정보:', date);
    }

    // 예약 추가 기능 (향후 확장용)
    function addReservation(date, time, customer, service) {
        // 새로운 예약을 추가하는 함수
        console.log('새 예약 추가:', { date, time, customer, service });
    }

    // 일정 추가 모달 열기
    function openEventModal(dateString) {
        const modal = document.getElementById('eventModal');
        const dateInput = document.getElementById('eventDate');
        
        // 선택된 날짜를 모달에 설정
        dateInput.value = dateString;
        
        // 기본 시간 설정 (현재 시간)
        const now = new Date();
        const startTime = document.getElementById('eventStartTime');
        const endTime = document.getElementById('eventEndTime');
        
        startTime.value = now.toTimeString().slice(0, 5);
        endTime.value = new Date(now.getTime() + 60 * 60 * 1000).toTimeString().slice(0, 5); // 1시간 후
        
        // 모달 표시
        modal.style.display = 'block';
    }

    // 모달 닫기 (전역 함수로 선언)
    window.closeModal = function() {
        const modal = document.getElementById('eventModal');
        if (modal) {
            modal.style.display = 'none';
            
            // 폼 초기화
            const form = document.getElementById('eventForm');
            if (form) {
                form.reset();
            }
        }
    };

    // 모달 이벤트 설정
    function setupModalEvents() {
        const modal = document.getElementById('eventModal');
        const form = document.getElementById('eventForm');
        
        // 모달 바깥쪽 클릭 시 닫기
        window.addEventListener('click', function(event) {
            if (event.target === modal) {
                window.closeModal();
            }
        });
        
        // 폼 제출 이벤트
        form.addEventListener('submit', function(event) {
            event.preventDefault();
            createEvent();
        });
        
        // ESC 키로 모달 닫기
        document.addEventListener('keydown', function(event) {
            if (event.key === 'Escape') {
                window.closeModal();
            }
        });
    }

    // 일정 생성 함수
    function createEvent() {
        const form = document.getElementById('eventForm');
        const formData = new FormData(form);
        
        const eventData = {
            title: formData.get('title'),
            date: formData.get('date'),
            startTime: formData.get('startTime'),
            endTime: formData.get('endTime'),
            description: formData.get('description'),
            location: formData.get('location'),
            googleSyncEnabled: formData.get('googleSyncEnabled') === 'true'
        };
        
        // API 호출
        fetch('/api/google/events', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(eventData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                let message = '일정이 성공적으로 추가되었습니다!';
                if (data.googleSyncEnabled) {
                    message += '\n구글 캘린더와 동기화되었습니다.';
                }
                alert(message);
                window.closeModal();
                // 달력 새로고침
                loadGoogleEvents();
            } else {
                alert('일정 추가 중 오류가 발생했습니다: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('일정 추가 중 오류가 발생했습니다.');
        });
    }

    // 예약 수정 기능 (향후 확장용)
    function editReservation(reservationId, newData) {
        // 기존 예약을 수정하는 함수
        console.log('예약 수정:', reservationId, newData);
    }

    // 예약 삭제 기능 (향후 확장용)
    function deleteReservation(reservationId) {
        // 예약을 삭제하는 함수
        console.log('예약 삭제:', reservationId);
    }

    // 키보드 단축키
    document.addEventListener('keydown', function(e) {
        if (e.key === 'ArrowLeft') {
            document.getElementById('prevMonth').click();
        } else if (e.key === 'ArrowRight') {
            document.getElementById('nextMonth').click();
        }
    });

    // 오늘 날짜로 이동
    function goToToday() {
        const today = new Date();
        currentYear = today.getFullYear();
        currentMonth = today.getMonth();
        updateCalendar();
    }

    // 오늘 버튼 추가 (선택사항)
    const todayBtn = document.createElement('button');
    todayBtn.textContent = '오늘';
    todayBtn.className = 'today-btn';
    todayBtn.addEventListener('click', goToToday);
    document.querySelector('.calendar-header').appendChild(todayBtn);
});
