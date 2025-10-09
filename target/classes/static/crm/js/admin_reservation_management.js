// 관리자 예약 캘린더 JavaScript

console.log('관리자 예약 캘린더 JavaScript 로드됨');

let currentDate = new Date();
let currentMonth = currentDate.getMonth();
let currentYear = currentDate.getFullYear();
let selectedDate = null;

// 승인된 이벤트 데이터 (실제로는 서버에서 가져와야 함)
const approvedEvents = [
    {
        id: 1,
        title: '가을맞이 레이저 토닝 할인',
        company: '힝거피부과',
        type: 'event',
        startDate: '2024-10-15',
        endDate: '2024-10-31',
        discount: '30% 할인',
        description: '가을 맞이 특별 이벤트'
    },
    {
        id: 2,
        title: '신규 고객 50% 할인',
        company: '뷰티클리닉',
        type: 'promotion',
        startDate: '2024-10-01',
        endDate: '2024-10-30',
        discount: '50% 할인',
        description: '첫 방문 고객 대상'
    },
    {
        id: 3,
        title: '주말 특가 할인',
        company: '메디컬센터',
        type: 'discount',
        startDate: '2024-10-05',
        endDate: '2024-10-25',
        discount: '주말 한정 20% 할인',
        description: '주말 예약 고객 대상'
    },
    {
        id: 4,
        title: '가을 건강검진 이벤트',
        company: '서울메디컬',
        type: 'event',
        startDate: '2024-10-20',
        endDate: '2024-11-10',
        discount: '종합검진 25% 할인',
        description: '가을 건강검진 특별 이벤트'
    }
];

document.addEventListener('DOMContentLoaded', function() {
    initCalendar();
    setupModalEvents();
});

// 캘린더 초기화
function initCalendar() {
    updateCalendar();
    
    // 이전/다음 달 버튼
    document.getElementById('prevMonth').addEventListener('click', function() {
        currentMonth--;
        if (currentMonth < 0) {
            currentMonth = 11;
            currentYear--;
        }
        updateCalendar();
    });

    document.getElementById('nextMonth').addEventListener('click', function() {
        currentMonth++;
        if (currentMonth > 11) {
            currentMonth = 0;
            currentYear++;
        }
        updateCalendar();
    });
}

// 캘린더 업데이트
function updateCalendar() {
    const monthNames = ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'];
    
    // 월/년도 업데이트
    document.getElementById('currentMonth').textContent = `${currentYear}년 ${monthNames[currentMonth]}`;
    
    // 캘린더 날짜 생성
    generateCalendarDays();
}

// 캘린더 날짜 생성
function generateCalendarDays() {
    const daysContainer = document.getElementById('calendarDays');
    daysContainer.innerHTML = '';

    const firstDay = new Date(currentYear, currentMonth, 1);
    const lastDay = new Date(currentYear, currentMonth + 1, 0);
    const firstDayOfWeek = firstDay.getDay();
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
        
        // 이벤트가 있는 날짜 확인
        const dateString = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        const dayEvents = getEventsForDate(dateString);
        
        if (dayEvents.length > 0) {
            const eventsContainer = document.createElement('div');
            eventsContainer.className = 'day-events';
            
            // 최대 3개의 점만 표시
            const displayEvents = dayEvents.slice(0, 3);
            displayEvents.forEach(event => {
                const dot = document.createElement('div');
                dot.className = `event-dot ${event.type}-type`;
                eventsContainer.appendChild(dot);
            });
            
            dayElement.appendChild(eventsContainer);
        }
        
        daysContainer.appendChild(dayElement);
    }

    // 다음 달 날짜들 추가
    const totalCells = daysContainer.children.length;
    const remainingCells = 42 - totalCells; // 6주 표시
    
    for (let i = 1; i <= remainingCells; i++) {
        const dayElement = createDayElement(i, true);
        daysContainer.appendChild(dayElement);
    }
}

// 날짜 요소 생성
function createDayElement(day, isOtherMonth) {
    const dayElement = document.createElement('div');
    dayElement.className = 'day';
    
    if (isOtherMonth) {
        dayElement.classList.add('other-month');
    }
    
    // 오늘 날짜 표시
    const today = new Date();
    if (!isOtherMonth && 
        day === today.getDate() && 
        currentMonth === today.getMonth() && 
        currentYear === today.getFullYear()) {
        dayElement.classList.add('today');
    }
    
    dayElement.textContent = day;
    
    // 날짜 클릭 이벤트
    dayElement.addEventListener('click', function() {
        if (!isOtherMonth) {
            selectDate(day);
        }
    });

    return dayElement;
}

// 날짜 선택
function selectDate(day) {
    // 모든 날짜에서 선택 상태 제거
    document.querySelectorAll('.day').forEach(d => {
        d.classList.remove('selected');
    });

    // 클릭된 날짜에 선택 상태 추가
    event.target.classList.add('selected');

    const dateString = `${currentYear}-${String(currentMonth + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    selectedDate = dateString;
    
    // 해당 날짜의 이벤트 필터링
    filterEventsByDate(dateString);
}

// 특정 날짜의 이벤트 가져오기
function getEventsForDate(dateString) {
    return approvedEvents.filter(event => {
        const eventStart = new Date(event.startDate);
        const eventEnd = new Date(event.endDate);
        const checkDate = new Date(dateString);
        
        return checkDate >= eventStart && checkDate <= eventEnd;
    });
}

// 날짜별 이벤트 필터링
function filterEventsByDate(dateString) {
    const eventItems = document.querySelectorAll('.event-item');
    const selectedDateTitle = document.getElementById('selectedDateTitle');
    const clearFilterBtn = document.getElementById('clearDateFilter');
    
    const date = new Date(dateString);
    selectedDateTitle.textContent = `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일 일정`;
    clearFilterBtn.style.display = 'inline-flex';
    
    const dayEvents = getEventsForDate(dateString);
    
    eventItems.forEach(item => {
        const itemStartDate = item.getAttribute('data-date');
        const itemEndDate = item.getAttribute('data-end-date');
        
        const start = new Date(itemStartDate);
        const end = new Date(itemEndDate);
        const check = new Date(dateString);
        
        if (check >= start && check <= end) {
            item.style.display = '';
        } else {
            item.style.display = 'none';
        }
    });
}

// 날짜 필터 해제
function clearDateFilter() {
    selectedDate = null;
    const eventItems = document.querySelectorAll('.event-item');
    const selectedDateTitle = document.getElementById('selectedDateTitle');
    const clearFilterBtn = document.getElementById('clearDateFilter');
    
    selectedDateTitle.textContent = '전체 일정';
    clearFilterBtn.style.display = 'none';
    
    // 선택된 날짜 스타일 제거
    document.querySelectorAll('.day').forEach(d => {
        d.classList.remove('selected');
    });
    
    eventItems.forEach(item => {
        item.style.display = '';
    });
    
    // 유형 필터 적용
    filterCalendarEvents();
}

// 유형별 이벤트 필터링
function filterCalendarEvents() {
    const checkboxes = document.querySelectorAll('.filter-checkbox input[type="checkbox"]');
    const selectedTypes = Array.from(checkboxes)
        .filter(cb => cb.checked)
        .map(cb => cb.value);
    
    const eventItems = document.querySelectorAll('.event-item');
    
    eventItems.forEach(item => {
        const itemType = item.getAttribute('data-type');
        
        if (selectedTypes.includes(itemType)) {
            // 날짜 필터가 있는 경우 날짜도 확인
            if (selectedDate) {
                const itemStartDate = item.getAttribute('data-date');
                const itemEndDate = item.getAttribute('data-end-date');
                
                const start = new Date(itemStartDate);
                const end = new Date(itemEndDate);
                const check = new Date(selectedDate);
                
                item.style.display = (check >= start && check <= end) ? '' : 'none';
            } else {
                item.style.display = '';
            }
        } else {
            item.style.display = 'none';
        }
    });
    
    // 캘린더 다시 그리기
    updateCalendar();
}

// 오늘로 이동
function goToToday() {
    const today = new Date();
    currentYear = today.getFullYear();
    currentMonth = today.getMonth();
    updateCalendar();
    
    // 오늘 날짜 선택
    setTimeout(() => {
        const todayElement = document.querySelector('.day.today');
        if (todayElement) {
            todayElement.click();
        }
    }, 100);
}

// Google 캘린더 동기화
function syncGoogleCalendar() {
    if (confirm('Google 캘린더와 동기화하시겠습니까?')) {
        console.log('Google 캘린더 동기화 시작');
        // 실제로는 서버 API 호출
        alert('동기화가 시작되었습니다. 잠시만 기다려주세요.');
    }
}

// 일정 추가 모달 열기
function openAddEventModal() {
    const modal = document.getElementById('addEventModal');
    const form = document.getElementById('addEventForm');
    
    // 폼 초기화
    form.reset();
    
    // 선택된 날짜가 있으면 시작일에 설정
    if (selectedDate) {
        document.getElementById('startDate').value = selectedDate;
    }
    
    modal.style.display = 'block';
}

// 일정 추가 모달 닫기
function closeAddEventModal() {
    const modal = document.getElementById('addEventModal');
    modal.style.display = 'none';
}

// 일정 추가 처리
function processAddEvent() {
    const title = document.getElementById('eventTitle').value;
    const company = document.getElementById('eventCompany').value;
    const type = document.getElementById('eventType').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const discount = document.getElementById('eventDiscount').value;
    const description = document.getElementById('eventDescription').value;
    
    console.log('일정 추가:', { title, company, type, startDate, endDate, discount, description });
    
    // 실제로는 서버에 저장 요청
    
    // 새 이벤트를 배열에 추가
    const newEvent = {
        id: approvedEvents.length + 1,
        title,
        company: document.getElementById('eventCompany').options[document.getElementById('eventCompany').selectedIndex].text,
        type,
        startDate,
        endDate,
        discount,
        description
    };
    
    approvedEvents.push(newEvent);
    
    // 캘린더와 목록 업데이트
    updateCalendar();
    addEventToList(newEvent);
    
    closeAddEventModal();
    alert('일정이 추가되었습니다.');
}

// 이벤트를 목록에 추가
function addEventToList(event) {
    const eventsList = document.getElementById('eventsList');
    
    const eventItem = document.createElement('div');
    eventItem.className = 'event-item';
    eventItem.setAttribute('data-type', event.type);
    eventItem.setAttribute('data-date', event.startDate);
    eventItem.setAttribute('data-end-date', event.endDate);
    
    eventItem.innerHTML = `
        <div class="event-color event-color-${event.type}"></div>
        <div class="event-content">
            <h4 class="event-title">${event.title}</h4>
            <p class="event-company"><i class="fas fa-building"></i> ${event.company}</p>
            <p class="event-period"><i class="fas fa-calendar"></i> ${event.startDate} ~ ${event.endDate}</p>
            <p class="event-discount"><i class="fas fa-tag"></i> ${event.discount}</p>
        </div>
        <div class="event-badge">
            <span class="type-badge type-${event.type}">${getTypeText(event.type)}</span>
        </div>
    `;
    
    eventsList.appendChild(eventItem);
}

// 유형 텍스트 변환
function getTypeText(type) {
    const typeMap = {
        'event': '이벤트',
        'promotion': '프로모션',
        'discount': '할인'
    };
    return typeMap[type] || type;
}

// 모달 이벤트 설정
function setupModalEvents() {
    const addEventForm = document.getElementById('addEventForm');
    const addEventModal = document.getElementById('addEventModal');
    
    // 폼 제출 이벤트
    if (addEventForm) {
        addEventForm.addEventListener('submit', function(e) {
            e.preventDefault();
            processAddEvent();
        });
    }
    
    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target === addEventModal) {
            closeAddEventModal();
        }
    });
}

