// 예약내역 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 예약내역 아이템 클릭 이벤트
    const bookingItems = document.querySelectorAll('.booking-item');
    
    bookingItems.forEach(item => {
        item.addEventListener('click', function() {
            const bookingId = this.querySelector('.booking-id').textContent;
            const bookingName = this.querySelector('.booking-name').textContent;
            
            // 예약 상세 정보 표시 (실제로는 상세 페이지로 이동)
            showBookingDetail(bookingId, bookingName);
        });
    });
    
    // 페이지네이션 버튼 이벤트
    const pageButtons = document.querySelectorAll('.page-btn');
    
    pageButtons.forEach(button => {
        button.addEventListener('click', function() {
            // 현재 활성 페이지 제거
            document.querySelectorAll('.page-btn').forEach(btn => {
                btn.classList.remove('active');
            });
            
            // 클릭된 버튼 활성화
            this.classList.add('active');
            
            // 페이지 변경 처리
            const pageNumber = this.textContent.trim();
            if (pageNumber && !isNaN(pageNumber)) {
                loadBookingPage(parseInt(pageNumber));
            } else if (this.querySelector('.fa-chevron-right')) {
                // 다음 페이지
                const currentPage = getCurrentPage();
                if (currentPage < 5) {
                    loadBookingPage(currentPage + 1);
                }
            } else if (this.querySelector('.fa-angle-double-right')) {
                // 마지막 페이지
                loadBookingPage(5);
            }
        });
    });
});

// 예약 상세 정보 표시
function showBookingDetail(bookingId, bookingName) {
    // 모달 표시
    showBookingModal(bookingId, bookingName);
}

// 예약 상세 모달 표시
function showBookingModal(bookingId, bookingName) {
    // 모달 HTML 생성
    const modalHTML = `
        <div id="bookingDetailModal" class="booking-detail-modal">
            <div class="booking-detail-content">
                <div class="booking-detail-header">
                    <h3>예약 상세 정보</h3>
                    <button class="booking-close-btn" onclick="closeBookingModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="booking-detail-body">
                    <div class="booking-info-section">
                        <div class="info-row">
                            <label>예약 번호</label>
                            <span>${bookingId}</span>
                        </div>
                        <div class="info-row">
                            <label>병원명</label>
                            <span>${bookingName}</span>
                        </div>
                        <div class="info-row">
                            <label>예약일</label>
                            <span>2024.01.15</span>
                        </div>
                        <div class="info-row">
                            <label>예약 시간</label>
                            <span>14:00</span>
                        </div>
                        <div class="info-row">
                            <label>진료과목</label>
                            <span>성형외과</span>
                        </div>
                        <div class="info-row">
                            <label>담당의</label>
                            <span>김의사</span>
                        </div>
                        <div class="info-row">
                            <label>상태</label>
                            <span class="status confirmed">확정</span>
                        </div>
                    </div>
                    <div class="booking-actions">
                        <button class="booking-btn cancel" onclick="cancelBooking('${bookingId}')">예약 취소</button>
                        <button class="booking-btn modify" onclick="modifyBooking('${bookingId}')">예약 변경</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // 기존 모달 제거
    const existingModal = document.getElementById('bookingDetailModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // 모달 추가
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // 모달 표시
    const modal = document.getElementById('bookingDetailModal');
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
}

// 예약 모달 닫기
function closeBookingModal() {
    const modal = document.getElementById('bookingDetailModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = '';
        setTimeout(() => {
            modal.remove();
        }, 300);
    }
}

// 예약 취소
function cancelBooking(bookingId) {
    if (confirm('정말로 예약을 취소하시겠습니까?')) {
        alert('예약이 취소되었습니다.');
        closeBookingModal();
    }
}

// 예약 변경
function modifyBooking(bookingId) {
    alert('예약 변경 기능은 준비 중입니다.');
}

// 현재 페이지 번호 가져오기
function getCurrentPage() {
    const activeButton = document.querySelector('.page-btn.active');
    return activeButton ? parseInt(activeButton.textContent) : 1;
}

// 예약내역 페이지 로드
function loadBookingPage(pageNumber) {
    console.log(`페이지 ${pageNumber} 로드 중...`);
    
    // 실제로는 서버에서 데이터를 가져와야 함
    // fetch(`/api/booking?page=${pageNumber}`)
    //     .then(response => response.json())
    //     .then(data => {
    //         updateBookingList(data);
    //     });
    
    // 임시로 페이지 번호만 업데이트
    updatePageButtons(pageNumber);
}

// 페이지 버튼 업데이트
function updatePageButtons(currentPage) {
    const buttons = document.querySelectorAll('.page-btn');
    
    buttons.forEach((button, index) => {
        button.classList.remove('active');
        
        // 숫자 버튼들
        if (index < 5) {
            const pageNum = index + 1;
            if (pageNum === currentPage) {
                button.classList.add('active');
            }
        }
    });
}

// 예약내역 목록 업데이트
function updateBookingList(data) {
    const container = document.querySelector('.booking-list-container');
    
    // 기존 목록 제거
    container.innerHTML = '';
    
    // 새 목록 추가
    data.bookings.forEach(booking => {
        const bookingItem = createBookingItem(booking);
        container.appendChild(bookingItem);
    });
}

// 예약 아이템 생성
function createBookingItem(booking) {
    const item = document.createElement('div');
    item.className = 'booking-item';
    
    item.innerHTML = `
        <div class="booking-icon">
            <i class="fas fa-folder"></i>
        </div>
        <div class="booking-info">
            <div class="booking-id">${booking.id}</div>
            <div class="booking-name">${booking.name}</div>
            <div class="booking-date">
                <i class="fas fa-calendar"></i>
                Created ${booking.date}
            </div>
        </div>
    `;
    
    // 클릭 이벤트 추가
    item.addEventListener('click', function() {
        showBookingDetail(booking.id, booking.name);
    });
    
    return item;
}

// 검색 기능 (추후 확장용)
function searchBookings(keyword) {
    console.log(`검색어: ${keyword}`);
    
    // 실제로는 서버에 검색 요청
    // fetch(`/api/booking/search?q=${keyword}`)
    //     .then(response => response.json())
    //     .then(data => {
    //         updateBookingList(data);
    //     });
}

// 필터 기능 (추후 확장용)
function filterBookings(filter) {
    console.log(`필터: ${filter}`);
    
    // 실제로는 서버에 필터 요청
    // fetch(`/api/booking/filter?type=${filter}`)
    //     .then(response => response.json())
    //     .then(data => {
    //         updateBookingList(data);
    //     });
}
