(function() {
    // DOM 요소들
    const kakaoLoginBtn = document.getElementById('kakao-login-btn');
    const googleLoginBtn = document.getElementById('google-login-btn');

    // 이벤트 리스너 등록
    function initEventListeners() {
        if (kakaoLoginBtn) {
            kakaoLoginBtn.addEventListener('click', function(e) {
                // 서버에서 전달받은 URL을 사용하므로 href 속성으로 직접 이동
                // preventDefault()를 제거하여 기본 동작(링크 이동)을 허용
            });
        }

        if (googleLoginBtn) {
            googleLoginBtn.addEventListener('click', function(e) {
                // 서버에서 전달받은 URL을 사용하므로 href 속성으로 직접 이동
                // preventDefault()를 제거하여 기본 동작(링크 이동)을 허용
            });
        }
    }

    // 초기화
    function init() {
        initEventListeners();
    }

    // DOM 로드 완료 후 초기화
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
