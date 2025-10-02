// 마이페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 알림 팝업 관련 기능
    initializeNotificationPopup();
    
    // 액션 버튼 이벤트 리스너
    initializeActionButtons();
    
    // 예약내역 카드 클릭 이벤트
    initializeBookingItems();
});

// 알림 팝업 초기화
function initializeNotificationPopup() {
    // 알림 섹션 클릭 시 팝업 열기
    const notificationsSection = document.querySelector('.notifications-section');
    if (notificationsSection) {
        notificationsSection.addEventListener('click', function(e) {
            if (!e.target.closest('.view-all')) {
                openNotificationPopup();
            }
        });
    }
}

// 알림 팝업 열기
function openNotificationPopup() {
    const popup = document.getElementById('notificationPopup');
    if (popup) {
        popup.style.display = 'block';
        // 배경 클릭으로 닫기
        document.addEventListener('click', closeOnBackgroundClick);
    }
}

// 알림 팝업 닫기
function closeNotificationPopup() {
    const popup = document.getElementById('notificationPopup');
    if (popup) {
        popup.style.display = 'none';
        document.removeEventListener('click', closeOnBackgroundClick);
    }
}

// 배경 클릭 시 팝업 닫기
function closeOnBackgroundClick(e) {
    const popup = document.getElementById('notificationPopup');
    if (popup && !popup.contains(e.target)) {
        closeNotificationPopup();
    }
}

// 액션 버튼 초기화
function initializeActionButtons() {
    const actionButtons = document.querySelectorAll('.action-btn');
    
    actionButtons.forEach(button => {
        button.addEventListener('click', function() {
            const action = this.querySelector('span').textContent;
            handleActionClick(action);
        });
    });
}

// 액션 버튼 클릭 처리
function handleActionClick(action) {
    switch(action) {
        case '찜':
            showMessage('찜 목록 페이지로 이동합니다.');
            // 실제로는 찜 목록 페이지로 이동
            // window.location.href = '/favorites';
            break;
        case '채팅':
            showMessage('채팅 페이지로 이동합니다.');
            // 실제로는 채팅 페이지로 이동
            // window.location.href = '/chat';
            break;
        case '후기':
            showMessage('후기 페이지로 이동합니다.');
            // 실제로는 후기 페이지로 이동
            // window.location.href = '/reviews';
            break;
        case '문의&신고':
            showMessage('문의&신고 페이지로 이동합니다.');
            // 실제로는 문의&신고 페이지로 이동
            // window.location.href = '/inquiry';
            break;
    }
}

// 예약내역 아이템 초기화
function initializeBookingItems() {
    const bookingItems = document.querySelectorAll('.booking-item');
    
    bookingItems.forEach(item => {
        item.addEventListener('click', function() {
            const bookingNumber = this.querySelector('.booking-number').textContent;
            showBookingDetails(bookingNumber);
        });
    });
}

// 예약 상세 정보 표시
function showBookingDetails(bookingNumber) {
    showMessage(`예약번호 ${bookingNumber}의 상세 정보를 표시합니다.`);
    // 실제로는 예약 상세 페이지로 이동
    // window.location.href = `/booking/${bookingNumber}`;
}

// 메시지 표시 (임시)
function showMessage(message) {
    // 간단한 알림 표시
    const notification = document.createElement('div');
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #27ae60;
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        z-index: 3000;
        font-size: 14px;
        max-width: 300px;
    `;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    // 3초 후 자동 제거
    setTimeout(() => {
        if (notification.parentNode) {
            notification.parentNode.removeChild(notification);
        }
    }, 3000);
}

// 스크롤 애니메이션
function initializeScrollAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);
    
    // 애니메이션 대상 요소들
    const animatedElements = document.querySelectorAll('.user-profile, .notifications-section, .booking-section');
    animatedElements.forEach(el => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(20px)';
        el.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
        observer.observe(el);
    });
}

// 페이지 로드 시 스크롤 애니메이션 초기화
window.addEventListener('load', initializeScrollAnimations);

// 반응형 네비게이션 (모바일용)
function initializeMobileNavigation() {
    const header = document.querySelector('.main-header');
    const userActions = document.querySelector('.user-actions');
    
    if (window.innerWidth <= 768) {
        // 모바일에서 사용자 액션 버튼들을 햄버거 메뉴로 변경
        const mobileMenu = document.createElement('button');
        mobileMenu.innerHTML = '<i class="fas fa-bars"></i>';
        mobileMenu.className = 'mobile-menu-btn';
        mobileMenu.style.cssText = `
            background: none;
            border: none;
            color: white;
            font-size: 20px;
            cursor: pointer;
            padding: 10px;
        `;
        
        // 기존 사용자 액션을 숨기고 모바일 메뉴 추가
        if (userActions) {
            userActions.style.display = 'none';
            header.querySelector('.header-container').appendChild(mobileMenu);
        }
    }
}

// 윈도우 리사이즈 이벤트
window.addEventListener('resize', function() {
    initializeMobileNavigation();
});

// 초기 로드 시 모바일 네비게이션 초기화
initializeMobileNavigation();

// 알림 모달 열기
function openNotificationModal() {
    const modal = document.getElementById('notificationModal');
    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    }
}

// 알림 모달 닫기
function closeNotificationModal() {
    const modal = document.getElementById('notificationModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = ''; // 스크롤 복원
    }
}

// 모달 배경 클릭 시 닫기
document.addEventListener('click', function(e) {
    const modal = document.getElementById('notificationModal');
    if (modal && e.target === modal) {
        closeNotificationModal();
    }
});

// ESC 키로 모달 닫기
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeNotificationModal();
        closeProfileModal();
    }
});

// 정보 수정 모달 열기
function openProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden'; // 배경 스크롤 방지
    }
}

// 정보 수정 모달 닫기
function closeProfileModal() {
    const modal = document.getElementById('profileModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = ''; // 스크롤 복원
    }
}

// 프로필 저장
function saveProfile() {
    const modal = document.getElementById('profileModal');
    if (!modal) return;
    
    // 폼 데이터 수집
    const formData = {
        nickname: modal.querySelector('input[type="text"]').value,
        email: modal.querySelector('input[type="email"]').value,
        gender: modal.querySelector('select').value,
        currentPassword: modal.querySelector('input[placeholder="현재 비밀번호를 입력하세요"]').value,
        newPassword: modal.querySelector('input[placeholder="새 비밀번호를 입력하세요"]').value,
        confirmPassword: modal.querySelector('input[placeholder="새 비밀번호를 다시 입력하세요"]').value
    };
    
    // 유효성 검사
    if (!formData.nickname.trim()) {
        alert('닉네임을 입력해주세요.');
        return;
    }
    
    if (!formData.email.trim()) {
        alert('이메일을 입력해주세요.');
        return;
    }
    
    // 비밀번호 변경 시 유효성 검사
    if (formData.currentPassword || formData.newPassword || formData.confirmPassword) {
        if (!formData.currentPassword) {
            alert('현재 비밀번호를 입력해주세요.');
            return;
        }
        
        if (!formData.newPassword) {
            alert('새 비밀번호를 입력해주세요.');
            return;
        }
        
        if (formData.newPassword !== formData.confirmPassword) {
            alert('새 비밀번호가 일치하지 않습니다.');
            return;
        }
        
        if (formData.newPassword.length < 6) {
            alert('비밀번호는 6자 이상이어야 합니다.');
            return;
        }
    }
    
    // 저장 처리 (실제로는 서버에 전송)
    console.log('프로필 저장:', formData);
    
    // 성공 메시지
    alert('정보가 성공적으로 저장되었습니다.');
    
    // 메인 페이지 정보 업데이트
    updateMainProfile(formData);
    
    // 모달 닫기
    closeProfileModal();
}

// 메인 페이지 프로필 정보 업데이트
function updateMainProfile(formData) {
    // 사용자 이름 업데이트
    const userName = document.querySelector('.user-name');
    if (userName) {
        userName.textContent = `${formData.nickname} 님`;
    }
    
    // 사용자 ID 업데이트 (닉네임으로)
    const userId = document.querySelector('.user-id');
    if (userId) {
        userId.textContent = formData.nickname;
    }
}

// 모달 배경 클릭 시 닫기 (정보 수정 모달)
document.addEventListener('click', function(e) {
    const profileModal = document.getElementById('profileModal');
    if (profileModal && e.target === profileModal) {
        closeProfileModal();
    }
});

// 프로필 이미지 처리
function handleProfileImage(input) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            // 모달 내 프로필 이미지 업데이트
            const modalPreview = document.querySelector('.profile-image-preview');
            if (modalPreview) {
                modalPreview.innerHTML = `<img src="${e.target.result}" alt="프로필 이미지" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">`;
            }
            
            // 메인 페이지 프로필 이미지 업데이트
            const mainProfileImage = document.getElementById('mainProfileImage');
            if (mainProfileImage) {
                mainProfileImage.innerHTML = `<img src="${e.target.result}" alt="프로필 이미지" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">`;
            }
            
            console.log('프로필 이미지가 업데이트되었습니다.');
        };
        reader.readAsDataURL(file);
    }
}
