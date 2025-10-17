// 사용자 후기 페이지 JavaScript

// 전역 변수
let currentUserId = null;
let reservations = [];
let selectedReservation = null;
let selectedReview = null;
let uploadedImageFile = null;

document.addEventListener('DOMContentLoaded', async function() {
    // 현재 로그인한 사용자 ID 가져오기
    currentUserId = await getCurrentUserId();
    
    if (!currentUserId) {
        alert('로그인이 필요합니다.');
        window.location.href = '/login';
        return;
    }
    
    // 사용자의 예약 목록 로드
    await loadUserReservations();
    
    // 별점 선택 이벤트 설정
    setupStarRating();
});

// 현재 사용자 ID 가져오기 (세션에서)
async function getCurrentUserId() {
    try {
        // TODO: 실제로는 세션에서 가져와야 함
        // 임시로 하드코딩 (실제 구현 시 수정 필요)
        const response = await fetch('/api/current-user');
        if (response.ok) {
            const data = await response.json();
            return data.userId;
        }
        return null;
    } catch (error) {
        console.error('Failed to get user ID:', error);
        // 테스트용
        return 1;
    }
}

// 사용자의 예약 목록 로드
async function loadUserReservations() {
    try {
        // 예약 목록 가져오기
        reservations = await ReservationAPI.getUserReservations(currentUserId);
        
        // 각 예약에 대한 리뷰 정보 가져오기
        for (let reservation of reservations) {
            const review = await ReviewAPI.getReviewByBooking(reservation.id);
            reservation.review = review;
        }
        
        // 예약 목록 렌더링
        renderReservationList(reservations);
        
        // 첫 번째 예약 선택
        if (reservations.length > 0) {
            selectReservation(reservations[0]);
        } else {
            showEmptyState();
        }
    } catch (error) {
        console.error('Failed to load reservations:', error);
        showErrorState();
    }
}

// 예약 목록 렌더링
function renderReservationList(reservations) {
    const container = document.querySelector('.review-list');
    if (!container) return;
    
    container.innerHTML = '';
    
    if (reservations.length === 0) {
        container.innerHTML = '<div class="empty-message">예약 내역이 없습니다.</div>';
        return;
    }
    
    reservations.forEach(reservation => {
        const hasReview = reservation.review !== null;
        const reviewItem = document.createElement('div');
        reviewItem.className = 'review-item';
        reviewItem.dataset.reservationId = reservation.id;
        
        reviewItem.innerHTML = `
            <div class="review-icon">#${String(reservation.id).padStart(4, '0')}</div>
            <div class="review-content">
                <div class="review-number">${hasReview ? '리뷰 작성 완료' : '리뷰 작성 대기'}</div>
                <div class="review-title">${reservation.title || '예약'}</div>
                <div class="review-date">${formatDate(reservation.date)} ${reservation.start_time || ''}</div>
                <div class="review-clinic">${reservation.location || ''}</div>
                ${hasReview ? `
                    <div class="review-rating">
                        ${'★'.repeat(reservation.review.rating || 0)}${'☆'.repeat(5 - (reservation.review.rating || 0))}
                        ${reservation.review.replies && reservation.review.replies.length > 0 ? `<span class="reply-badge">답글 ${reservation.review.replies.length}개</span>` : ''}
            </div>
                ` : `
                    <div class="review-status-pending">리뷰를 작성해주세요</div>
                `}
        </div>
    `;
        
        reviewItem.addEventListener('click', () => selectReservation(reservation));
        container.appendChild(reviewItem);
    });
}

// 예약 선택
function selectReservation(reservation) {
    // 모든 아이템에서 active 클래스 제거
    document.querySelectorAll('.review-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // 선택된 아이템에 active 클래스 추가
    const selectedItem = document.querySelector(`[data-reservation-id="${reservation.id}"]`);
    if (selectedItem) {
        selectedItem.classList.add('active');
    }
    
    selectedReservation = reservation;
    selectedReview = reservation.review;
    
    // 상세 정보 렌더링
    renderReservationDetail(reservation);
}

// 예약 상세 정보 렌더링
function renderReservationDetail(reservation) {
    const detailSection = document.querySelector('.review-detail-section');
    if (!detailSection) return;
    
    const imageContainer = detailSection.querySelector('.review-detail-image');
    const contentContainer = detailSection.querySelector('.review-detail-content');
    const ratingSection = detailSection.querySelector('.rating-section');
    
    const hasReview = reservation.review !== null;
    
    // 이미지 컨테이너
    if (imageContainer) {
        if (hasReview && reservation.review.imageUrl) {
        imageContainer.innerHTML = `
                <img src="${reservation.review.imageUrl}" alt="리뷰 이미지">
            `;
        } else {
            imageContainer.innerHTML = `
                <div class="no-image">
                    <i class="fas fa-image"></i>
                    <p>이미지 없음</p>
            </div>
        `;
    }
}

    // 컨텐츠 컨테이너
    if (contentContainer) {
        if (hasReview) {
            // 이미 작성된 리뷰 표시
            renderExistingReview(contentContainer, reservation);
        } else {
            // 리뷰 작성 폼 표시
            renderReviewForm(contentContainer, reservation);
        }
    }
    
    // 별점 섹션 표시/숨김
    if (ratingSection) {
        ratingSection.style.display = hasReview ? 'none' : 'block';
    }
}

// 이미 작성된 리뷰 표시
function renderExistingReview(container, reservation) {
    const review = reservation.review;
    
    container.innerHTML = `
        <div class="existing-review">
            <div class="review-header-info">
                <h2>${review.title || '리뷰'}</h2>
                <div class="review-rating-display">
                    ${'★'.repeat(review.rating || 0)}${'☆'.repeat(5 - (review.rating || 0))}
                    <span>(${review.rating || 0}점)</span>
                </div>
            </div>
            
            <div class="reservation-info">
                <h3>예약 정보</h3>
                <p><strong>제목:</strong> ${reservation.title}</p>
                <p><strong>날짜:</strong> ${formatDate(reservation.date)} ${reservation.start_time || ''}</p>
                <p><strong>장소:</strong> ${reservation.location || ''}</p>
                <p><strong>금액:</strong> ${formatCurrency(reservation.total_amount)}</p>
            </div>
            
            <div class="review-content-box">
                <h3>내 리뷰</h3>
                <p>${review.content || '내용 없음'}</p>
            </div>
            
            ${review.replies && review.replies.length > 0 ? `
                <div class="review-replies">
                    <h3>업체 답글</h3>
                    ${review.replies.map(reply => `
                        <div class="reply-item">
                            <div class="reply-header">
                                <strong>${reply.companyName || '업체'}</strong>
                                <span class="reply-date">${formatDate(reply.createdAt)}</span>
                            </div>
                            <p>${reply.body}</p>
                        </div>
                    `).join('')}
                </div>
            ` : ''}
            
            <div class="review-actions">
                <button class="edit-btn" onclick="editReview(${review.reviewId})">
                    <i class="fas fa-edit"></i> 수정
                </button>
                <button class="delete-btn" onclick="deleteReview(${review.reviewId})">
                    <i class="fas fa-trash"></i> 삭제
                </button>
            </div>
        </div>
    `;
}

// 리뷰 작성 폼 표시
function renderReviewForm(container, reservation) {
    container.innerHTML = `
        <div class="review-form">
            <h2>리뷰 작성하기</h2>
            
            <div class="reservation-info">
                <h3>예약 정보</h3>
                <p><strong>제목:</strong> ${reservation.title}</p>
                <p><strong>날짜:</strong> ${formatDate(reservation.date)} ${reservation.start_time || ''}</p>
                <p><strong>장소:</strong> ${reservation.location || ''}</p>
                <p><strong>금액:</strong> ${formatCurrency(reservation.total_amount)}</p>
            </div>
            
            <div class="form-group">
                <label>리뷰 제목 *</label>
                <input type="text" id="reviewTitle" class="form-input" placeholder="리뷰 제목을 입력하세요" required>
            </div>
            
            <div class="form-group">
                <label>리뷰 내용 *</label>
                <textarea id="reviewContent" class="form-textarea" rows="6" placeholder="상세한 리뷰를 작성해주세요" required></textarea>
            </div>
            
            <div class="form-group">
                <label>이미지 첨부 (선택)</label>
                <div class="image-upload-area" onclick="document.getElementById('reviewImage').click()">
                    <input type="file" id="reviewImage" accept="image/*" style="display: none;" onchange="handleImageUpload(this)">
                    <div class="upload-placeholder" id="uploadPlaceholder">
                        <i class="fas fa-cloud-upload-alt"></i>
                        <p>클릭하여 이미지 업로드</p>
                    </div>
                    <img id="imagePreview" style="display: none; width: 100%; max-height: 300px; object-fit: contain;">
                </div>
            </div>
            
            <div class="form-actions">
                <button class="cancel-btn" onclick="cancelReview()">취소</button>
                <button class="submit-btn" onclick="submitReview()">리뷰 등록</button>
            </div>
        </div>
    `;
}

// 별점 선택 기능 설정
function setupStarRating() {
    const starContainer = document.querySelector('.star-container');
    if (!starContainer) return;
    
    const stars = starContainer.querySelectorAll('.star-rating');
    
    stars.forEach(star => {
        // 클릭 이벤트
        star.addEventListener('click', function() {
            const rating = parseInt(this.dataset.rating);
            updateStarRating(rating);
        });
        
        // 호버 이벤트
        star.addEventListener('mouseenter', function() {
            const rating = parseInt(this.dataset.rating);
            highlightStars(rating);
        });
    });
    
    // 호버 아웃 이벤트
    starContainer.addEventListener('mouseleave', function() {
        const currentRating = parseInt(starContainer.querySelector('input[name="rating"]').value) || 0;
        highlightStars(currentRating);
    });
}

// 별점 업데이트
function updateStarRating(rating) {
    const starContainer = document.querySelector('.star-container');
    const ratingInput = starContainer.querySelector('input[name="rating"]');
    const ratingText = starContainer.querySelector('.rating-text');
    
    ratingInput.value = rating;
    ratingText.textContent = `${rating}점 선택됨`;
    highlightStars(rating);
}

// 별 하이라이트
function highlightStars(rating) {
    const stars = document.querySelectorAll('.star-rating');
    stars.forEach((star, index) => {
        if (index < rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}

// 이미지 업로드 처리
function handleImageUpload(input) {
    const file = input.files[0];
    if (!file) return;
    
    // 파일 크기 체크 (5MB)
    if (file.size > 5 * 1024 * 1024) {
        alert('이미지 크기는 5MB 이하로 업로드해주세요.');
        input.value = '';
        return;
    }
    
    uploadedImageFile = file;
    
    // 미리보기
    const reader = new FileReader();
    reader.onload = function(e) {
        const preview = document.getElementById('imagePreview');
        const placeholder = document.getElementById('uploadPlaceholder');
        
        if (preview && placeholder) {
            preview.src = e.target.result;
            preview.style.display = 'block';
            placeholder.style.display = 'none';
        }
    };
    reader.readAsDataURL(file);
}

// 리뷰 제출
async function submitReview() {
    if (!selectedReservation) {
        alert('예약을 선택해주세요.');
        return;
    }
    
    const title = document.getElementById('reviewTitle')?.value.trim();
    const content = document.getElementById('reviewContent')?.value.trim();
    const rating = parseInt(document.querySelector('input[name="rating"]')?.value) || 0;
    
    // 유효성 검사
    if (!title) {
        alert('리뷰 제목을 입력해주세요.');
        return;
    }
    
    if (!content) {
        alert('리뷰 내용을 입력해주세요.');
        return;
    }
    
    if (rating === 0) {
        alert('별점을 선택해주세요.');
        return;
    }
    
    try {
        // FormData 생성
        const formData = new FormData();
        formData.append('userId', currentUserId);
        formData.append('itemId', selectedReservation.company_id || 1); // TODO: item_id 정확히 매핑 필요
        formData.append('bookingId', selectedReservation.id);
        formData.append('rating', rating);
        formData.append('title', title);
        formData.append('content', content);
        formData.append('isPublic', 'true');
        
        if (uploadedImageFile) {
            formData.append('image', uploadedImageFile);
        }
        
        // API 호출
        await ReviewAPI.createReview(formData);
        
        // 성공 메시지
        alert('리뷰가 등록되었습니다!');
        
        // 예약 목록 다시 로드
        await loadUserReservations();
        
        // 파일 초기화
        uploadedImageFile = null;
        
    } catch (error) {
        console.error('Failed to submit review:', error);
        alert('리뷰 등록에 실패했습니다: ' + error.message);
    }
}

// 리뷰 수정
async function editReview(reviewId) {
    // TODO: 리뷰 수정 폼으로 전환
    alert('리뷰 수정 기능은 준비 중입니다.');
}

// 리뷰 삭제
async function deleteReview(reviewId) {
    if (!confirm('리뷰를 삭제하시겠습니까?')) {
        return;
    }
    
    try {
        await ReviewAPI.deleteReview(reviewId);
        alert('리뷰가 삭제되었습니다.');
        
        // 예약 목록 다시 로드
        await loadUserReservations();
    } catch (error) {
        console.error('Failed to delete review:', error);
        alert('리뷰 삭제에 실패했습니다: ' + error.message);
    }
}

// 리뷰 작성 취소
function cancelReview() {
    if (confirm('작성 중인 내용이 사라집니다. 취소하시겠습니까?')) {
        if (selectedReservation) {
            selectReservation(selectedReservation);
        }
        uploadedImageFile = null;
    }
}

// 별점 평가 제출 (별도 평점 섹션용)
function submitRating() {
    const starContainer = document.querySelector('.star-container');
    const rating = parseInt(starContainer.querySelector('input[name="rating"]').value) || 0;
    
    if (rating === 0) {
        alert('별점을 선택해주세요.');
        return;
    }
    
    // 리뷰 폼의 별점과 동기화
    updateStarRating(rating);
    
    // 리뷰 작성 섹션으로 스크롤
    const reviewForm = document.querySelector('.review-form');
    if (reviewForm) {
        reviewForm.scrollIntoView({ behavior: 'smooth' });
    }
}

// 빈 상태 표시
function showEmptyState() {
    const detailSection = document.querySelector('.review-detail-section');
    if (detailSection) {
        detailSection.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-calendar-times"></i>
                <h2>예약 내역이 없습니다</h2>
                <p>예약을 완료하신 후 리뷰를 작성해보세요!</p>
            </div>
        `;
    }
}

// 에러 상태 표시
function showErrorState() {
    const detailSection = document.querySelector('.review-detail-section');
    if (detailSection) {
        detailSection.innerHTML = `
            <div class="error-state">
                <i class="fas fa-exclamation-triangle"></i>
                <h2>데이터를 불러올 수 없습니다</h2>
                <p>잠시 후 다시 시도해주세요.</p>
                <button onclick="location.reload()">새로고침</button>
            </div>
        `;
    }
}

// 날짜 포맷팅
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// 통화 포맷팅
function formatCurrency(amount) {
    if (!amount) return '0원';
    return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW'
    }).format(amount);
}
