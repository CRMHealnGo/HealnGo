// 리뷰 관리 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 리뷰 데이터 (실제로는 서버에서 가져와야 함)
    const reviewData = [
        {
            id: '0015',
            status: 'completed',
            procedure: '자연스러운 힙필러 시술',
            customer: '정**',
            rating: 4.8,
            date: '2025-10-03',
            price: '150,000 원',
            reviewText: '시술 결과가 정말 만족스럽습니다. 자연스럽고 제가 원하던 모습이에요. 의료진 분들도 친절하시고 사후관리도 좋았습니다.',
            replyText: '소중한 후기 감사합니다. 만족하신 결과를 얻으셔서 저희도 기쁩니다. 앞으로도 최선을 다하겠습니다.'
        },
        {
            id: '0014',
            status: 'pending',
            procedure: '완벽한 눈 성형 후기',
            customer: '정**',
            rating: 5.0,
            date: '2025-10-03',
            price: '700,000 원',
            reviewText: '너무 만족해요! 회복도 빨랐고 결과도 자연스러워서 주변에서 많이 예뻐졌다고 합니다.',
            replyText: ''
        },
        {
            id: '0013',
            status: 'completed',
            procedure: '보톡스 시술',
            customer: '정**',
            rating: 4.5,
            date: '2025-10-03',
            price: '150,000 원',
            reviewText: '효과가 좋네요. 통증도 거의 없었고 바로 일상생활 가능했습니다.',
            replyText: '감사합니다. 좋은 결과를 얻으셨다니 저희도 기쁩니다.'
        }
    ];

    let currentFilter = 'all';
    let selectedReview = null;

    // 리뷰 아이템 클릭 이벤트
    document.querySelectorAll('.review-item').forEach(item => {
        item.addEventListener('click', function() {
            // 이전 선택 해제
            document.querySelectorAll('.review-item').forEach(i => i.classList.remove('selected'));
            
            // 현재 아이템 선택
            this.classList.add('selected');
            
            // 리뷰 ID 가져오기
            const reviewId = this.dataset.reviewId;
            selectedReview = reviewData.find(r => r.id === reviewId);
            
            // 상세 정보 업데이트
            updateReviewDetail(selectedReview);
        });
    });

    // 필터 버튼 이벤트
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

    // 검색 기능
    const searchInput = document.getElementById('reviewSearch');
    if (searchInput) {
        searchInput.addEventListener('input', function() {
            const searchTerm = this.value.toLowerCase();
            filterReviews(currentFilter, searchTerm);
        });
    }

    // 답변 수정 버튼
    const editReplyBtn = document.querySelector('.edit-reply-btn');
    if (editReplyBtn) {
        editReplyBtn.addEventListener('click', function() {
            showReplyEditSection();
        });
    }

    // 답변 수정 취소 버튼
    const cancelBtn = document.querySelector('.cancel-btn');
    if (cancelBtn) {
        cancelBtn.addEventListener('click', function() {
            hideReplyEditSection();
        });
    }

    // 답변 수정 저장 버튼
    const saveBtn = document.querySelector('.save-btn');
    if (saveBtn) {
        saveBtn.addEventListener('click', function() {
            saveReply();
        });
    }

    // 리뷰 필터링 함수
    function filterReviews(filter, searchTerm = '') {
        const reviewItems = document.querySelectorAll('.review-item');
        
        reviewItems.forEach(item => {
            const reviewId = item.dataset.reviewId;
            const review = reviewData.find(r => r.id === reviewId);
            let shouldShow = true;

            // 필터 조건 확인
            if (filter !== 'all') {
                switch (filter) {
                    case 'pending':
                        shouldShow = review.status === 'pending';
                        break;
                    case 'completed':
                        shouldShow = review.status === 'completed';
                        break;
                    case 'high-rating':
                        shouldShow = review.rating >= 4.5;
                        break;
                    case 'low-rating':
                        shouldShow = review.rating < 4.5;
                        break;
                }
            }

            // 검색어 조건 확인
            if (searchTerm && shouldShow) {
                const procedureMatch = review.procedure.toLowerCase().includes(searchTerm);
                const customerMatch = review.customer.toLowerCase().includes(searchTerm);
                const reviewTextMatch = review.reviewText.toLowerCase().includes(searchTerm);
                
                shouldShow = procedureMatch || customerMatch || reviewTextMatch;
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

    // 리뷰 상세 정보 업데이트
    function updateReviewDetail(review) {
        if (!review) return;

        // 헤더 정보 업데이트
        const detailHeader = document.querySelector('.detail-header');
        detailHeader.innerHTML = `
            <span class="review-id">#${review.id}</span>
            <span class="status-badge ${review.status}">${review.status === 'completed' ? '답변 완료' : '답변 대기'}</span>
        `;

        // 평점 업데이트
        const stars = document.querySelector('.stars');
        const ratingScore = document.querySelector('.rating-score');
        stars.innerHTML = generateStars(review.rating);
        ratingScore.textContent = review.rating;

        // 기본 정보 업데이트
        const detailInfo = document.querySelector('.detail-info');
        detailInfo.innerHTML = `
            <h3>${review.procedure}</h3>
            <p class="customer">고객 : ${review.customer}</p>
            <div class="detail-meta">
                <span class="date">작성일: ${review.date}</span>
                <span class="price">시술 금액: ${review.price}</span>
            </div>
        `;

        // 고객 리뷰 업데이트
        const customerReview = document.querySelector('.customer-review');
        customerReview.textContent = review.reviewText;

        // 판매자 답변 업데이트
        const replySection = document.querySelector('.reply-section');
        const replyEditSection = document.querySelector('.reply-edit-section');
        const editReplyBtn = document.querySelector('.edit-reply-btn');
        const textarea = document.querySelector('.reply-textarea');

        if (review.replyText) {
            const sellerReply = document.querySelector('.seller-reply');
            sellerReply.textContent = review.replyText;
            textarea.value = review.replyText;
            replySection.style.display = 'block';
            editReplyBtn.textContent = '답변 수정';
        } else {
            replySection.style.display = 'none';
            editReplyBtn.textContent = '답변 작성';
        }

        // 답변 수정 섹션 숨김
        hideReplyEditSection();
    }

    // 별점 생성 함수
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

    // 답변 수정 섹션 표시
    function showReplyEditSection() {
        const replyEditSection = document.querySelector('.reply-edit-section');
        const editReplyBtn = document.querySelector('.edit-reply-btn');
        
        replyEditSection.style.display = 'block';
        editReplyBtn.style.display = 'none';
        
        // 텍스트 영역에 포커스
        const textarea = document.querySelector('.reply-textarea');
        textarea.focus();
    }

    // 답변 수정 섹션 숨김
    function hideReplyEditSection() {
        const replyEditSection = document.querySelector('.reply-edit-section');
        const editReplyBtn = document.querySelector('.edit-reply-btn');
        
        replyEditSection.style.display = 'none';
        editReplyBtn.style.display = 'block';
    }

    // 답변 저장
    function saveReply() {
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

        // 실제로는 서버에 저장 요청
        console.log('답변 저장:', {
            reviewId: selectedReview.id,
            replyText: replyText
        });

        // 로컬 데이터 업데이트
        selectedReview.replyText = replyText;
        selectedReview.status = 'completed';

        // UI 업데이트
        const sellerReply = document.querySelector('.seller-reply');
        sellerReply.textContent = replyText;

        // 상태 업데이트
        const statusBadge = document.querySelector('.detail-header .status-badge');
        statusBadge.textContent = '답변 완료';
        statusBadge.className = 'status-badge completed';

        // 리뷰 목록의 상태도 업데이트
        const reviewItem = document.querySelector(`[data-review-id="${selectedReview.id}"]`);
        if (reviewItem) {
            const itemStatusBadge = reviewItem.querySelector('.status-badge');
            itemStatusBadge.textContent = '답변 완료';
            itemStatusBadge.className = 'status-badge completed';
        }

        // 답변 수정 섹션 숨김
        hideReplyEditSection();

        // 성공 메시지
        showSuccessMessage('답변이 저장되었습니다.');

        // 통계 업데이트 (실제로는 서버에서 가져와야 함)
        updateStats();
    }

    // 통계 업데이트
    function updateStats() {
        const pendingCount = reviewData.filter(r => r.status === 'pending').length;
        const completedCount = reviewData.filter(r => r.status === 'completed').length;
        const averageRating = (reviewData.reduce((sum, r) => sum + r.rating, 0) / reviewData.length).toFixed(1);

        // 통계 카드 업데이트
        const pendingElement = document.querySelector('.stats-cards .stat-card:nth-child(2) h3');
        const completedElement = document.querySelector('.stats-cards .stat-card:nth-child(3) h3');
        const averageElement = document.querySelector('.stats-cards .stat-card:nth-child(4) h3');

        if (pendingElement) pendingElement.textContent = pendingCount;
        if (completedElement) completedElement.textContent = completedCount;
        if (averageElement) averageElement.textContent = averageRating;
    }

    // 결과 없음 메시지 표시
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

    // 결과 없음 메시지 숨김
    function hideNoResultsMessage() {
        const noResultsMsg = document.querySelector('.no-results-message');
        if (noResultsMsg) {
            noResultsMsg.style.display = 'none';
        }
    }

    // 성공 메시지 표시
    function showSuccessMessage(message) {
        // 간단한 토스트 메시지 구현
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
        `;
        toast.textContent = message;
        document.body.appendChild(toast);

        // 3초 후 자동 제거
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                document.body.removeChild(toast);
            }, 300);
        }, 3000);
    }

    // CSS 애니메이션 추가
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);

    // 첫 번째 리뷰 자동 선택
    const firstReviewItem = document.querySelector('.review-item');
    if (firstReviewItem) {
        firstReviewItem.click();
    }
});
