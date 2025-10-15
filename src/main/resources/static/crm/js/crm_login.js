document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('/crm/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        const result = await response.json();

        if (result.success) {
            showAlert('로그인 성공! 이동 중...', 'success');
            setTimeout(() => {
                window.location.href = result.redirectUrl || '/crm/company';
            }, 1000);
        } else {
            showAlert(result.message || '로그인에 실패했습니다.', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('로그인 처리 중 오류가 발생했습니다.', 'error');
    }
});

function showAlert(message, type) {
    const alertBox = document.getElementById('alertBox');
    const icon = type === 'success' ? '<i class="fas fa-check-circle"></i>' : '<i class="fas fa-exclamation-circle"></i>';
    alertBox.innerHTML = `<div class="alert alert-${type}">${icon} ${message}</div>`;
    
    setTimeout(() => {
        alertBox.innerHTML = '';
    }, 5000);
}

// URL 파라미터 확인
window.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('error')) {
        showAlert('이메일 또는 비밀번호가 올바르지 않습니다.', 'error');
    }
    if (urlParams.get('logout')) {
        showAlert('로그아웃되었습니다.', 'success');
    }
});
