// favorite.js
document.addEventListener('DOMContentLoaded', function() {
    // 모달 관련 이벤트 리스너
    const modal = document.getElementById('detailModal');
    const closeBtn = document.querySelector('.close');
    
    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            modal.style.display = 'none';
        });
    }
    
    // 모달 외부 클릭 시 닫기
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
    
    // 즐겨찾기 삭제 이벤트 리스너
    document.addEventListener('click', function(event) {
        if (event.target.closest('.btn-remove')) {
            const favoriteItem = event.target.closest('.favorite-item');
            const favoriteId = favoriteItem.dataset.id;
            const hospitalName = favoriteItem.dataset.hospital;
            
            if (confirm(`"${hospitalName}"을(를) 즐겨찾기에서 삭제하시겠습니까?`)) {
                removeFavorite(favoriteId);
            }
        }
    });
});

// 즐겨찾기 삭제 함수
async function removeFavorite(favoriteId) {
    try {
        const response = await fetch(`/api/favorites/${favoriteId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            // 성공적으로 삭제된 경우 UI에서 제거
            const favoriteItem = document.querySelector(`[data-id="${favoriteId}"]`);
            if (favoriteItem) {
                favoriteItem.remove();
                
                // 즐겨찾기가 모두 삭제된 경우 빈 상태 표시
                const favoriteList = document.querySelector('.favorite-list');
                const remainingItems = favoriteList.querySelectorAll('.favorite-item');
                if (remainingItems.length === 0) {
                    renderEmptyFavorites();
                }
            }
            
            // 성공 메시지 표시 (선택사항)
            showNotification('즐겨찾기에서 삭제되었습니다.', 'success');
        } else {
            throw new Error('삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('즐겨찾기 삭제 중 오류:', error);
        showNotification('삭제 중 오류가 발생했습니다.', 'error');
    }
}

// 상세보기 함수
async function showDetail(hospitalId) {
    try {
        const response = await fetch(`/api/list/${hospitalId}`);
        if (response.ok) {
            const hospitalData = await response.json();
            showDetailModal(hospitalData);
        } else {
            throw new Error('병원 정보를 가져올 수 없습니다.');
        }
    } catch (error) {
        console.error('병원 상세 정보 로드 중 오류:', error);
        showNotification('병원 정보를 불러올 수 없습니다.', 'error');
    }
}

// 상세보기 모달 표시
function showDetailModal(hospitalData) {
    const modal = document.getElementById('detailModal');
    const modalBody = document.getElementById('modalBody');
    
    modalBody.innerHTML = `
        <div class="modal-header">
            <h2>${hospitalData.name}</h2>
        </div>
        <div class="modal-body">
            <div class="hospital-info">
                <div class="info-row">
                    <strong>주소:</strong> ${hospitalData.address}
                </div>
                <div class="info-row">
                    <strong>전화번호:</strong> ${hospitalData.phone || '정보 없음'}
                </div>
                <div class="info-row">
                    <strong>홈페이지:</strong> 
                    ${hospitalData.homepage ? `<a href="${hospitalData.homepage}" target="_blank">${hospitalData.homepage}</a>` : '정보 없음'}
                </div>
                <div class="info-row">
                    <strong>카테고리:</strong> ${hospitalData.category || '정보 없음'}
                </div>
                <div class="info-row">
                    <strong>지역:</strong> ${hospitalData.region || '정보 없음'} ${hospitalData.subregion ? `- ${hospitalData.subregion}` : ''}
                </div>
            </div>
            <div class="modal-actions">
                <button class="btn-primary" onclick="window.location.href='/list/${hospitalData.id}'">상세 페이지 보기</button>
                <button class="btn-secondary" onclick="closeModal()">닫기</button>
            </div>
        </div>
    `;
    
    modal.style.display = 'block';
}

// 모달 닫기 함수
function closeModal() {
    const modal = document.getElementById('detailModal');
    modal.style.display = 'none';
}

// 빈 즐겨찾기 상태 렌더링
function renderEmptyFavorites() {
    const container = document.getElementById('favorites-content');
    container.innerHTML = `
        <div class="empty-favorites">
            <div class="empty-icon">
                <i class="fas fa-heart"></i>
                <i class="fas fa-plus empty-plus"></i>
            </div>
            <div class="empty-content">
                <h3>아직 즐겨찾기한 병원이 없습니다</h3>
                <p class="empty-description">마음에 드는 병원을 찾아서 즐겨찾기에 추가해보세요!</p>
            </div>
            <div class="empty-actions">
                <a href="/main" class="btn-primary">병원 찾아보기</a>
            </div>
        </div>
    `;
}

// 알림 메시지 표시 함수
function showNotification(message, type = 'info') {
    // 간단한 알림 구현 (실제 프로젝트에서는 더 정교한 알림 시스템 사용)
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#27ae60' : type === 'error' ? '#e74c3c' : '#3498db'};
        color: white;
        padding: 12px 20px;
        border-radius: 4px;
        z-index: 10000;
        box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    `;
    
    document.body.appendChild(notification);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 3000);
}

// 즐겨찾기 목록 렌더링 (서버 데이터 우선)
function renderFavorites(favorites) {
    const container = document.getElementById('favorites-content');
    
    if (!favorites || favorites.length === 0) {
        renderEmptyFavorites();
        return;
    }
    
    container.innerHTML = favorites.map((favorite, index) => `
        <div class="favorite-item" data-id="${favorite.id}" data-hospital="${favorite.name}">
            <div class="item-banner">
                <span class="day-number">${index + 1}</span>
            </div>
            <div class="item-content">
                <div class="item-info">
                    <h3 class="hospital-name">${favorite.name}</h3>
                    <div class="hospital-details">
                        <p class="hospital-address">
                            <i class="fas fa-map-marker-alt"></i>
                            ${favorite.address || '주소 정보 없음'}
                        </p>
                        <p class="hospital-phone">
                            <i class="fas fa-phone"></i>
                            ${favorite.phone || '전화번호 정보 없음'}
                        </p>
                        <p class="hospital-hours">
                            <i class="fas fa-clock"></i>
                            운영시간 정보 없음
                        </p>
                    </div>
                    <div class="hospital-tags">
                        <span class="tag">${favorite.region || ''}</span>
                        <span class="tag">${favorite.subregion || ''}</span>
                        ${favorite.category ? `<span class="tag">${favorite.category}</span>` : ''}
                    </div>
                </div>
            </div>
            <div class="item-actions">
                <button class="btn-detail" onclick="showDetail(${favorite.id})">상세보기</button>
                <button class="btn-remove" title="삭제">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
        </div>
    `).join('');
}

// 즐겨찾기 데이터 로드 (서버 데이터 우선)
async function loadFavorites() {
    try {
        // 서버 데이터가 있으면 우선 사용
        if (window.serverData && window.serverData.favorites && window.serverData.favorites.length > 0) {
            renderFavorites(window.serverData.favorites);
            return;
        }
        
        // 서버 데이터가 없으면 API 호출
        const response = await fetch('/api/favorites');
        if (response.ok) {
            const favorites = await response.json();
            renderFavorites(favorites);
        } else {
            renderEmptyFavorites();
        }
    } catch (error) {
        console.error('즐겨찾기 데이터 로드 중 오류:', error);
        renderEmptyFavorites();
    }
}
