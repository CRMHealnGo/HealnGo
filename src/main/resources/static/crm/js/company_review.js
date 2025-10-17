// 업체 리뷰 관리 페이지 JavaScript

// 전역 변수
let reviewData = [];
let allItems = [];
let currentFilter = 'all';
let selectedReview = null;
let currentCompanyId = null;

document.addEventListener('DOMContentLoaded', async function() {
    // 세션에서 현재 로그인한 업체 ID 가져오기
    currentCompanyId = await getCurrentCompanyId();
    
    if (!currentCompanyId) {
        alert('로그인이 필요합니다.');
        return;
    }
    
    // 리뷰 데이터 로드
    await loadReviewData();
    
    // 통계 업데이트
    updateStats();
    
    // 필터 버튼 이벤트
    setupFilterButtons();
    
    // 검색 기능
    setupSearch();
    
    // 답변 관련 버튼 이벤트
    setupReplyButtons();
});

// 현재 업체 ID 가져오기
async function getCurrentCompanyId() {
    try {
        const response = await fetch('/crm/api/current-company');
        if (response.ok) {
            const data = await response.json();
            console.log('현재 업체 정보:', data);
            return data.companyId;
        }
        console.log('API 응답 실패, 테스트용 ID 사용');
        return 1;
    } catch (error) {
        console.error('Failed to get company ID:', error);
        // 테스트용
        return 1;
    }
}

// 리뷰 데이터 로드
async function loadReviewData() {
    try {
        console.log('리뷰 데이터 로드 시작...');
        
        // API 호출 시도
        try {
            // 1. 업체의 모든 리뷰 직접 조회
            console.log('업체 리뷰 직접 조회 중...');
            const response = await fetch(`/api/review/company-reviews/${currentCompanyId}`);
            console.log(`API 호출: /api/review/company-reviews/${currentCompanyId}`);
            if (response.ok) {
                const reviews = await response.json();
                console.log('업체 리뷰 데이터:', reviews);
                console.log('리뷰 개수:', reviews.length);
                
                // Object[]를 리뷰 객체로 변환
                reviewData = reviews.map(review => ({
                    reviewId: review[0],
                    userId: review[1],
                    itemId: review[2],
                    bookingId: review[3],
                    rating: review[4],
                    title: review[5],
                    content: review[6],
                    imageMime: review[7],
                    isPublic: review[8],
                    createdAt: review[9],
                    updatedAt: review[10],
                    itemName: review[11],
                    userName: review[12],
                    imageUrl: `/review/${review[0]}/image`
                }));
                
                console.log('변환된 리뷰 데이터:', reviewData);
                
                // 2. 리뷰 목록 렌더링
                renderReviewList(reviewData);
                
                // 3. 첫 번째 리뷰 선택
                if (reviewData.length > 0) {
                    selectReview(reviewData[0]);
                } else {
                    showNoReviewsMessage();
                }
            } else {
                throw new Error('리뷰 조회 실패');
            }
            
        } catch (apiError) {
            console.error('API 호출 실패:', apiError);
            console.log('테스트 데이터로 폴백...');
            loadTestData();
        }
        
    } catch (error) {
        console.error('Failed to load review data:', error);
        showErrorMessage('리뷰 데이터를 불러오는데 실패했습니다.');
        
        // 에러 발생 시 테스트 데이터 사용
        console.log('테스트 데이터로 폴백...');
        loadTestData();
    }
}

// 리뷰 목록 렌더링
function renderReviewList(reviews) {
    console.log('renderReviewList 호출됨, 리뷰 개수:', reviews.length);
    
    const reviewListContainer = document.querySelector('.review-list');
    if (!reviewListContainer) {
        console.error('review-list 컨테이너를 찾을 수 없습니다!');
        return;
    }
    
    console.log('review-list 컨테이너 찾음:', reviewListContainer);
    
    // 기존 하드코딩된 아이템 제거
    reviewListContainer.innerHTML = '';
    
    if (reviews.length === 0) {
        console.log('리뷰가 없어서 빈 메시지 표시');
        reviewListContainer.innerHTML = '<div class="empty-message">리뷰가 없습니다.</div>';
        return;
    }
    
    reviews.forEach(review => {
        console.log('리뷰 데이터 확인:', review);
        const hasReply = review.replies && review.replies.length > 0;
        console.log(`리뷰 ${review.reviewId}의 답글 상태:`, { hasReply, replies: review.replies });
        const status = hasReply ? 'completed' : 'pending';
        const statusText = hasReply ? '답변 완료' : '답변 대기';
        
        const reviewItem = document.createElement('div');
        reviewItem.className = 'review-item';
        reviewItem.dataset.reviewId = review.reviewId;
        reviewItem.dataset.status = status;
        
        reviewItem.innerHTML = `
            <div class="review-header">
                <span class="review-id">#${String(review.reviewId).padStart(4, '0')}</span>
                <span class="status-badge ${status}">${statusText}</span>
            </div>
            <h4>${review.title || '제목 없음'}</h4>
            <p class="customer">고객 : ${maskName(review.userName || '익명')}</p>
            <p class="review-text">${truncateText(review.content, 50)}</p>
            <div class="review-footer">
                <div class="rating">
                    <i class="fas fa-star"></i>
                    <span>${review.rating || 0}</span>
                </div>
                <span class="date">${formatDate(review.createdAt)}</span>
                <span class="item-name">${review.itemName || ''}</span>
            </div>
        `;
        
        reviewItem.addEventListener('click', function() {
            selectReview(review);
        });
        
        reviewListContainer.appendChild(reviewItem);
    });
}

// 리뷰 선택
async function selectReview(review) {
    // 이전 선택 해제
    document.querySelectorAll('.review-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // 현재 아이템 선택
    const selectedItem = document.querySelector(`[data-review-id="${review.reviewId}"]`);
    if (selectedItem) {
        selectedItem.classList.add('selected');
    }
    
    selectedReview = review;
    
    // 답글 데이터 로드
    try {
        const response = await fetch(`/review/reply/review/${review.reviewId}`);
        if (response.ok) {
            const replies = await response.json();
            review.replies = replies;
            console.log(`리뷰 ${review.reviewId}의 답글 로드됨:`, replies);
        }
    } catch (error) {
        console.error('답글 로드 실패:', error);
        review.replies = [];
    }
    
    // 상세 정보 업데이트
    updateReviewDetail(review);
}

// 리뷰 상세 정보 업데이트
function updateReviewDetail(review) {
    if (!review) return;

    const hasReply = review.replies && review.replies.length > 0;
    const status = hasReply ? 'completed' : 'pending';
    const statusText = hasReply ? '답변 완료' : '답변 대기';

    // 헤더 정보 업데이트
    const detailHeader = document.querySelector('.detail-header');
    if (detailHeader) {
        detailHeader.innerHTML = `
            <span class="review-id">#${String(review.reviewId).padStart(4, '0')}</span>
            <span class="status-badge ${status}">${statusText}</span>
        `;
    }

    // 평점 업데이트
    const stars = document.querySelector('.stars');
    const ratingScore = document.querySelector('.rating-score');
    if (stars) stars.innerHTML = generateStars(review.rating || 0);
    if (ratingScore) ratingScore.textContent = review.rating || 0;

    // 기본 정보 업데이트
    const detailInfo = document.querySelector('.detail-info');
    if (detailInfo) {
        detailInfo.innerHTML = `
            <h3>${review.title || '제목 없음'}</h3>
            <p class="customer">고객 : ${maskName(review.userName || '익명')}</p>
            <div class="detail-meta">
                <span class="date">작성일: ${formatDate(review.createdAt)}</span>
                <span class="item">아이템: ${review.itemName || '정보 없음'}</span>
            </div>
        `;
    }

    // 고객 리뷰 업데이트
    const customerReview = document.querySelector('.customer-review');
    if (customerReview) {
        customerReview.textContent = review.content || '내용 없음';
    }

    // 리뷰 이미지 표시
    if (review.imageUrl) {
        const reviewContentBox = document.querySelector('.review-content-box');
        if (reviewContentBox) {
            const existingImage = reviewContentBox.querySelector('.review-image');
            if (existingImage) {
                existingImage.remove();
            }
            
            const imageDiv = document.createElement('div');
            imageDiv.className = 'review-image';
            imageDiv.innerHTML = `<img src="${review.imageUrl}" alt="리뷰 이미지" style="max-width: 100%; border-radius: 8px; margin-top: 10px;">`;
            reviewContentBox.appendChild(imageDiv);
        }
    }

    // 판매자 답변 섹션 업데이트
    const replySection = document.querySelector('.reply-section');
    const replyEditSection = document.querySelector('.reply-edit-section');
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    const textarea = document.querySelector('.reply-textarea');

    if (hasReply && review.replies.length > 0) {
        const reply = review.replies[0]; // 첫 번째 답글
        const sellerReply = document.querySelector('.seller-reply');
        if (sellerReply) sellerReply.textContent = reply.body;
        if (textarea) textarea.value = reply.body;
        if (replySection) replySection.style.display = 'block';
        if (editReplyBtn) {
            editReplyBtn.textContent = '답변 수정';
            editReplyBtn.style.display = 'block';
        }
    } else {
        if (replySection) replySection.style.display = 'none';
        if (textarea) textarea.value = '';
        if (editReplyBtn) {
            editReplyBtn.textContent = '답변 작성';
            editReplyBtn.style.display = 'block';
        }
    }

    // 답변 수정 섹션 숨김
    if (replyEditSection) {
        replyEditSection.style.display = 'none';
    }
}

// 필터 버튼 설정
function setupFilterButtons() {
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            // 활성 버튼 변경
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            // 필터 적용
            currentFilter = this.dataset.filter;
            filterReviews(currentFilter);
        });
    });
}

// 검색 설정
function setupSearch() {
    const searchInput = document.getElementById('reviewSearch');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            filterReviews(currentFilter, searchTerm);
        });
    }
}

// 답변 버튼 설정
function setupReplyButtons() {
    // 답변 작성/수정 버튼
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    if (editReplyBtn) {
        editReplyBtn.addEventListener('click', function() {
            showReplyEditSection();
        });
    }

    // 답변 취소 버튼
    const cancelBtn = document.querySelector('.cancel-btn');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            hideReplyEditSection();
        });
    }

    // 답변 저장 버튼
    const saveBtn = document.querySelector('.save-btn');
    if (saveBtn) {
        saveBtn.addEventListener('click', async function() {
            await saveReply();
        });
    }
}

// 리뷰 필터링
function filterReviews(filter, searchTerm = '') {
    const reviewItems = document.querySelectorAll('.review-item');
    
    reviewItems.forEach(item => {
        const reviewId = parseInt(item.dataset.reviewId);
        const review = reviewData.find(r => r.reviewId === reviewId);
        if (!review) return;
        
        let shouldShow = true;
        const hasReply = review.replies && review.replies.length > 0;
        const status = hasReply ? 'completed' : 'pending';

        // 필터 조건 확인
        if (filter !== 'all') {
            switch (filter) {
                case 'pending':
                    shouldShow = status === 'pending';
                    break;
                case 'completed':
                    shouldShow = status === 'completed';
                    break;
                case 'high-rating':
                    shouldShow = (review.rating || 0) >= 4.5;
                    break;
                case 'low-rating':
                    shouldShow = (review.rating || 0) < 4.5;
                    break;
            }
        }

        // 검색어 조건 확인
        if (searchTerm && shouldShow) {
            const titleMatch = (review.title || '').toLowerCase().includes(searchTerm);
            const contentMatch = (review.content || '').toLowerCase().includes(searchTerm);
            const userNameMatch = (review.userName || '').toLowerCase().includes(searchTerm);
            const itemNameMatch = (review.itemName || '').toLowerCase().includes(searchTerm);
            
            shouldShow = titleMatch || contentMatch || userNameMatch || itemNameMatch;
        }

        // 표시/숨김 처리
        item.style.display = shouldShow ? 'block' : 'none';
    });

    // 필터링 후 결과가 없으면 메시지 표시
    const visibleItems = Array.from(reviewItems).filter(item => item.style.display !== 'none');
    if (visibleItems.length === 0) {
        showNoResultsMessage();
    } else {
        hideNoResultsMessage();
    }
}

// 통계 업데이트
function updateStats() {
    const totalReviews = reviewData.length;
    const pendingReplies = reviewData.filter(r => !r.replies || r.replies.length === 0).length;
    const completedReplies = reviewData.filter(r => r.replies && r.replies.length > 0).length;
    
    // 평균 평점 계산
    let averageRating = 0;
    if (totalReviews > 0) {
        const totalRating = reviewData.reduce((sum, r) => sum + (r.rating || 0), 0);
        averageRating = (totalRating / totalReviews).toFixed(1);
    }

    // 통계 카드 업데이트
    const totalElement = document.querySelector('.stats-cards .stat-card:nth-child(1) h3');
    const pendingElement = document.querySelector('.stats-cards .stat-card:nth-child(2) h3');
    const completedElement = document.querySelector('.stats-cards .stat-card:nth-child(3) h3');
    const averageElement = document.querySelector('.stats-cards .stat-card:nth-child(4) h3');

    if (totalElement) totalElement.textContent = totalReviews;
    if (pendingElement) pendingElement.textContent = pendingReplies;
    if (completedElement) completedElement.textContent = completedReplies;
    if (averageElement) averageElement.textContent = averageRating;
}

// 답변 수정 섹션 표시
function showReplyEditSection() {
    const replySection = document.querySelector('.reply-section');
    const replyEditSection = document.querySelector('.reply-edit-section');
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    
    if (replyEditSection) {
        replyEditSection.style.display = 'block';
    }
    if (editReplyBtn) {
        editReplyBtn.style.display = 'none';
    }
    
    // 텍스트 영역에 포커스
    const textarea = document.querySelector('.reply-textarea');
    if (textarea) {
        textarea.focus();
    }
}

// 답변 수정 섹션 숨김
function hideReplyEditSection() {
    const replyEditSection = document.querySelector('.reply-edit-section');
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    
    if (replyEditSection) {
        replyEditSection.style.display = 'none';
    }
    if (editReplyBtn) {
        editReplyBtn.style.display = 'block';
    }
}

// 답변 저장
async function saveReply() {
    const textarea = document.querySelector('.reply-textarea');
    const replyText = textarea.value.trim();

    if (!replyText) {
        alert('답변을 입력해주세요.');
        return;
    }

    if (!selectedReview) {
        alert('리뷰를 선택해주세요.');
        return;
    }

    try {
        const hasReply = selectedReview.replies && selectedReview.replies.length > 0;
        
        if (hasReply) {
            // 답글 수정
            const replyId = selectedReview.replies[0].replyId;
            await ReviewAPI.updateReply(replyId, replyText);
        } else {
            // 새 답글 작성
            await ReviewAPI.createReply(selectedReview.reviewId, currentCompanyId, replyText, true);
        }

        // 성공 메시지
        showSuccessMessage('답변이 저장되었습니다.');

        // 리뷰 데이터 다시 로드
        await loadReviewData();
        
        // 답변 수정 섹션 숨김
        hideReplyEditSection();
        
        // 통계 업데이트
        updateStats();

    } catch (error) {
        console.error('Failed to save reply:', error);
        alert('답변 저장에 실패했습니다: ' + error.message);
    }
}

// 별점 생성
function generateStars(rating) {
    let stars = '';
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;

    for (let i = 0; i < fullStars; i++) {
        stars += '<i class="fas fa-star"></i>';
    }

    if (hasHalfStar) {
        stars += '<i class="fas fa-star-half-alt"></i>';
    }

    const emptyStars = 5 - Math.ceil(rating);
    for (let i = 0; i < emptyStars; i++) {
        stars += '<i class="far fa-star"></i>';
    }

    return stars;
}

// 유틸리티 함수들

// 이름 마스킹
function maskName(name) {
    if (!name || name.length < 2) return name;
    return name[0] + '*'.repeat(name.length - 1);
}

// 텍스트 자르기
function truncateText(text, maxLength) {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
}

// 날짜 포맷팅
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toISOString().split('T')[0];
}

// 메시지 표시 함수들

function showNoItemsMessage() {
    const reviewListContainer = document.querySelector('.review-list');
    if (reviewListContainer) {
        reviewListContainer.innerHTML = '<div class="empty-message">등록된 아이템이 없습니다.</div>';
    }
    
    const detailSection = document.querySelector('.review-detail');
    if (detailSection) {
        detailSection.innerHTML = '<div class="empty-message">아이템을 먼저 등록해주세요.</div>';
    }
}

function showNoReviewsMessage() {
    const detailSection = document.querySelector('.review-detail');
    if (detailSection) {
        detailSection.innerHTML = '<div class="empty-message">아직 작성된 리뷰가 없습니다.</div>';
    }
}

function showErrorMessage(message) {
    const reviewListContainer = document.querySelector('.review-list');
    if (reviewListContainer) {
        reviewListContainer.innerHTML = `<div class="error-message">${message}</div>`;
    }
}

function showNoResultsMessage() {
    let noResultsMsg = document.querySelector('.no-results-message');
    if (!noResultsMsg) {
        noResultsMsg = document.createElement('div');
        noResultsMsg.className = 'no-results-message';
        noResultsMsg.style.cssText = `
            text-align: center;
            padding: 40px;
            color: #7f8c8d;
            font-size: 16px;
        `;
        noResultsMsg.textContent = '검색 조건에 맞는 리뷰가 없습니다.';
        document.querySelector('.review-list').appendChild(noResultsMsg);
    }
    noResultsMsg.style.display = 'block';
}

function hideNoResultsMessage() {
    const noResultsMsg = document.querySelector('.no-results-message');
    if (noResultsMsg) {
        noResultsMsg.style.display = 'none';
    }
}

function showSuccessMessage(message) {
    // 토스트 메시지
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #27ae60;
        color: white;
        padding: 12px 20px;
        border-radius: 6px;
        font-size: 14px;
        z-index: 1000;
        animation: slideIn 0.3s ease;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    `;
    toast.textContent = message;
    document.body.appendChild(toast);

    // 3초 후 자동 제거
    setTimeout(() => {
        toast.style.animation = 'slideOut 0.3s ease';
        setTimeout(() => {
            if (document.body.contains(toast)) {
                document.body.removeChild(toast);
            }
        }, 300);
    }, 3000);
}

// CSS 애니메이션 추가
if (!document.head.querySelector('style[data-review-animations]')) {
    const style = document.createElement('style');
    style.setAttribute('data-review-animations', 'true');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
        .empty-message, .error-message {
            text-align: center;
            padding: 40px;
            color: #7f8c8d;
            font-size: 16px;
        }
        .error-message {
            color: #e74c3c;
        }
    `;
    document.head.appendChild(style);
}

// ========== 답글 관련 함수들 ==========

// 답글 작성/수정 폼 표시
function showReplyForm(reviewId, existingReply = null) {
    const replyEditSection = document.querySelector('.reply-edit-section');
    const replySection = document.querySelector('.reply-section');
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    
    if (replyEditSection) {
        replyEditSection.style.display = 'block';
        replySection.style.display = 'none';
        editReplyBtn.style.display = 'none';
        
        const textarea = replyEditSection.querySelector('.reply-textarea');
        if (existingReply) {
            textarea.value = existingReply.body;
        } else {
            textarea.value = '';
        }
        
        // 저장 버튼 이벤트 리스너
        const saveBtn = replyEditSection.querySelector('.save-btn');
        saveBtn.onclick = () => saveReply(reviewId, existingReply?.replyId);
        
        // 취소 버튼 이벤트 리스너
        const cancelBtn = replyEditSection.querySelector('.cancel-btn');
        cancelBtn.onclick = () => hideReplyForm();
    }
}

// 답글 작성/수정 폼 숨김
function hideReplyForm() {
    const replyEditSection = document.querySelector('.reply-edit-section');
    const replySection = document.querySelector('.reply-section');
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    
    if (replyEditSection) {
        replyEditSection.style.display = 'none';
        replySection.style.display = 'block';
        editReplyBtn.style.display = 'block';
    }
}

// 답글 저장
async function saveReply(reviewId, replyId = null) {
    const textarea = document.querySelector('.reply-textarea');
    const body = textarea.value.trim();
    
    if (!body) {
        alert('답글 내용을 입력해주세요.');
        return;
    }
    
    try {
        if (replyId) {
            // 답글 수정
            await ReviewAPI.updateReply(replyId, body, true);
            alert('답글이 수정되었습니다!');
        } else {
            // 답글 작성
            await ReviewAPI.createReply(reviewId, currentCompanyId, body, true);
            alert('답글이 작성되었습니다!');
        }
        
        // 답글 목록 다시 로드
        await loadRepliesForReview(reviewId);
        
        // 폼 숨김
        hideReplyForm();
        
    } catch (error) {
        console.error('답글 저장 실패:', error);
        alert('답글 저장에 실패했습니다.');
    }
}

// 특정 리뷰의 답글 로드
async function loadRepliesForReview(reviewId) {
    try {
        console.log('ReviewAPI 확인:', typeof ReviewAPI, ReviewAPI);
        if (!ReviewAPI || !ReviewAPI.getRepliesByReview) {
            console.error('ReviewAPI.getRepliesByReview이 정의되지 않았습니다. 직접 API 호출합니다.');
            // 직접 API 호출
            const response = await fetch(`/review/reply/review/${reviewId}`);
            if (!response.ok) throw new Error('Failed to fetch replies');
            const replies = await response.json();
            renderRepliesInDetail(replies);
            return;
        }
        const replies = await ReviewAPI.getRepliesByReview(reviewId);
        renderRepliesInDetail(replies);
    } catch (error) {
        console.error('답글 로드 실패:', error);
    }
}

// 답글 목록을 상세 화면에 렌더링
function renderRepliesInDetail(replies) {
    const replySection = document.querySelector('.reply-section');
    const replyWriteBtn = document.getElementById('replyWriteBtn');
    const replyEditBtn = document.getElementById('replyEditBtn');
    
    if (!replySection) return;
    
    if (replies.length === 0) {
        replySection.innerHTML = `
            <h4>판매자 답변</h4>
            <div class="seller-reply">
                <p class="no-reply">아직 답글이 없습니다.</p>
            </div>
        `;
        
        // 답글 작성 버튼만 표시
        if (replyWriteBtn) replyWriteBtn.style.display = 'block';
        if (replyEditBtn) replyEditBtn.style.display = 'none';
        return;
    }
    
    const latestReply = replies[replies.length - 1]; // 가장 최근 답글
    replySection.innerHTML = `
        <h4>판매자 답변</h4>
        <div class="seller-reply">
            ${latestReply.body}
        </div>
    `;
    
    // 답글 수정 버튼만 표시
    if (replyWriteBtn) replyWriteBtn.style.display = 'none';
    if (replyEditBtn) replyEditBtn.style.display = 'block';
}

// 기존 답글 로드 후 수정 폼 표시
async function loadExistingReplyForEdit(reviewId) {
    try {
        const replies = await ReviewAPI.getRepliesByReview(reviewId);
        if (replies.length > 0) {
            const latestReply = replies[replies.length - 1];
            showReplyForm(reviewId, latestReply);
        }
    } catch (error) {
        console.error('기존 답글 로드 실패:', error);
        alert('기존 답글을 불러오는데 실패했습니다.');
    }
}

// 답글 삭제
async function deleteReply(replyId, reviewId) {
    if (!confirm('정말로 이 답글을 삭제하시겠습니까?')) {
        return;
    }
    
    try {
        await ReviewAPI.deleteReply(replyId);
        alert('답글이 삭제되었습니다!');
        
        // 답글 목록 다시 로드
        await loadRepliesForReview(reviewId);
        
    } catch (error) {
        console.error('답글 삭제 실패:', error);
        alert('답글 삭제에 실패했습니다.');
    }
}

// 답글 공개/비공개 설정
async function toggleReplyVisibility(replyId, reviewId) {
    try {
        await ReviewAPI.toggleReplyVisibility(replyId);
        alert('답글 공개 설정이 변경되었습니다!');
        
        // 답글 목록 다시 로드
        await loadRepliesForReview(reviewId);
        
    } catch (error) {
        console.error('답글 공개 설정 변경 실패:', error);
        alert('답글 공개 설정 변경에 실패했습니다.');
    }
}

// 리뷰 상세 정보 로드 시 답글도 함께 로드
async function loadReviewDetail(reviewId) {
    try {
        // 리뷰 상세 정보 로드
        const review = await ReviewAPI.getReviewById(reviewId);
        if (review) {
            // 답글 로드
            await loadRepliesForReview(reviewId);
        }
    } catch (error) {
        console.error('리뷰 상세 로드 실패:', error);
    }
}

// 기존 리뷰 클릭 이벤트에 답글 로드 추가
function setupReviewItemClick() {
    const reviewItems = document.querySelectorAll('.review-item');
    reviewItems.forEach(item => {
        item.addEventListener('click', async function() {
            const reviewId = this.dataset.reviewId;
            if (reviewId) {
                selectedReview = reviewId;
                
                // 리뷰 상세 정보 로드 (답글 포함)
                await loadReviewDetail(reviewId);
                
                // 선택된 리뷰 하이라이트
                reviewItems.forEach(ri => ri.classList.remove('selected'));
                this.classList.add('selected');
            }
        });
    });
}

// 답글 작성 버튼 이벤트 리스너
function setupReplyButtons() {
    // 답글 작성 버튼
    const replyWriteBtn = document.getElementById('replyWriteBtn');
    if (replyWriteBtn) {
        replyWriteBtn.addEventListener('click', function() {
            if (selectedReview) {
                showReplyForm(selectedReview.reviewId);
            }
        });
    }
    
    // 답글 수정 버튼
    const replyEditBtn = document.getElementById('replyEditBtn');
    if (replyEditBtn) {
        replyEditBtn.addEventListener('click', function() {
            if (selectedReview) {
                // 기존 답글 로드 후 수정 폼 표시
                loadExistingReplyForEdit(selectedReview.reviewId);
            }
        });
    }
}

// 테스트 데이터 로드
function loadTestData() {
    console.log('테스트 데이터 로드 중...');
    
    // 실제 DB 데이터와 일치하는 테스트 데이터
    reviewData = [
        {
            reviewId: 3,
            userId: 2,
            itemId: 1,
            bookingId: 4,
            rating: 4,
            title: '탱글탱글gg',
            content: '탱글탱글한 피부가 되었어요 ㅎㅎ',
            imageUrl: '/review/3/image',
            imageMime: 'image/png',
            isPublic: true,
            createdAt: '2025-10-17T18:44:11',
            updatedAt: '2025-10-17T19:23:41',
            itemName: '힙필러 시술',
            userName: '테스트 사용자',
            itemInfo: { id: 1, name: '힙필러 시술' }
        },
        {
            reviewId: 4,
            userId: 2,
            itemId: 1,
            bookingId: 6,
            rating: 5,
            title: '좋아요',
            content: '좋더라구요',
            imageUrl: '/review/4/image',
            imageMime: 'image/jpeg',
            isPublic: true,
            createdAt: '2025-10-17T19:25:31',
            updatedAt: '2025-10-17T19:25:31',
            itemName: '힙필러 시술',
            userName: '테스트 사용자',
            itemInfo: { id: 1, name: '힙필러 시술' }
        }
    ];
    
    console.log('테스트 리뷰 데이터:', reviewData);
    
    // 리뷰 목록 렌더링
    renderReviewList(reviewData);
    
    // 첫 번째 리뷰 선택
    if (reviewData.length > 0) {
        selectReview(reviewData[0]);
    }
}

// 페이지 로드 시 답글 관련 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', function() {
    setupReplyButtons();
    setupReviewItemClick();
});
