// 비밀번호 찾기 JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('forgot-password-form');
    const emailInput = document.getElementById('email');
    const submitBtn = form.querySelector('.submit-btn');

    // 폼 제출 처리
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (validateEmail()) {
            submitForm();
        }
    });

    // 실시간 유효성 검사
    emailInput.addEventListener('blur', validateEmail);
    emailInput.addEventListener('input', clearError);

    // 이메일 유효성 검사
    function validateEmail() {
        const email = emailInput.value.trim();
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        
        if (!email) {
            showError(emailInput, '이메일을 입력해주세요.');
            return false;
        }
        
        if (!emailRegex.test(email)) {
            showError(emailInput, '올바른 이메일 형식을 입력해주세요.');
            return false;
        }
        
        clearError(emailInput);
        return true;
    }

    // 폼 제출
    function submitForm() {
        const email = emailInput.value.trim();
        
        // 로딩 상태 표시
        const originalText = submitBtn.textContent;
        submitBtn.classList.add('loading');
        submitBtn.textContent = '처리 중...';
        submitBtn.disabled = true;

        // 비밀번호 재설정 요청
        fetch('/forgot-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
                email: email
            })
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('서버 오류가 발생했습니다.');
        })
        .then(data => {
            if (data.success) {
                showMessage('비밀번호 재설정 링크가 이메일로 발송되었습니다. 이메일을 확인해주세요.', 'success');
                // 3초 후 로그인 페이지로 이동
                setTimeout(() => {
                    window.location.href = '/login';
                }, 3000);
            } else {
                showMessage(data.message || '이메일 발송에 실패했습니다.', 'error');
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
        form.insertBefore(messageDiv, form.firstChild);
        
        // 5초 후 자동 제거
        setTimeout(() => {
            if (messageDiv.parentNode) {
                messageDiv.remove();
            }
        }, 5000);
    }
});
