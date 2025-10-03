// 회원가입 페이지 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // 사용자 타입 선택 기능
    const userTypeButtons = document.querySelectorAll('.user-type-btn');
    const signupForms = document.querySelectorAll('.signup-form');
    
    userTypeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userType = this.dataset.type;
            
            // 모든 버튼에서 active 클래스 제거
            userTypeButtons.forEach(btn => btn.classList.remove('active'));
            // 클릭된 버튼에 active 클래스 추가
            this.classList.add('active');
            
            // 모든 폼 숨기기
            signupForms.forEach(form => form.classList.remove('active'));
            // 선택된 타입의 폼만 보이기
            document.getElementById(userType + '-signup-form').classList.add('active');
        });
    });

    // 폼 유효성 검사 및 제출
    setupFormValidation('social-signup-form');
    setupFormValidation('company-signup-form');
    setupFormValidation('manager-signup-form');

    // 아바타 미리보기 기능
    setupAvatarPreview('social-avatar', 'social-avatar-preview');
    setupAvatarPreview('company-avatar', 'company-avatar-preview');
});

// 폼 유효성 검사 설정
function setupFormValidation(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (validateForm(form)) {
            submitForm(form);
        }
    });

    // 실시간 유효성 검사
    const inputs = form.querySelectorAll('input, select');
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });
        
        input.addEventListener('input', function() {
            clearError(this);
        });
    });
}

// 폼 유효성 검사
function validateForm(form) {
    let isValid = true;
    const inputs = form.querySelectorAll('input[required], select[required]');
    
    inputs.forEach(input => {
        if (!validateField(input)) {
            isValid = false;
        }
    });

    // 비밀번호 확인 검사
    const password = form.querySelector('input[name="password"]');
    const passwordConfirm = form.querySelector('input[name="passwordConfirm"]');
    
    if (password && passwordConfirm) {
        if (password.value !== passwordConfirm.value) {
            showError(passwordConfirm, '비밀번호가 일치하지 않습니다.');
            isValid = false;
        }
    }

    return isValid;
}

// 개별 필드 유효성 검사
function validateField(field) {
    const value = field.value.trim();
    const fieldName = field.name;
    let isValid = true;
    let errorMessage = '';

    // 필수 필드 검사
    if (field.hasAttribute('required') && !value) {
        errorMessage = '이 필드는 필수입니다.';
        isValid = false;
    }

    // 이메일 유효성 검사
    if (fieldName === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            errorMessage = '올바른 이메일 형식을 입력해주세요.';
            isValid = false;
        }
    }

    // 비밀번호 유효성 검사
    if (fieldName === 'password' && value) {
        if (value.length < 8) {
            errorMessage = '비밀번호는 8자 이상이어야 합니다.';
            isValid = false;
        } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
            errorMessage = '비밀번호는 영문과 숫자를 포함해야 합니다.';
            isValid = false;
        }
    }

    // 전화번호 유효성 검사
    if (fieldName === 'phone' && value) {
        const phoneRegex = /^010-\d{4}-\d{4}$/;
        if (!phoneRegex.test(value)) {
            errorMessage = '올바른 전화번호 형식을 입력해주세요. (010-1234-5678)';
            isValid = false;
        }
    }

    // 사업자등록번호 유효성 검사
    if (fieldName === 'bizNo' && value) {
        const bizNoRegex = /^\d{3}-\d{2}-\d{5}$/;
        if (!bizNoRegex.test(value)) {
            errorMessage = '올바른 사업자등록번호 형식을 입력해주세요. (123-45-67890)';
            isValid = false;
        }
    }

    if (!isValid) {
        showError(field, errorMessage);
    } else {
        clearError(field);
    }

    return isValid;
}

// 에러 메시지 표시
function showError(field, message) {
    const errorElement = document.getElementById(field.id + '-error');
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
    
    field.style.borderColor = '#dc3545';
    field.style.backgroundColor = '#fff5f5';
}

// 에러 메시지 제거
function clearError(field) {
    const errorElement = document.getElementById(field.id + '-error');
    if (errorElement) {
        errorElement.textContent = '';
        errorElement.style.display = 'none';
    }
    
    field.style.borderColor = '#e1e5e9';
    field.style.backgroundColor = '#f8f9fa';
}

// 폼 제출
function submitForm(form) {
    const submitBtn = form.querySelector('.signup-btn');
    const originalText = submitBtn.textContent;
    
    // 로딩 상태 표시
    submitBtn.classList.add('loading');
    submitBtn.textContent = '처리 중...';
    submitBtn.disabled = true;

    // FormData 생성
    const formData = new FormData(form);
    
    // fetch로 서버에 전송
    fetch(form.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('서버 오류가 발생했습니다.');
    })
    .then(data => {
        if (data.success) {
            showMessage('회원가입이 완료되었습니다!', 'success');
            // 3초 후 로그인 페이지로 이동
            setTimeout(() => {
                window.location.href = '/crm/login';
            }, 3000);
        } else {
            showMessage(data.message || '회원가입에 실패했습니다.', 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showMessage('네트워크 오류가 발생했습니다.', 'error');
    })
    .finally(() => {
        // 로딩 상태 해제
        submitBtn.classList.remove('loading');
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
    });
}

// 메시지 표시
function showMessage(message, type) {
    // 기존 메시지 제거
    const existingMessage = document.querySelector('.message');
    if (existingMessage) {
        existingMessage.remove();
    }
    
    // 새 메시지 생성
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.textContent = message;
    
    // 폼 앞에 삽입
    const activeForm = document.querySelector('.signup-form.active');
    activeForm.insertBefore(messageDiv, activeForm.firstChild);
    
    // 5초 후 자동 제거
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.remove();
        }
    }, 5000);
}

// 아바타 미리보기 설정
function setupAvatarPreview(inputId, previewId) {
    const input = document.getElementById(inputId);
    const preview = document.getElementById(previewId);
    
    if (!input || !preview) return;
    
    input.addEventListener('change', function(e) {
        const file = e.target.files[0];
        
        if (file) {
            // 파일 크기 검사 (5MB 제한)
            if (file.size > 5 * 1024 * 1024) {
                alert('파일 크기는 5MB 이하여야 합니다.');
                this.value = '';
                return;
            }
            
            // 이미지 파일 검사
            if (!file.type.startsWith('image/')) {
                alert('이미지 파일만 업로드 가능합니다.');
                this.value = '';
                return;
            }
            
            // 미리보기 표시
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.innerHTML = `<img src="${e.target.result}" alt="미리보기">`;
            };
            reader.readAsDataURL(file);
        } else {
            preview.innerHTML = '';
        }
    });
}

// 전화번호 자동 포맷팅
document.addEventListener('input', function(e) {
    if (e.target.type === 'tel') {
        let value = e.target.value.replace(/\D/g, '');
        
        if (value.length >= 11) {
            value = value.substring(0, 11);
            value = value.replace(/(\d{3})(\d{4})(\d{4})/, '$1-$2-$3');
        } else if (value.length >= 7) {
            value = value.replace(/(\d{3})(\d{4})/, '$1-$2');
        } else if (value.length >= 3) {
            value = value.replace(/(\d{3})/, '$1-');
        }
        
        e.target.value = value;
    }
});

// 사업자등록번호 자동 포맷팅
document.addEventListener('input', function(e) {
    if (e.target.name === 'bizNo') {
        let value = e.target.value.replace(/\D/g, '');
        
        if (value.length >= 10) {
            value = value.substring(0, 10);
            value = value.replace(/(\d{3})(\d{2})(\d{5})/, '$1-$2-$3');
        } else if (value.length >= 5) {
            value = value.replace(/(\d{3})(\d{2})/, '$1-$2');
        } else if (value.length >= 3) {
            value = value.replace(/(\d{3})/, '$1-');
        }
        
        e.target.value = value;
    }
});

// 엔터키로 폼 제출 방지 (비밀번호 확인 필드 제외)
document.addEventListener('keydown', function(e) {
    if (e.key === 'Enter' && e.target.type !== 'password') {
        e.preventDefault();
    }
});

// 페이지 로드 시 첫 번째 폼 활성화
window.addEventListener('load', function() {
    const firstForm = document.querySelector('.signup-form');
    if (firstForm) {
        firstForm.classList.add('active');
    }
    
    // 전화번호 인증 기능 초기화
    initializePhoneVerification();
});

// 전화번호 인증 기능 초기화
function initializePhoneVerification() {
    // 일반 사용자 인증
    setupPhoneVerification('social');
    // 업체 인증
    setupPhoneVerification('company');
}

// 전화번호 인증 설정
function setupPhoneVerification(userType) {
    const phoneInput = document.getElementById(userType + '-phone');
    const verifyBtn = document.getElementById(userType + '-verify-btn');
    const verificationGroup = document.getElementById(userType + '-verification-group');
    const verificationCode = document.getElementById(userType + '-verification-code');
    const verifyCodeBtn = document.getElementById(userType + '-verify-code-btn');
    const timer = document.getElementById(userType + '-timer');
    const timeLeft = document.getElementById(userType + '-time-left');
    const phoneError = document.getElementById(userType + '-phone-error');
    const verificationError = document.getElementById(userType + '-verification-error');
    
    let verificationCodeValue = '';
    let timeInterval = null;
    let timeLeftSeconds = 180; // 3분
    
    // 인증번호 발송 버튼 클릭
    verifyBtn.addEventListener('click', function() {
        const phone = phoneInput.value.trim();
        
        if (!phone) {
            showError(phoneInput, '전화번호를 입력해주세요.');
            return;
        }
        
        if (!validatePhoneNumber(phone)) {
            showError(phoneInput, '올바른 전화번호 형식을 입력해주세요.');
            return;
        }
        
        // 인증번호 발송 요청
        sendVerificationCode(userType, phone);
    });
    
    // 인증 확인 버튼 클릭
    verifyCodeBtn.addEventListener('click', function() {
        const code = verificationCode.value.trim();
        
        if (!code) {
            showError(verificationCode, '인증번호를 입력해주세요.');
            return;
        }
        
        if (code.length !== 6) {
            showError(verificationCode, '인증번호는 6자리입니다.');
            return;
        }
        
        // 인증번호 확인
        verifyCode(userType, phoneInput.value.trim(), code);
    });
    
    // 인증번호 입력 시 실시간 검증
    verificationCode.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
        clearError(this);
    });
    
    // 인증번호 발송 함수
    function sendVerificationCode(userType, phone) {
        verifyBtn.disabled = true;
        verifyBtn.textContent = '발송 중...';
        
        // 실제로는 서버에 요청을 보내야 함
        // 여기서는 시뮬레이션
        setTimeout(() => {
            verificationCodeValue = Math.floor(100000 + Math.random() * 900000).toString();
            console.log(`${userType} 인증번호: ${verificationCodeValue}`); // 개발용
            
            verificationGroup.style.display = 'block';
            timer.style.display = 'block';
            startTimer();
            
            verifyBtn.textContent = '재발송';
            verifyBtn.disabled = false;
            
            showMessage('인증번호가 발송되었습니다.', 'success');
        }, 1000);
    }
    
    // 인증번호 확인 함수
    function verifyCode(userType, phone, code) {
        verifyCodeBtn.disabled = true;
        verifyCodeBtn.textContent = '확인 중...';
        
        // 실제로는 서버에 요청을 보내야 함
        // 여기서는 시뮬레이션
        setTimeout(() => {
            if (code === verificationCodeValue) {
                // 인증 성공
                verificationGroup.style.display = 'none';
                timer.style.display = 'none';
                clearInterval(timeInterval);
                
                phoneInput.disabled = true;
                verifyBtn.textContent = '인증완료';
                verifyBtn.disabled = true;
                verifyBtn.style.background = '#28a745';
                
                showMessage('전화번호 인증이 완료되었습니다.', 'success');
            } else {
                // 인증 실패
                showError(verificationCode, '인증번호가 올바르지 않습니다.');
                verifyCodeBtn.disabled = false;
                verifyCodeBtn.textContent = '인증 확인';
            }
        }, 1000);
    }
    
    // 타이머 시작
    function startTimer() {
        timeLeftSeconds = 180;
        updateTimer();
        
        timeInterval = setInterval(() => {
            timeLeftSeconds--;
            updateTimer();
            
            if (timeLeftSeconds <= 0) {
                clearInterval(timeInterval);
                timer.classList.add('danger');
                showError(verificationCode, '인증 시간이 만료되었습니다. 다시 발송해주세요.');
            }
        }, 1000);
    }
    
    // 타이머 업데이트
    function updateTimer() {
        const minutes = Math.floor(timeLeftSeconds / 60);
        const seconds = timeLeftSeconds % 60;
        timeLeft.textContent = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        
        if (timeLeftSeconds <= 30) {
            timer.classList.add('danger');
        } else if (timeLeftSeconds <= 60) {
            timer.classList.add('warning');
        }
    }
}

// 전화번호 유효성 검사
function validatePhoneNumber(phone) {
    const phoneRegex = /^010-\d{4}-\d{4}$/;
    return phoneRegex.test(phone);
}
