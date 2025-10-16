// ========================================
// 전역 변수
// ========================================
let currentCompanyId = null;
let currentCompanyName = null;
let selectedCompanyData = null;
let allServices = [];
let currentFilter = 'all';

// ========================================
// 업체 선택
// ========================================
function selectCompany(element) {
    console.log('===== 업체 선택 =====');
    
    // 이전 선택 해제
    document.querySelectorAll('.company-item').forEach(item => {
        item.classList.remove('selected');
    });
    
    // 현재 선택 표시
    element.classList.add('selected');
    
    // 데이터 가져오기
    const companyId = element.getAttribute('data-company-id');
    const companyName = element.getAttribute('data-company-name');
    const itemId = element.getAttribute('data-item-id');
    const approvalStatus = element.getAttribute('data-approval-status');
    const createdAt = element.getAttribute('data-created-at');
    
    console.log('선택된 업체:', {
        companyId,
        companyName,
        itemId,
        approvalStatus,
        createdAt
    });
    
    // 전역 변수에 저장
    selectedCompanyData = {
        companyId,
        companyName,
        itemId,
        approvalStatus,
        createdAt
    };
    
    // 사이드바 업데이트
    updateSidebar(selectedCompanyData);
}

// ========================================
// 사이드바 업데이트
// ========================================
function updateSidebar(data) {
    console.log('사이드바 업데이트:', data);
    
    // 업체 ID와 이름
    document.getElementById('sidebarCompanyId').textContent = 'PN' + data.itemId;
    document.getElementById('sidebarCompanyName').textContent = data.companyName;
    
    // 담당자 (현재 데이터 없음)
    document.getElementById('sidebarManager').textContent = '-';
    
    // 연락처 (현재 데이터 없음)
    document.getElementById('sidebarPhone').textContent = '-';
    
    // 주소 (현재 데이터 없음)
    document.getElementById('sidebarAddress').textContent = '-';
    
    // 등록일
    if (data.createdAt) {
        const date = new Date(data.createdAt);
        const formatted = `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
        document.getElementById('sidebarCreatedDate').textContent = formatted;
    } else {
        document.getElementById('sidebarCreatedDate').textContent = '-';
    }
    
    // 상태
    const statusElement = document.getElementById('sidebarStatus');
    let statusText = '대기';
    let statusClass = 'status-normal';
    
    if (data.approvalStatus === 'APPROVED') {
        statusText = '정상';
        statusClass = 'status-normal';
    } else if (data.approvalStatus === 'REPORTED') {
        statusText = '신고 접수';
        statusClass = 'status-warning';
    } else if (data.approvalStatus === 'SUSPENDED') {
        statusText = '제재중';
        statusClass = 'status-danger';
    }
    
    statusElement.textContent = statusText;
    statusElement.className = 'detail-value ' + statusClass;
    
    // 상품 목록 버튼 활성화
    document.getElementById('productListBtn').disabled = false;
}

// ========================================
// 사이드바에서 모달 열기
// ========================================
function openMedicalServiceModalFromSidebar() {
    console.log('===== 사이드바에서 모달 열기 =====');
    
    if (!selectedCompanyData) {
        alert('업체를 먼저 선택해주세요.');
        return;
    }
    
    currentCompanyId = selectedCompanyData.companyId;
    currentCompanyName = selectedCompanyData.companyName;
    
    console.log('Company ID:', currentCompanyId);
    console.log('Company Name:', currentCompanyName);
    
    // 모달 표시
    const modal = document.getElementById('medicalServiceModal');
    modal.classList.add('active');
    
    // 업체명 설정
    document.getElementById('modalCompanyName').textContent = currentCompanyName;
    
    // 데이터 로드
    loadMedicalServices(currentCompanyId);
}

// ========================================
// 모달 닫기
// ========================================
function closeMedicalServiceModal() {
    const modal = document.getElementById('medicalServiceModal');
    modal.classList.remove('active');
    
    // 데이터 초기화
    currentCompanyId = null;
    currentCompanyName = null;
    allServices = [];
    currentFilter = 'all';
    
    // UI 초기화
    document.getElementById('serviceTableBody').innerHTML = '';
    document.getElementById('modalLoading').style.display = 'flex';
    document.getElementById('serviceListContainer').style.display = 'none';
    document.getElementById('serviceSearch').value = '';
    
    // 필터 탭 초기화
    document.querySelectorAll('.filter-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.querySelectorAll('.filter-tab')[0].classList.add('active');
}

// 모달 외부 클릭 시 닫기
document.addEventListener('click', function(event) {
    const modal = document.getElementById('medicalServiceModal');
    if (event.target === modal) {
        closeMedicalServiceModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        const modal = document.getElementById('medicalServiceModal');
        if (modal.classList.contains('active')) {
            closeMedicalServiceModal();
        }
    }
});

// ========================================
// 의료 서비스 데이터 로드
// ========================================
async function loadMedicalServices(companyId) {
    console.log('===== 서비스 데이터 로드 시작 =====');
    console.log('Company ID:', companyId);
    
    try {
        // 로딩 표시
        document.getElementById('modalLoading').style.display = 'flex';
        document.getElementById('serviceListContainer').style.display = 'none';
        
        // API 호출
        const response = await fetch(`/admin/api/companies/${companyId}/medical-services`);
        
        console.log('응답 상태:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const services = await response.json();
        console.log('받은 서비스 데이터:', services);
        
        allServices = services;
        
        // 로딩 숨기고 컨텐츠 표시
        document.getElementById('modalLoading').style.display = 'none';
        document.getElementById('serviceListContainer').style.display = 'block';
        
        // 통계 업데이트
        updateStatistics(services);
        
        // 테이블 렌더링
        renderServiceTable(services);
        
    } catch (error) {
        console.error('서비스 로드 중 오류:', error);
        document.getElementById('modalLoading').innerHTML = `
            <i class="fas fa-exclamation-circle" style="color: #dc3545;"></i>
            <p style="color: #dc3545;">데이터를 불러오는 중 오류가 발생했습니다.</p>
            <p style="font-size: 14px; color: #6c757d;">${error.message}</p>
        `;
    }
}

// ========================================
// 통계 업데이트
// ========================================
function updateStatistics(services) {
    const total = services.length;
    const active = services.filter(s => isServiceActive(s)).length;
    const inactive = total - active;
    
    document.getElementById('totalServices').textContent = total;
    document.getElementById('activeServices').textContent = active;
    document.getElementById('inactiveServices').textContent = inactive;
    
    console.log('통계 업데이트:', { total, active, inactive });
}

// ========================================
// 서비스 활성 상태 확인 (deleted_at 기준)
// ========================================
function isServiceActive(service) {
    // deleted_at이 null이면 활성, null이 아니면 비활성
    return service.deletedAt === null || service.deletedAt === undefined;
}

// ========================================
// 서비스 테이블 렌더링
// ========================================
function renderServiceTable(services) {
    const tbody = document.getElementById('serviceTableBody');
    const noServices = document.getElementById('noServices');
    
    if (services.length === 0) {
        tbody.innerHTML = '';
        noServices.style.display = 'block';
        return;
    }
    
    noServices.style.display = 'none';
    
    tbody.innerHTML = services.map(service => {
        const isActive = isServiceActive(service);
        const statusClass = isActive ? 'active' : 'inactive';
        const statusText = isActive ? '활성' : '비활성';
        
        return `
            <tr>
                <td><span class="service-id">#${service.serviceId}</span></td>
                <td><span class="service-name">${service.name || '-'}</span></td>
                <td><span class="service-category">${formatCategory(service.tags)}</span></td>
                <td><span class="service-price">${formatPrice(service.price, service.currency)}</span></td>
                <td><span class="service-period">${formatPeriod(service.startDate, service.endDate)}</span></td>
                <td><span class="service-status ${statusClass}">${statusText}</span></td>
                <td><span class="service-date">${formatDate(service.createdAt)}</span></td>
            </tr>
        `;
    }).join('');
    
    console.log(`테이블 렌더링 완료: ${services.length}개 서비스`);
}

// ========================================
// 필터링
// ========================================
function filterServices(filterType) {
    console.log('필터 변경:', filterType);
    currentFilter = filterType;
    
    // 탭 활성화 상태 변경
    document.querySelectorAll('.filter-tab').forEach(tab => {
        tab.classList.remove('active');
    });
    event.target.classList.add('active');
    
    // 필터링된 서비스
    let filteredServices = allServices;
    
    if (filterType === 'active') {
        filteredServices = allServices.filter(s => isServiceActive(s));
    } else if (filterType === 'inactive') {
        filteredServices = allServices.filter(s => !isServiceActive(s));
    }
    
    // 검색어가 있으면 추가 필터링
    const searchTerm = document.getElementById('serviceSearch').value;
    if (searchTerm) {
        filteredServices = filteredServices.filter(s => 
            s.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            (s.tags && s.tags.toLowerCase().includes(searchTerm.toLowerCase()))
        );
    }
    
    renderServiceTable(filteredServices);
}

// ========================================
// 검색
// ========================================
function searchServices() {
    const searchTerm = document.getElementById('serviceSearch').value.toLowerCase();
    console.log('검색어:', searchTerm);
    
    let filteredServices = allServices;
    
    // 현재 필터 적용
    if (currentFilter === 'active') {
        filteredServices = filteredServices.filter(s => isServiceActive(s));
    } else if (currentFilter === 'inactive') {
        filteredServices = filteredServices.filter(s => !isServiceActive(s));
    }
    
    // 검색어 필터링
    if (searchTerm) {
        filteredServices = filteredServices.filter(s => 
            (s.name && s.name.toLowerCase().includes(searchTerm)) ||
            (s.tags && s.tags.toLowerCase().includes(searchTerm)) ||
            (s.serviceId && s.serviceId.toString().includes(searchTerm))
        );
    }
    
    renderServiceTable(filteredServices);
}

// ========================================
// 유틸리티 함수
// ========================================

// 카테고리 포맷
function formatCategory(tags) {
    if (!tags) return '-';
    const tagArray = tags.split(',');
    return tagArray[0] || '-';
}

// 가격 포맷
function formatPrice(price, currency) {
    if (!price) return '-';
    const formatted = new Intl.NumberFormat('ko-KR').format(price);
    return `${formatted} ${currency || 'KRW'}`;
}

// 기간 포맷
function formatPeriod(startDate, endDate) {
    if (!startDate || !endDate) return '-';
    
    const start = new Date(startDate);
    const end = new Date(endDate);
    
    const formatDate = (date) => {
        return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
    };
    
    return `${formatDate(start)} ~ ${formatDate(end)}`;
}

// 날짜 포맷
function formatDate(dateString) {
    if (!dateString) return '-';
    
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
}

console.log('admin_manage_company_detail.js 로드 완료');

