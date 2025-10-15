/* ==========================================
   신고 / 문의하기 페이지 JavaScript
   HealnGo CRM System
   ========================================== */

// ===== 페이지 로드 시 초기화 =====
document.addEventListener('DOMContentLoaded', function() {
    console.log('📋 신고/문의 페이지 초기화 시작...');
    
    initializeTabs();
    initializeFileUpload();
    initializeFormValidation();
    initializeFormSubmit();
    
    console.log('✅ 초기화 완료!');
});

// ===== 탭 전환 기능 =====
function initializeTabs() {
    const tabs = document.querySelectorAll('.inquiry-tab');
    
    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const targetTab = this.getAttribute('data-tab');
            switchTab(targetTab);
        });
    });
}

function switchTab(tabName) {
    console.log('🔄 탭 전환:', tabName);
    
    // 모든 탭 버튼에서 active 클래스 제거
    const tabs = document.querySelectorAll('.inquiry-tab');
    tabs.forEach(tab => tab.classList.remove('active'));
    
    // 클릭된 탭 버튼에 active 클래스 추가
    const activeTab = document.querySelector(`[data-tab="${tabName}"]`);
    if (activeTab) {
        activeTab.classList.add('active');
        console.log('✅ 탭 버튼 활성화:', tabName);
    }
    
    // 폼 콘텐츠 전환
    const reportFormContent = document.getElementById('reportFormContent');
    const inquiryFormContent = document.getElementById('inquiryFormContent');
    
    if (!reportFormContent || !inquiryFormContent) {
        console.error('❌ 폼 콘텐츠를 찾을 수 없습니다!');
        return;
    }
    
    if (tabName === 'report') {
        reportFormContent.style.display = 'block';
        inquiryFormContent.style.display = 'none';
        console.log('📝 신고하기 폼 표시');
        
        // 신고 폼의 필수 필드 활성화
        setRequiredFields(reportFormContent, true);
        setRequiredFields(inquiryFormContent, false);
    } else if (tabName === 'inquiry') {
        reportFormContent.style.display = 'none';
        inquiryFormContent.style.display = 'block';
        console.log('💬 문의하기 폼 표시');
        
        // 문의 폼의 필수 필드 활성화
        setRequiredFields(reportFormContent, false);
        setRequiredFields(inquiryFormContent, true);
    }
    
    // 폼 초기화
    resetForm();
}

// ===== 필수 필드 설정 =====
function setRequiredFields(form, isRequired) {
    // type이 file이 아닌 input, select, textarea만 선택
    const inputs = form.querySelectorAll('input:not([type="file"]), select, textarea');
    
    inputs.forEach(input => {
        // file 타입은 제외
        if (input.type === 'file') return;
        
        if (isRequired) {
            // 필수 필드로 설정 (file과 targetUrl 제외)
            if (input.name !== 'attachment' && input.name !== 'targetUrl') {
                input.setAttribute('required', 'required');
            }
        } else {
            // 필수 필드 해제
            input.removeAttribute('required');
        }
    });
    
    console.log(`📋 필수 필드 ${isRequired ? '활성화' : '비활성화'} 완료`);
}

// ===== 파일 업로드 기능 =====
function initializeFileUpload() {
    // 신고 파일 업로드
    const reportFileInput = document.getElementById('reportFile');
    const reportFileName = document.getElementById('reportFileName');
    
    if (reportFileInput) {
        reportFileInput.addEventListener('change', function(e) {
            handleFileSelect(e, reportFileName);
        });
        
        // 드래그 앤 드롭
        const reportLabel = reportFileInput.nextElementSibling;
        setupDragAndDrop(reportLabel, reportFileInput);
    }
    
    // 문의 파일 업로드
    const inquiryFileInput = document.getElementById('inquiryFile');
    const inquiryFileName = document.getElementById('inquiryFileName');
    
    if (inquiryFileInput) {
        inquiryFileInput.addEventListener('change', function(e) {
            handleFileSelect(e, inquiryFileName);
        });
        
        // 드래그 앤 드롭
        const inquiryLabel = inquiryFileInput.nextElementSibling;
        setupDragAndDrop(inquiryLabel, inquiryFileInput);
    }
}

// ===== 파일 선택 처리 =====
function handleFileSelect(e, fileNameDisplay) {
    const file = e.target.files[0];
    
    if (file) {
        // 파일 크기 체크 (10MB)
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            showAlert('파일 크기는 10MB를 초과할 수 없습니다.', 'error');
            e.target.value = '';
            return;
        }
        
        // 파일 형식 체크
        const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 
                             'application/pdf', 'application/msword', 
                             'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
        
        if (!allowedTypes.includes(file.type)) {
            showAlert('지원하지 않는 파일 형식입니다.', 'error');
            e.target.value = '';
            return;
        }
        
        // 파일명 표시
        fileNameDisplay.textContent = file.name;
        fileNameDisplay.classList.add('active');
    } else {
        fileNameDisplay.textContent = '';
        fileNameDisplay.classList.remove('active');
    }
}

// ===== 드래그 앤 드롭 설정 =====
function setupDragAndDrop(label, fileInput) {
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        label.addEventListener(eventName, preventDefaults, false);
    });
    
    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }
    
    ['dragenter', 'dragover'].forEach(eventName => {
        label.addEventListener(eventName, function() {
            label.style.borderColor = '#667eea';
            label.style.background = 'rgba(102, 126, 234, 0.05)';
        });
    });
    
    ['dragleave', 'drop'].forEach(eventName => {
        label.addEventListener(eventName, function() {
            label.style.borderColor = '#d0d7de';
            label.style.background = '#fafbfc';
        });
    });
    
    label.addEventListener('drop', function(e) {
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            fileInput.files = files;
            const event = new Event('change', { bubbles: true });
            fileInput.dispatchEvent(event);
        }
    });
}

// ===== 폼 검증 =====
function initializeFormValidation() {
    // 실시간 검증
    const inputs = document.querySelectorAll('.form-control');
    
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            if (this.classList.contains('is-invalid')) {
                validateField(this);
            }
        });
    });
}

function validateField(field) {
    const value = field.value.trim();
    const fieldName = field.name;
    let isValid = true;
    let errorMessage = '';
    
    // 필수 필드 체크
    if (field.hasAttribute('required') && !value) {
        isValid = false;
        errorMessage = '이 필드는 필수입니다.';
    }
    
    // 이메일 검증
    if (fieldName === 'email' && value) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(value)) {
            isValid = false;
            errorMessage = '올바른 이메일 주소를 입력해주세요.';
        }
    }
    
    // 전화번호 검증
    if (fieldName === 'phone' && value) {
        const phonePattern = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/;
        if (!phonePattern.test(value.replace(/\s/g, ''))) {
            isValid = false;
            errorMessage = '올바른 전화번호를 입력해주세요. (예: 010-1234-5678)';
        }
    }
    
    // URL 검증 (신고 대상 URL)
    if (fieldName === 'targetUrl' && value) {
        try {
            new URL(value);
        } catch (e) {
            isValid = false;
            errorMessage = '올바른 URL을 입력해주세요. (예: https://example.com)';
        }
    }
    
    // 검증 결과 표시
    if (isValid) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        removeErrorMessage(field);
    } else {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        showErrorMessage(field, errorMessage);
    }
    
    return isValid;
}

function showErrorMessage(field, message) {
    removeErrorMessage(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    
    field.parentNode.appendChild(errorDiv);
}

function removeErrorMessage(field) {
    const existingError = field.parentNode.querySelector('.invalid-feedback');
    if (existingError) {
        existingError.remove();
    }
}

// ===== 폼 제출 =====
function initializeFormSubmit() {
    const form = document.getElementById('mainForm');
    
    if (form) {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
            handleFormSubmit(this);
        });
    }
    
    // 초기화 버튼
    const resetButtons = document.querySelectorAll('button[type="reset"]');
    resetButtons.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('작성 중인 내용을 모두 지우시겠습니까?')) {
                resetForm();
            }
        });
    });
}

function handleFormSubmit(form) {
    // 현재 활성화된 탭 확인
    const activeTab = document.querySelector('.inquiry-tab.active');
    const isReportForm = activeTab.getAttribute('data-tab') === 'report';
    
    console.log('📤 폼 제출 시도:', isReportForm ? '신고하기' : '문의하기');
    
    // 활성화된 폼 콘텐츠 가져오기
    const activeFormContent = isReportForm ? 
        document.getElementById('reportFormContent') : 
        document.getElementById('inquiryFormContent');
    
    // 폼 검증
    const inputs = activeFormContent.querySelectorAll('.form-control[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!validateField(input)) {
            isValid = false;
        }
    });
    
    if (!isValid) {
        showAlert('필수 항목을 모두 올바르게 입력해주세요.', 'error');
        
        // 첫 번째 오류 필드로 스크롤
        const firstInvalidField = activeFormContent.querySelector('.is-invalid');
        if (firstInvalidField) {
            firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
            firstInvalidField.focus();
        }
        return;
    }
    
    // 폼 데이터 수집
    const formData = new FormData();
    
    // 활성화된 폼의 입력값만 수집
    const formInputs = activeFormContent.querySelectorAll('input, select, textarea');
    formInputs.forEach(input => {
        if (input.type === 'file' && input.files[0]) {
            formData.append(input.name, input.files[0]);
        } else if (input.value.trim()) {
            formData.append(input.name, input.value.trim());
        }
    });
    
    // 폼 타입 추가
    formData.append('formType', isReportForm ? 'report' : 'inquiry');
    
    // 제출 버튼 비활성화 및 로딩 상태
    const submitButton = activeFormContent.querySelector('button[type="submit"]');
    submitButton.classList.add('loading');
    submitButton.disabled = true;
    
    // 서버로 전송
    submitFormData(formData, submitButton);
}

function submitFormData(formData, submitButton) {
    // AJAX 요청
    fetch('/inquiry/submit', {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('서버 응답 오류');
        }
        return response.json();
    })
    .then(data => {
        // 성공 처리
        submitButton.classList.remove('loading');
        submitButton.disabled = false;
        
        showAlert('제출이 완료되었습니다. 빠른 시일 내에 답변드리겠습니다.', 'success');
        
        // 폼 초기화
        setTimeout(() => {
            resetForm();
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }, 2000);
    })
    .catch(error => {
        // 오류 처리
        console.error('Error:', error);
        submitButton.classList.remove('loading');
        submitButton.disabled = false;
        
        showAlert('오류가 발생했습니다. 다시 시도해주세요.', 'error');
    });
}

// ===== 알림 메시지 표시 =====
function showAlert(message, type = 'success') {
    // 기존 알림 제거
    const existingAlert = document.querySelector('.alert');
    if (existingAlert) {
        existingAlert.remove();
    }
    
    // 새 알림 생성
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    
    const icon = type === 'success' ? 
        '<i class="fas fa-check-circle"></i>' : 
        '<i class="fas fa-exclamation-circle"></i>';
    
    alert.innerHTML = `${icon} ${message}`;
    
    // 폼 컨테이너 상단에 추가
    const formContainer = document.querySelector('.inquiry-form-container');
    formContainer.insertBefore(alert, formContainer.firstChild);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        alert.style.opacity = '0';
        alert.style.transform = 'translateY(-10px)';
        setTimeout(() => alert.remove(), 300);
    }, 5000);
}

// ===== 폼 초기화 =====
function resetForm() {
    // 모든 입력 필드 초기화
    const inputs = document.querySelectorAll('.form-control');
    inputs.forEach(input => {
        input.value = '';
        input.classList.remove('is-valid', 'is-invalid');
    });
    
    // 파일 입력 초기화
    const fileInputs = document.querySelectorAll('.form-control-file');
    fileInputs.forEach(input => {
        input.value = '';
    });
    
    // 파일명 표시 초기화
    const fileNames = document.querySelectorAll('.file-name');
    fileNames.forEach(fileName => {
        fileName.textContent = '';
        fileName.classList.remove('active');
    });
    
    // 오류 메시지 제거
    const errorMessages = document.querySelectorAll('.invalid-feedback');
    errorMessages.forEach(msg => msg.remove());
    
    // 알림 메시지 제거
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => alert.remove());
}

// ===== 전화번호 자동 포맷팅 (선택적 기능) =====
document.addEventListener('input', function(e) {
    if (e.target.name === 'phone') {
        let value = e.target.value.replace(/[^0-9]/g, '');
        
        if (value.length > 3 && value.length <= 7) {
            value = value.slice(0, 3) + '-' + value.slice(3);
        } else if (value.length > 7) {
            value = value.slice(0, 3) + '-' + value.slice(3, 7) + '-' + value.slice(7, 11);
        }
        
        e.target.value = value;
    }
});

// ===== 페이지 이탈 시 경고 (작성 중인 내용이 있을 때) =====
let formModified = false;

document.addEventListener('input', function(e) {
    if (e.target.classList.contains('form-control')) {
        formModified = true;
    }
});

window.addEventListener('beforeunload', function(e) {
    if (formModified) {
        e.preventDefault();
        e.returnValue = '작성 중인 내용이 있습니다. 페이지를 나가시겠습니까?';
        return e.returnValue;
    }
});

// ===== 폼 제출 후 경고 해제 =====
document.addEventListener('submit', function() {
    formModified = false;
});

// ===== 디버그용 콘솔 로그 (개발 시에만 활성화) =====
const DEBUG_MODE = false;

function log(...args) {
    if (DEBUG_MODE) {
        console.log('[Inquiry Form]', ...args);
    }
}

log('Inquiry form script loaded successfully');
