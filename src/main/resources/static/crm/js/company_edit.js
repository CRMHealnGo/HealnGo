// Company Edit Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    console.log('Company Edit Page DOM loaded');
    initializeCompanyEditPage();
    setupTabNavigation();
    setupFormValidation();
    setupSpecializationTags();
    setupCharacterCounter();
    setupFormSubmission();
    setupRealTimePreview();
});

/**
 * 회사 정보 수정 페이지 초기화
 */
function initializeCompanyEditPage() {
    console.log('Company Edit Page initialized');
    
    // 현재 페이지 활성화
    highlightCurrentPage();
    
    // 폼 상태 초기화
    initializeFormState();
    
    // 자동 저장 설정
    setupAutoSave();
}

/**
 * 현재 페이지 하이라이트
 */
function highlightCurrentPage() {
    const sidebarLinks = document.querySelectorAll('.sidebar .nav-link');
    sidebarLinks.forEach(link => {
        if (link.textContent.includes('업체 정보 수정')) {
            link.classList.add('active');
        }
    });
}

/**
 * 탭 네비게이션 설정
 */
function setupTabNavigation() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');
    
    console.log('탭 버튼 개수:', tabButtons.length);
    console.log('탭 패널 개수:', tabPanes.length);
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const targetTab = this.getAttribute('data-tab');
            console.log('탭 클릭됨:', targetTab);
            
            // 모든 탭 버튼에서 active 클래스 제거
            tabButtons.forEach(btn => btn.classList.remove('active'));
            
            // 모든 탭 패널에서 active 클래스 제거
            tabPanes.forEach(pane => pane.classList.remove('active'));
            
            // 클릭된 탭 버튼에 active 클래스 추가
            this.classList.add('active');
            
            // 해당 탭 패널에 active 클래스 추가
            const targetPane = document.getElementById(targetTab);
            if (targetPane) {
                targetPane.classList.add('active');
                console.log('탭 패널 활성화됨:', targetTab);
            } else {
                console.error('탭 패널을 찾을 수 없음:', targetTab);
            }
            
            // 탭 변경 이벤트 발생
            onTabChange(targetTab);
        });
    });
}

/**
 * 탭 변경 시 호출되는 함수
 */
function onTabChange(tabName) {
    console.log(`Tab changed to: ${tabName}`);
    
    // 폼 검증 상태 업데이트
    updateFormValidationStatus();
    
    // 진행률 업데이트
    updateProgressBar();
}

/**
 * 폼 검증 설정
 */
function setupFormValidation() {
    const requiredFields = document.querySelectorAll('input[required], textarea[required]');
    
    requiredFields.forEach(field => {
        field.addEventListener('blur', function() {
            validateField(this);
        });
        
        field.addEventListener('input', function() {
            // 실시간 검증 (입력 중에는 에러 메시지 제거)
            clearFieldError(this);
        });
    });
}

/**
 * 필드 검증
 */
function validateField(field) {
    const value = field.value.trim();
    const fieldName = field.getAttribute('name');
    let isValid = true;
    let errorMessage = '';
    
    // 필수 필드 검증
    if (field.hasAttribute('required') && !value) {
        isValid = false;
        errorMessage = '필수 입력 항목입니다.';
    }
    
    // 이메일 형식 검증
    if (fieldName === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            isValid = false;
            errorMessage = '올바른 이메일 형식을 입력하세요.';
        }
    }
    
    // 전화번호 형식 검증
    if ((fieldName === 'mainPhone' || fieldName === 'mobilePhone') && value) {
        const phoneRegex = /^[0-9-+\s()]+$/;
        if (!phoneRegex.test(value)) {
            isValid = false;
            errorMessage = '올바른 전화번호 형식을 입력하세요.';
        }
    }
    
    // URL 형식 검증
    if (fieldName === 'website' && value) {
        try {
            new URL(value.startsWith('http') ? value : 'https://' + value);
        } catch (e) {
            isValid = false;
            errorMessage = '올바른 웹사이트 URL을 입력하세요.';
        }
    }
    
    // 검증 결과 처리
    if (isValid) {
        clearFieldError(field);
    } else {
        showFieldError(field, errorMessage);
    }
    
    return isValid;
}

/**
 * 필드 에러 표시
 */
function showFieldError(field, message) {
    clearFieldError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'field-error';
    errorDiv.textContent = message;
    errorDiv.style.cssText = `
        color: #E53E3E;
        font-size: 12px;
        margin-top: 4px;
    `;
    
    field.parentNode.appendChild(errorDiv);
    field.style.borderColor = '#E53E3E';
}

/**
 * 필드 에러 제거
 */
function clearFieldError(field) {
    const existingError = field.parentNode.querySelector('.field-error');
    if (existingError) {
        existingError.remove();
    }
    field.style.borderColor = '';
}

/**
 * 전문 분야 태그 설정
 */
function setupSpecializationTags() {
    const input = document.getElementById('specializationInput');
    const tagsContainer = document.getElementById('specializationTags');
    
    if (!input || !tagsContainer) return;
    
    // Enter 키로 태그 추가
    input.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            e.preventDefault();
            addSpecializationTag(this.value.trim());
            this.value = '';
        }
    });
    
    // 기존 태그 제거 이벤트
    tagsContainer.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-tag')) {
            e.target.parentNode.remove();
            updateProgressBar();
        }
    });
}

/**
 * 전문 분야 태그 추가
 */
function addSpecializationTag(value) {
    if (!value) return;
    
    const tagsContainer = document.getElementById('specializationTags');
    const existingTags = Array.from(tagsContainer.querySelectorAll('.tag span')).map(tag => tag.textContent);
    
    // 중복 체크
    if (existingTags.includes(value)) {
        showNotification('이미 추가된 전문 분야입니다.', 'warning');
        return;
    }
    
    // 태그 생성
    const tagElement = document.createElement('span');
    tagElement.className = 'tag';
    tagElement.innerHTML = `
        <span>${value}</span>
        <i class="fas fa-times remove-tag"></i>
    `;
    
    tagsContainer.appendChild(tagElement);
    
    // 진행률 업데이트
    updateProgressBar();
    
    showNotification('전문 분야가 추가되었습니다.', 'success');
}

/**
 * 문자 수 카운터 설정
 */
function setupCharacterCounter() {
    const textarea = document.getElementById('companyIntroduction');
    const counter = document.getElementById('charCount');
    
    if (!textarea || !counter) return;
    
    textarea.addEventListener('input', function() {
        const currentLength = this.value.length;
        const maxLength = this.getAttribute('maxlength') || 500;
        
        counter.textContent = currentLength;
        
        // 문자 수에 따른 스타일 변경
        if (currentLength > maxLength * 0.9) {
            counter.style.color = '#E53E3E';
        } else if (currentLength > maxLength * 0.7) {
            counter.style.color = '#F6AD55';
        } else {
            counter.style.color = '#718096';
        }
    });
}

/**
 * 폼 제출 설정
 */
function setupFormSubmission() {
    const saveButton = document.querySelector('.btn-save');
    const cancelButton = document.querySelector('.btn-cancel');
    
    if (saveButton) {
        saveButton.addEventListener('click', function() {
            saveCompanyInfo();
        });
    }
    
    if (cancelButton) {
        cancelButton.addEventListener('click', function() {
            confirmCancel();
        });
    }
}

/**
 * 회사 정보 저장
 */
function saveCompanyInfo() {
    console.log('Saving company information...');
    
    // 폼 검증
    if (!validateForm()) {
        showNotification('입력 정보를 확인해주세요.', 'error');
        return;
    }
    
    // 로딩 상태 표시
    showLoadingState(true);
    
    // 폼 데이터 수집
    const formData = collectFormData();
    
    // 실제 구현에서는 AJAX 요청으로 서버에 저장
    setTimeout(() => {
        showLoadingState(false);
        showNotification('회사 정보가 저장되었습니다.', 'success');
        updateLastModifiedTime();
    }, 1000);
}

/**
 * 폼 검증
 */
function validateForm() {
    const requiredFields = document.querySelectorAll('input[required], textarea[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });
    
    return isValid;
}

/**
 * 폼 데이터 수집
 */
function collectFormData() {
    const data = {
        basicInfo: {
            companyName: document.getElementById('companyName')?.value || '',
            businessNumber: document.getElementById('businessNumber')?.value || '',
            representative: document.getElementById('representative')?.value || '',
            establishmentDate: document.getElementById('establishmentDate')?.value || ''
        },
        contactInfo: {
            email: document.getElementById('email')?.value || '',
            mainPhone: document.getElementById('mainPhone')?.value || '',
            mobilePhone: document.getElementById('mobilePhone')?.value || '',
            fax: document.getElementById('fax')?.value || '',
            address: document.getElementById('address')?.value || '',
            detailAddress: document.getElementById('detailAddress')?.value || '',
            website: document.getElementById('website')?.value || ''
        },
        detailInfo: {
            employeeCount: document.getElementById('employeeCount')?.value || '',
            specializations: Array.from(document.querySelectorAll('.specialization-tags .tag span')).map(tag => tag.textContent),
            companyIntroduction: document.getElementById('companyIntroduction')?.value || ''
        }
    };
    
    return data;
}

/**
 * 취소 확인
 */
function confirmCancel() {
    if (confirm('변경사항이 저장되지 않습니다. 정말 취소하시겠습니까?')) {
        window.history.back();
    }
}

/**
 * 실시간 미리보기 설정
 */
function setupRealTimePreview() {
    const previewFields = [
        'companyName',
        'representative',
        'email',
        'mainPhone',
        'address'
    ];
    
    previewFields.forEach(fieldId => {
        const field = document.getElementById(fieldId);
        if (field) {
            field.addEventListener('input', function() {
                updatePreview(fieldId, this.value);
            });
        }
    });
}

/**
 * 미리보기 업데이트
 */
function updatePreview(fieldId, value) {
    // 실제 구현에서는 미리보기 영역의 해당 요소를 업데이트
    console.log(`Preview updated: ${fieldId} = ${value}`);
}

/**
 * 진행률 바 업데이트
 */
function updateProgressBar() {
    const requiredFields = document.querySelectorAll('input[required], textarea[required]');
    const filledFields = Array.from(requiredFields).filter(field => field.value.trim());
    const progress = Math.round((filledFields.length / requiredFields.length) * 100);
    
    const progressFill = document.querySelector('.progress-fill');
    const progressText = document.querySelector('.status-value');
    
    if (progressFill) {
        progressFill.style.width = `${progress}%`;
    }
    
    if (progressText) {
        progressText.textContent = `${progress}%`;
    }
}

/**
 * 폼 상태 초기화
 */
function initializeFormState() {
    updateProgressBar();
}

/**
 * 자동 저장 설정
 */
function setupAutoSave() {
    let autoSaveTimeout;
    
    const inputs = document.querySelectorAll('input, textarea');
    inputs.forEach(input => {
        input.addEventListener('input', function() {
            clearTimeout(autoSaveTimeout);
            autoSaveTimeout = setTimeout(() => {
                // 자동 저장 로직 (실제 구현에서는 서버에 저장)
                console.log('Auto-saving form data...');
            }, 3000);
        });
    });
}

/**
 * 마지막 수정 시간 업데이트
 */
function updateLastModifiedTime() {
    const timeElement = document.querySelector('.status-value');
    if (timeElement) {
        timeElement.textContent = '방금 전';
    }
}

/**
 * 로딩 상태 표시
 */
function showLoadingState(isLoading) {
    const saveButton = document.querySelector('.btn-save');
    
    if (isLoading) {
        saveButton.disabled = true;
        saveButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 저장 중...';
    } else {
        saveButton.disabled = false;
        saveButton.innerHTML = '<i class="fas fa-save"></i> 저장하기';
    }
}

/**
 * 알림 표시
 */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    
    const colors = {
        success: '#48BB78',
        error: '#E53E3E',
        warning: '#F6AD55',
        info: '#3182CE'
    };
    
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${colors[type] || colors.info};
        color: white;
        padding: 12px 20px;
        border-radius: 8px;
        z-index: 1000;
        font-size: 14px;
        font-weight: 500;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transform: translateX(100%);
        transition: transform 0.3s ease;
    `;
    
    document.body.appendChild(notification);
    
    // 애니메이션
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // 3초 후 제거
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

/**
 * 폼 검증 상태 업데이트
 */
function updateFormValidationStatus() {
    // 현재 활성 탭의 필드들 검증
    const activeTab = document.querySelector('.tab-pane.active');
    const fields = activeTab.querySelectorAll('input, textarea');
    
    fields.forEach(field => {
        if (field.value.trim()) {
            validateField(field);
        }
    });
}

// 내보내기
window.CompanyEdit = {
    addSpecializationTag,
    updateProgressBar,
    showNotification,
    saveCompanyInfo
};
