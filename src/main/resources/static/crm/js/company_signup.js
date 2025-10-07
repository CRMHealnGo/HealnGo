// 회원가입 데이터 저장
let signupData = {
    phone: '',
    email: '',
    password: '',
    companyName: '',
    companyNumber: '',
    hospitalType: '',
    position: '',
    teamMembers: []
};

let currentStep = 1;
let timerInterval = null;

document.addEventListener('DOMContentLoaded', function() {
    initializeSignup();
});

function initializeSignup() {
    // 전화번호 입력 이벤트
    const phoneInput = document.getElementById('phoneNumber');
    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            formatPhoneNumber(this);
        });
    }

    // SMS 코드 입력 이벤트
    const smsCodeInput = document.getElementById('smsCode');
    if (smsCodeInput) {
        smsCodeInput.addEventListener('input', function() {
            if (this.value.length === 6) {
                verifySMSCode(this.value);
            }
        });
    }

    // 비밀번호 토글
    setupPasswordToggle();
    
    // 폼 검증
    setupFormValidation();
}

// 전화번호 포맷팅
function formatPhoneNumber(input) {
    let value = input.value.replace(/\D/g, '');
    if (value.length >= 3) {
        value = value.replace(/(\d{3})(\d{0,4})(\d{0,4})/, '$1 $2-$3');
    }
    input.value = value;
}

// SMS 전송
function sendSMS() {
    const phoneNumber = document.getElementById('phoneNumber').value;
    const sendButton = document.querySelector('.btn-send-sms');
    
    // 전화번호 유효성 검사
    if (!phoneNumber || phoneNumber.length < 10) {
        alert('올바른 전화번호를 입력해주세요.');
        return;
    }
    
    // 버튼 비활성화
    sendButton.disabled = true;
    sendButton.textContent = '전송중...';
    
    // 실제 구현에서는 서버 API 호출
    fetch('/api/sms/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            phoneNumber: phoneNumber
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // SMS 전송 성공
            document.getElementById('sentNumber').textContent = phoneNumber;
            document.getElementById('smsInfo').style.display = 'block';
            
            // 타이머 시작
            startTimer();
            
            // 버튼 상태 변경
            sendButton.textContent = '재전송';
            sendButton.disabled = false;
            
            alert('인증번호가 전송되었습니다.');
        } else {
            alert('SMS 전송 중 오류가 발생했습니다: ' + data.message);
            sendButton.disabled = false;
            sendButton.textContent = '인증하기';
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('SMS 전송 중 오류가 발생했습니다.');
        sendButton.disabled = false;
        sendButton.textContent = '인증하기';
    });
}

// SMS 코드 검증
function verifySMSCode(code) {
    // 실제 구현에서는 서버에서 검증
    if (code.length === 6) {
        // SMS 전송 시뮬레이션 (개발용)
        const phoneNumber = document.getElementById('phoneNumber').value;
        document.getElementById('sentNumber').textContent = phoneNumber;
        document.getElementById('smsInfo').style.display = 'block';
        
        // 타이머 시작
        startTimer();
    }
}

// SMS 타이머
function startTimer() {
    let timeLeft = 85; // 1분 25초
    
    timerInterval = setInterval(function() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        
        document.getElementById('timer').textContent = 
            String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
        
        timeLeft--;
        
        if (timeLeft < 0) {
            clearInterval(timerInterval);
            document.getElementById('smsInfo').style.display = 'none';
        }
    }, 1000);
}

// 비밀번호 토글 설정
function setupPasswordToggle() {
    const passwordToggle = document.querySelector('.password-toggle');
    const passwordInput = document.getElementById('password');
    
    if (passwordToggle && passwordInput) {
        passwordToggle.addEventListener('click', function() {
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                this.classList.remove('fa-eye');
                this.classList.add('fa-eye-slash');
            } else {
                passwordInput.type = 'password';
                this.classList.remove('fa-eye-slash');
                this.classList.add('fa-eye');
            }
        });
    }
}

// 폼 검증 설정
function setupFormValidation() {
    const forms = document.querySelectorAll('.signup-form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            e.preventDefault();
        });
    });
}

// 다음 단계로 이동
function nextStep() {
    if (validateCurrentStep()) {
        saveCurrentStepData();
        
        if (currentStep < 3) {
            currentStep++;
            showStep(currentStep);
            updateProgressSteps();
        } else {
            submitSignup();
        }
    }
}

// 이전 단계로 이동
function previousStep() {
    if (currentStep > 1) {
        currentStep--;
        showStep(currentStep);
        updateProgressSteps();
    }
}

// 현재 단계 표시
function showStep(step) {
    // 모든 단계 숨기기
    document.querySelectorAll('.step-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // 현재 단계 표시
    const currentStepContent = document.getElementById(`step${step}-content`);
    if (currentStepContent) {
        currentStepContent.classList.add('active');
    }
}

// 진행 단계 업데이트
function updateProgressSteps() {
    for (let i = 1; i <= 3; i++) {
        const stepCircle = document.querySelector(`#step${i} .step-circle`);
        const stepNumber = document.querySelector(`#step${i} .step-number`);
        
        if (i < currentStep) {
            // 완료된 단계
            stepCircle.classList.remove('active');
            stepCircle.classList.add('completed');
            stepNumber.style.display = 'none';
        } else if (i === currentStep) {
            // 현재 단계
            stepCircle.classList.remove('completed');
            stepCircle.classList.add('active');
            stepNumber.style.display = 'block';
        } else {
            // 아직 안 한 단계
            stepCircle.classList.remove('active', 'completed');
            stepNumber.style.display = 'block';
        }
    }
}

// 현재 단계 데이터 저장
function saveCurrentStepData() {
    if (currentStep === 1) {
        const countryCode = document.getElementById('countryCode').value;
        const phoneNumber = document.getElementById('phoneNumber').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        
        signupData.phone = countryCode + ' ' + phoneNumber;
        signupData.email = email;
        signupData.password = password;
    } else if (currentStep === 2) {
        signupData.companyName = document.getElementById('companyName').value;
        signupData.companyNumber = document.getElementById('companyNumber').value;
        signupData.hospitalType = document.getElementById('hospitalType').value;
        signupData.position = document.getElementById('position').value;
    }
}

// 현재 단계 검증
function validateCurrentStep() {
    if (currentStep === 1) {
        const phoneNumber = document.getElementById('phoneNumber').value;
        const smsCode = document.getElementById('smsCode').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        
        if (!phoneNumber || phoneNumber.length < 10) {
            alert('전화번호를 올바르게 입력해주세요.');
            return false;
        }
        
        if (!smsCode || smsCode.length !== 6) {
            alert('SMS 인증번호를 입력해주세요.');
            return false;
        }
        
        if (!email || !isValidEmail(email)) {
            alert('올바른 이메일 주소를 입력해주세요.');
            return false;
        }
        
        if (!password || password.length < 6) {
            alert('비밀번호는 6자 이상 입력해주세요.');
            return false;
        }
        
        return true;
        
    } else if (currentStep === 2) {
        const companyName = document.getElementById('companyName').value;
        const companyNumber = document.getElementById('companyNumber').value;
        const hospitalType = document.getElementById('hospitalType').value;
        const position = document.getElementById('position').value;
        
        if (!companyName) {
            alert('회사명을 입력해주세요.');
            return false;
        }
        
        if (!companyNumber) {
            alert('사업자번호를 입력해주세요.');
            return false;
        }
        
        if (!hospitalType) {
            alert('병원 종류를 선택해주세요.');
            return false;
        }
        
        if (!position) {
            alert('직급을 선택해주세요.');
            return false;
        }
        
        return true;
    }
    
    return true;
}

// 이메일 유효성 검사
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// 팀원 추가
function addMember() {
    const memberEmailInput = document.querySelector('.member-email');
    const email = memberEmailInput.value.trim();
    
    if (!email || !isValidEmail(email)) {
        alert('올바른 이메일 주소를 입력해주세요.');
        return;
    }
    
    // 중복 체크
    if (signupData.teamMembers.includes(email)) {
        alert('이미 추가된 이메일입니다.');
        return;
    }
    
    // 팀원 목록에 추가
    signupData.teamMembers.push(email);
    
    // UI에 추가
    addMemberToList(email);
    
    // 입력 필드 초기화
    memberEmailInput.value = '';
}

// 팀원 목록에 추가
function addMemberToList(email) {
    const membersList = document.getElementById('membersList');
    
    const memberItem = document.createElement('div');
    memberItem.className = 'member-item';
    memberItem.innerHTML = `
        <span class="member-email-text">${email}</span>
        <button type="button" class="btn-remove-member" onclick="removeMember('${email}')">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    membersList.appendChild(memberItem);
}

// 팀원 제거
function removeMember(email) {
    signupData.teamMembers = signupData.teamMembers.filter(member => member !== email);
    
    // UI에서 제거
    const membersList = document.getElementById('membersList');
    const memberItems = membersList.querySelectorAll('.member-item');
    
    memberItems.forEach(item => {
        if (item.querySelector('.member-email-text').textContent === email) {
            item.remove();
        }
    });
}

// 비밀번호 토글
function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleIcon = document.querySelector('.password-toggle');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleIcon.classList.remove('fa-eye');
        toggleIcon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        toggleIcon.classList.remove('fa-eye-slash');
        toggleIcon.classList.add('fa-eye');
    }
}

// 회원가입 제출
function submitSignup() {
    // 마지막 단계 데이터 저장
    saveCurrentStepData();
    
    console.log('회원가입 데이터:', signupData);
    
    // 실제 구현에서는 서버로 데이터 전송
    // fetch('/api/company-signup', {
    //     method: 'POST',
    //     headers: {
    //         'Content-Type': 'application/json',
    //     },
    //     body: JSON.stringify(signupData)
    // })
    // .then(response => response.json())
    // .then(data => {
    //     if (data.success) {
    //         showSuccessPage();
    //     } else {
    //         alert('회원가입 중 오류가 발생했습니다: ' + data.message);
    //     }
    // })
    // .catch(error => {
    //     console.error('Error:', error);
    //     alert('회원가입 중 오류가 발생했습니다.');
    // });
    
    // 시뮬레이션: 성공 페이지 표시
    setTimeout(() => {
        showSuccessPage();
    }, 1000);
}

// 성공 페이지 표시
function showSuccessPage() {
    // 모든 단계 숨기기
    document.querySelectorAll('.step-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // 성공 페이지 표시
    document.getElementById('success-content').style.display = 'block';
    
    // 사이드바 숨기기
    document.querySelector('.signup-sidebar').style.display = 'none';
    document.querySelector('.signup-main').style.width = '100%';
}

// 대시보드로 이동
function goToDashboard() {
    // 실제 구현에서는 대시보드 페이지로 리다이렉트
    window.location.href = '/company/dashboard';
}

// 페이지 로드 시 초기화
updateProgressSteps();
