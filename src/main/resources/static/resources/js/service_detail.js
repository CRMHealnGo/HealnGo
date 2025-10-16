// Service Detail Page JavaScript

let currentServiceData = null;
let currentItemId = null;

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', async function() {
    console.log('서비스 상세 페이지 로드 완료');
    
    // URL에서 서비스 ID 추출
    const pathParts = window.location.pathname.split('/');
    const serviceId = pathParts[pathParts.length - 1];
    
    if (serviceId && !isNaN(serviceId)) {
        await loadServiceDetail(serviceId);
    } else {
        showError('유효하지 않은 서비스 ID입니다.');
    }

    // 썸네일 클릭 이벤트
    initializeThumbnails();
});

// 서비스 상세 정보 로드
async function loadServiceDetail(serviceId) {
    try {
        const response = await fetch(`/api/items/services/${serviceId}`);
        
        if (!response.ok) {
            throw new Error('서비스 정보를 불러올 수 없습니다.');
        }
        
        const data = await response.json();
        currentServiceData = data;
        currentItemId = data.itemId;
        
        renderServiceDetail(data);
        
        // 병원 정보 로드
        if (data.itemId) {
            await loadHospitalInfo(data.itemId);
        }
        
        // 관련 서비스 로드
        if (data.itemId) {
            await loadRelatedServices(data.itemId, serviceId);
        }
        
        // 찜 상태 확인
        if (data.itemId) {
            await checkFavoriteStatus(data.itemId);
        }
        
    } catch (error) {
        console.error('서비스 상세 정보 로드 중 오류:', error);
        showError(error.message);
    }
}

// 서비스 상세 정보 렌더링
function renderServiceDetail(data) {
    console.log('서비스 데이터 렌더링:', data);
    
    // 타이틀
    const titleElement = document.getElementById('service-title');
    const breadcrumbElement = document.getElementById('service-name-breadcrumb');
    
    if (titleElement) titleElement.textContent = data.name || '서비스';
    if (breadcrumbElement) breadcrumbElement.textContent = data.name || '서비스';
    
    // 카테고리
    const categoryElement = document.getElementById('service-category');
    if (categoryElement) {
        categoryElement.textContent = data.serviceCategory || '일반';
    }
    
    // 태그
    const tagsContainer = document.getElementById('service-tags');
    tagsContainer.innerHTML = '';
    if (data.tags) {
        const tags = data.tags.split(',').map(t => t.trim()).filter(Boolean);
        tags.forEach(tag => {
            const tagElement = document.createElement('span');
            tagElement.className = 'service-tag';
            tagElement.textContent = `#${tag}`;
            tagsContainer.appendChild(tagElement);
        });
    }
    
    // 가격
    const priceElement = document.getElementById('service-price');
    if (data.price != null) {
        const formattedPrice = Number(data.price).toLocaleString();
        const currency = data.currency || '원';
        priceElement.textContent = `${formattedPrice} ${currency}`;
    } else {
        priceElement.textContent = '가격 문의';
    }
    
    // VAT 정보
    const vatInfo = document.getElementById('vat-info');
    vatInfo.textContent = data.vatIncluded ? 'VAT 포함' : 'VAT 비대상';
    
    // 환불 정보
    const refundInfo = document.getElementById('refund-info');
    refundInfo.textContent = data.isRefundable ? '환불 가능' : '환불 불가';
    
    // 제공 기간
    if (data.startDate && data.endDate) {
        const datesElement = document.getElementById('service-dates');
        datesElement.textContent = `${data.startDate} ~ ${data.endDate}`;
    } else {
        document.getElementById('service-period').style.display = 'none';
    }
    
    // 대상 성별
    const genderTargetElement = document.getElementById('gender-target');
    const genderMap = {
        'ALL': '남녀공용',
        'MALE': '남성',
        'FEMALE': '여성'
    };
    genderTargetElement.textContent = genderMap[data.genderTarget] || '남녀공용';
    
    // 설명
    const descriptionElement = document.getElementById('service-description');
    if (data.description) {
        descriptionElement.innerHTML = `<p>${data.description}</p>`;
    } else {
        descriptionElement.innerHTML = '<p>서비스에 대한 상세 설명이 준비 중입니다.</p>';
    }
    
    // 대상 국가
    const targetCountryElement = document.getElementById('target-country');
    const countryMap = {
        'KOR': '한국 (한국어)',
        'JPN': '일본 (일본어)',
        'OTHER': '그 외 국가 (영어)'
    };
    targetCountryElement.textContent = countryMap[data.targetCountry] || '한국 (한국어)';
}

// 병원 정보 로드
async function loadHospitalInfo(itemId) {
    try {
        const response = await fetch(`/api/items/${itemId}`);
        
        if (!response.ok) {
            console.warn('병원 정보를 불러올 수 없습니다.');
            return;
        }
        
        const hospitalData = await response.json();
        renderHospitalInfo(hospitalData);
        
    } catch (error) {
        console.error('병원 정보 로드 중 오류:', error);
    }
}

// 병원 정보 렌더링
function renderHospitalInfo(data) {
    // 병원명
    const hospitalNameElements = [
        document.getElementById('hospital-name'),
        document.getElementById('hospital-name-detail')
    ];
    
    hospitalNameElements.forEach(el => {
        if (el) el.textContent = data.name || '병원명';
    });
    
    // 병원 링크
    const hospitalLink = document.getElementById('hospital-link');
    if (hospitalLink && data.id) {
        hospitalLink.href = `/list?Id=${data.id}`;
    }
    
    // 주소
    const addressElement = document.getElementById('hospital-address');
    if (addressElement) {
        addressElement.textContent = data.address || '주소 정보 없음';
    }
    
    // 연락처
    const phoneElement = document.getElementById('hospital-phone');
    if (phoneElement) {
        phoneElement.textContent = data.phone || '연락처 정보 없음';
    }
    
    // 홈페이지
    const websiteElement = document.getElementById('hospital-website');
    if (websiteElement) {
        if (data.homepage && data.homepage !== '홈페이지 정보 없음' && data.homepage !== '-') {
            let url = data.homepage;
            if (!/^https?:\/\//i.test(url)) {
                url = 'https://' + url;
            }
            websiteElement.innerHTML = `<a href="${url}" target="_blank" rel="noopener">${data.homepage}</a>`;
        } else {
            websiteElement.textContent = '홈페이지 정보 없음';
        }
    }
}

// 관련 서비스 로드
async function loadRelatedServices(itemId, currentServiceId) {
    try {
        const response = await fetch(`/api/items/${itemId}/services`);
        
        if (!response.ok) {
            console.warn('관련 서비스를 불러올 수 없습니다.');
            return;
        }
        
        const services = await response.json();
        
        // 현재 서비스 제외
        const relatedServices = services.filter(s => s.serviceId != currentServiceId);
        
        renderRelatedServices(relatedServices);
        
    } catch (error) {
        console.error('관련 서비스 로드 중 오류:', error);
    }
}

// 관련 서비스 렌더링
function renderRelatedServices(services) {
    const container = document.getElementById('related-services');
    container.innerHTML = '';
    
    if (!services || services.length === 0) {
        container.innerHTML = '<p style="color: #6c757d; text-align: center; grid-column: 1 / -1;">다른 서비스가 없습니다.</p>';
        return;
    }
    
    services.forEach(service => {
        const card = document.createElement('div');
        card.className = 'related-service-card';
        card.onclick = () => goToService(service.serviceId);
        
        const priceText = service.price != null 
            ? `${Number(service.price).toLocaleString()} ${service.currency || '원'}`
            : '가격 문의';
        
        const tags = service.tags 
            ? service.tags.split(',').map(t => t.trim()).filter(Boolean)
                .slice(0, 3)
                .map(t => `<span class="related-service-tag">#${t}</span>`)
                .join('')
            : '';
        
        card.innerHTML = `
            <div class="related-service-image">
                <i class="fas fa-image"></i>
            </div>
            <div class="related-service-title">${service.name || '서비스'}</div>
            <div class="related-service-price">${priceText}</div>
            <div class="related-service-tags">${tags}</div>
        `;
        
        container.appendChild(card);
    });
}

// 썸네일 초기화
function initializeThumbnails() {
    const thumbnails = document.querySelectorAll('.thumbnail');
    thumbnails.forEach((thumb, index) => {
        thumb.addEventListener('click', function() {
            // 모든 썸네일에서 active 클래스 제거
            thumbnails.forEach(t => t.classList.remove('active'));
            // 클릭된 썸네일에 active 클래스 추가
            this.classList.add('active');
            
            // 여기에 메인 이미지 변경 로직 추가 가능
            console.log(`썸네일 ${index + 1} 선택됨`);
        });
    });
}

// 탭 전환
function switchTab(tabName) {
    // 모든 탭과 패널에서 active 클래스 제거
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab-panel').forEach(panel => {
        panel.classList.remove('active');
        panel.style.display = 'none';
    });
    
    // 선택된 탭과 패널에 active 클래스 추가
    event.target.closest('.tab').classList.add('active');
    const panel = document.getElementById(`${tabName}-panel`);
    if (panel) {
        panel.classList.add('active');
        panel.style.display = 'block';
    }
}

// 찜 상태 확인
async function checkFavoriteStatus(itemId) {
    try {
        const response = await fetch(`/favorite/check/${itemId}`);
        
        if (response.ok) {
            const isFavorite = await response.json();
            const btn = document.querySelector('.btn-favorite');
            const icon = btn.querySelector('i');
            
            if (isFavorite) {
                icon.classList.remove('far');
                icon.classList.add('fas');
                btn.classList.add('active');
            } else {
                icon.classList.remove('fas');
                icon.classList.add('far');
                btn.classList.remove('active');
            }
        }
    } catch (error) {
        console.error('찜 상태 확인 중 오류:', error);
    }
}

// 찜하기 토글
function toggleServiceFavorite() {
    const btn = document.querySelector('.btn-favorite');
    const icon = btn.querySelector('i');
    
    if (icon.classList.contains('far')) {
        // 찜하기 추가
        icon.classList.remove('far');
        icon.classList.add('fas');
        btn.classList.add('active');
        
        // 서버에 찜하기 추가 요청
        if (currentItemId) {
            addToFavorite(currentItemId);
        }
    } else {
        // 찜하기 제거
        icon.classList.remove('fas');
        icon.classList.add('far');
        btn.classList.remove('active');
        
        // 서버에 찜하기 제거 요청
        if (currentItemId) {
            removeFromFavorite(currentItemId);
        }
    }
}

// 찜하기 추가
async function addToFavorite(itemId) {
    try {
        const response = await fetch(`/favorite/add/${itemId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            console.log('찜하기 추가 완료');
            
            // 성공 메시지 표시
            showNotification('찜하기에 추가되었습니다. 찜 목록에서 확인하세요!');
        } else if (response.status === 401) {
            alert('로그인이 필요한 서비스입니다.');
            
            // 찜 상태 원복
            const btn = document.querySelector('.btn-favorite');
            const icon = btn.querySelector('i');
            icon.classList.remove('fas');
            icon.classList.add('far');
            btn.classList.remove('active');
            
            window.location.href = '/login';
        } else {
            alert('찜하기 추가에 실패했습니다.');
            
            // 찜 상태 원복
            const btn = document.querySelector('.btn-favorite');
            const icon = btn.querySelector('i');
            icon.classList.remove('fas');
            icon.classList.add('far');
            btn.classList.remove('active');
        }
    } catch (error) {
        console.error('찜하기 추가 중 오류:', error);
        
        // 찜 상태 원복
        const btn = document.querySelector('.btn-favorite');
        const icon = btn.querySelector('i');
        icon.classList.remove('fas');
        icon.classList.add('far');
        btn.classList.remove('active');
    }
}

// 찜하기 제거
async function removeFromFavorite(itemId) {
    try {
        const response = await fetch(`/favorite/remove/${itemId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            console.log('찜하기 제거 완료');
            showNotification('찜하기에서 제거되었습니다.');
        } else {
            alert('찜하기 제거에 실패했습니다.');
            
            // 찜 상태 원복
            const btn = document.querySelector('.btn-favorite');
            const icon = btn.querySelector('i');
            icon.classList.remove('far');
            icon.classList.add('fas');
            btn.classList.add('active');
        }
    } catch (error) {
        console.error('찜하기 제거 중 오류:', error);
        
        // 찜 상태 원복
        const btn = document.querySelector('.btn-favorite');
        const icon = btn.querySelector('i');
        icon.classList.remove('far');
        icon.classList.add('fas');
        btn.classList.add('active');
    }
}

// 예약하기
function goToReservation() {
    if (currentServiceData && currentItemId) {
        window.location.href = `/reservation?itemId=${currentItemId}&serviceId=${currentServiceData.serviceId}`;
    } else if (currentItemId) {
        window.location.href = `/reservation?itemId=${currentItemId}`;
    } else {
        alert('예약 정보를 불러올 수 없습니다.');
    }
}

// 문의하기
function goToInquiry() {
    if (currentItemId) {
        window.location.href = `/inquiry?itemId=${currentItemId}`;
    } else {
        alert('문의 정보를 불러올 수 없습니다.');
    }
}

// 다른 서비스로 이동
function goToService(serviceId) {
    window.location.href = `/service/detail/${serviceId}`;
}

// 에러 표시
function showError(message) {
    const container = document.querySelector('.service-detail-container');
    if (container) {
        container.innerHTML = `
            <div style="text-align: center; padding: 100px 20px; color: #6c757d;">
                <i class="fas fa-exclamation-triangle" style="font-size: 64px; margin-bottom: 20px; color: #ffc107;"></i>
                <h2 style="font-size: 24px; margin-bottom: 10px; color: #212529;">오류가 발생했습니다</h2>
                <p style="font-size: 16px; margin-bottom: 30px;">${message}</p>
                <button onclick="window.history.back()" style="padding: 12px 24px; background: #007bff; color: white; border: none; border-radius: 8px; font-size: 16px; cursor: pointer;">
                    <i class="fas fa-arrow-left"></i> 이전 페이지로
                </button>
            </div>
        `;
    }
}

// 알림 메시지 표시
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === 'success' ? '#28a745' : '#dc3545'};
        color: white;
        padding: 16px 24px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        z-index: 10000;
        font-size: 15px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 10px;
        animation: slideInRight 0.3s ease-out;
    `;
    
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    document.body.appendChild(notification);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        notification.style.animation = 'slideOutRight 0.3s ease-out';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// 전역 함수로 노출
window.switchTab = switchTab;
window.toggleServiceFavorite = toggleServiceFavorite;
window.goToReservation = goToReservation;
window.goToInquiry = goToInquiry;
window.goToService = goToService;

