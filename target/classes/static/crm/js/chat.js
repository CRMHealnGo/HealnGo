// 채팅 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // 대화 목록 초기화
    initializeConversations();
    
    // 메시지 입력 이벤트
    setupMessageInput();
    
    // 대화 선택 이벤트
    setupConversationSelection();
    
    // 상세 정보 토글
    setupDetailsToggle();
});

// 대화 목록 초기화
function initializeConversations() {
    const conversations = [
        {
            id: 1,
            name: 'Medical App Team',
            lastMessage: '새로운 업데이트가 준비되었습니다.',
            time: '12:04',
            unread: 12,
            type: 'group',
            pinned: true
        },
        {
            id: 2,
            name: 'Food Delivery Service',
            lastMessage: '주문이 확인되었습니다.',
            time: '11:30',
            unread: 1,
            type: 'group',
            pinned: true
        },
        {
            id: 3,
            name: 'Garrett Watson',
            lastMessage: '감사합니다!',
            time: '12:04',
            unread: 0,
            type: 'individual'
        },
        {
            id: 4,
            name: 'Caroline Santos',
            lastMessage: '내일 뵙겠습니다.',
            time: '11:45',
            unread: 0,
            type: 'individual'
        },
        {
            id: 5,
            name: 'Leon Nunez',
            lastMessage: '좋은 하루 되세요.',
            time: '10:20',
            unread: 0,
            type: 'individual'
        }
    ];
    
    renderConversations(conversations);
}

// 대화 목록 렌더링
function renderConversations(conversations) {
    const container = document.querySelector('.conversations-section');
    if (!container) return;
    
    // 고정된 대화
    const pinnedConversations = conversations.filter(conv => conv.pinned);
    const regularConversations = conversations.filter(conv => !conv.pinned);
    
    let html = '';
    
    if (pinnedConversations.length > 0) {
        html += '<div class="section-title">고정</div>';
        pinnedConversations.forEach(conv => {
            html += createConversationItem(conv);
        });
    }
    
    if (regularConversations.length > 0) {
        html += '<div class="section-title">Messages</div>';
        regularConversations.forEach(conv => {
            html += createConversationItem(conv);
        });
    }
    
    container.innerHTML = html;
}

// 대화 아이템 생성
function createConversationItem(conversation) {
    const unreadBadge = conversation.unread > 0 ? 
        `<span class="unread-badge">${conversation.unread}</span>` : '';
    
    return `
        <div class="conversation-item" data-conversation-id="${conversation.id}">
            <div class="conversation-avatar">
                ${conversation.name.charAt(0)}
            </div>
            <div class="conversation-info">
                <div class="conversation-name">${conversation.name}</div>
                <div class="conversation-last-message">${conversation.lastMessage}</div>
            </div>
            <div class="conversation-time">${conversation.time}</div>
            ${unreadBadge}
        </div>
    `;
}

// 대화 선택 이벤트
function setupConversationSelection() {
    document.addEventListener('click', function(e) {
        const conversationItem = e.target.closest('.conversation-item');
        if (conversationItem) {
            const conversationId = conversationItem.dataset.conversationId;
            selectConversation(conversationId);
        }
    });
}

// 대화 선택
function selectConversation(conversationId) {
    // 모든 대화 아이템에서 active 클래스 제거
    document.querySelectorAll('.conversation-item').forEach(item => {
        item.classList.remove('active');
    });
    
    // 선택된 대화 아이템에 active 클래스 추가
    const selectedItem = document.querySelector(`[data-conversation-id="${conversationId}"]`);
    if (selectedItem) {
        selectedItem.classList.add('active');
    }
    
    // 채팅 내용 로드
    loadChatMessages(conversationId);
}

// 채팅 메시지 로드
function loadChatMessages(conversationId) {
    const messages = getSampleMessages(conversationId);
    renderMessages(messages);
}

// 샘플 메시지 데이터
function getSampleMessages(conversationId) {
    const sampleMessages = {
        1: [
            {
                type: 'date',
                content: 'Friday, September 8'
            },
            {
                type: 'received',
                sender: 'Olive Dixon',
                content: 'UX Login + Registration',
                time: '12:04 AM',
                avatar: 'O'
            },
            {
                type: 'sent',
                content: 'Thank you for the update!',
                time: '12:15 AM'
            }
        ],
        2: [
            {
                type: 'date',
                content: 'Thursday, September 7'
            },
            {
                type: 'received',
                sender: 'Food Service',
                content: 'Your order has been confirmed.',
                time: '11:30 AM',
                avatar: 'F'
            }
        ],
        3: [
            {
                type: 'date',
                content: 'Friday, September 8'
            },
            {
                type: 'received',
                sender: 'Garrett Watson',
                content: 'Thank you for your help!',
                time: '12:04 AM',
                avatar: 'G'
            },
            {
                type: 'sent',
                content: 'You\'re welcome!',
                time: '12:05 AM'
            }
        ]
    };
    
    return sampleMessages[conversationId] || [];
}

// 메시지 렌더링
function renderMessages(messages) {
    const messagesContainer = document.querySelector('.chat-messages');
    if (!messagesContainer) return;
    
    let html = '';
    
    messages.forEach(message => {
        if (message.type === 'date') {
            html += `<div class="message-date">${message.content}</div>`;
        } else {
            html += createMessageElement(message);
        }
    });
    
    messagesContainer.innerHTML = html;
    
    // 스크롤을 맨 아래로
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 메시지 요소 생성
function createMessageElement(message) {
    const messageClass = message.type === 'sent' ? 'sent' : 'received';
    const avatar = message.avatar || message.sender?.charAt(0) || 'U';
    
    return `
        <div class="message ${messageClass}">
            <div class="message-avatar">${avatar}</div>
            <div class="message-content">
                ${message.content}
                <div class="message-time">${message.time}</div>
            </div>
        </div>
    `;
}

// 메시지 입력 설정
function setupMessageInput() {
    const messageInput = document.querySelector('.message-input');
    const sendBtn = document.querySelector('.send-btn');
    
    if (messageInput && sendBtn) {
        // 엔터키로 메시지 전송
        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                sendMessage();
            }
        });
        
        // 전송 버튼 클릭
        sendBtn.addEventListener('click', sendMessage);
        
        // 입력창 자동 높이 조절
        messageInput.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    }
}

// 메시지 전송
function sendMessage() {
    const messageInput = document.querySelector('.message-input');
    const content = messageInput.value.trim();
    
    if (content) {
        const message = {
            type: 'sent',
            content: content,
            time: getCurrentTime()
        };
        
        // 메시지 추가
        addMessageToChat(message);
        
        // 입력창 초기화
        messageInput.value = '';
        messageInput.style.height = 'auto';
        
        // 자동 응답 (시뮬레이션)
        setTimeout(() => {
            const autoReply = {
                type: 'received',
                sender: 'Bot',
                content: '메시지를 받았습니다. 곧 답변드리겠습니다.',
                time: getCurrentTime(),
                avatar: 'B'
            };
            addMessageToChat(autoReply);
        }, 1000);
    }
}

// 메시지를 채팅에 추가
function addMessageToChat(message) {
    const messagesContainer = document.querySelector('.chat-messages');
    if (!messagesContainer) return;
    
    const messageElement = createMessageElement(message);
    messagesContainer.insertAdjacentHTML('beforeend', messageElement);
    
    // 스크롤을 맨 아래로
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 현재 시간 가져오기
function getCurrentTime() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const displayHours = hours % 12 || 12;
    const displayMinutes = minutes.toString().padStart(2, '0');
    
    return `${displayHours}:${displayMinutes} ${ampm}`;
}

// 상세 정보 토글
function setupDetailsToggle() {
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('detail-section-title')) {
            const section = e.target.closest('.detail-section');
            const content = section.querySelector('.detail-section-content');
            
            if (content) {
                content.style.display = content.style.display === 'none' ? 'block' : 'none';
            }
        }
    });
}

// 첨부 파일 버튼
function attachFile() {
    alert('파일 첨부 기능은 준비 중입니다.');
}

// 링크 버튼
function shareLink() {
    const link = prompt('공유할 링크를 입력하세요:');
    if (link) {
        const message = {
            type: 'sent',
            content: link,
            time: getCurrentTime()
        };
        addMessageToChat(message);
    }
}

// 멘션 버튼
function mentionUser() {
    alert('멘션 기능은 준비 중입니다.');
}

// 이모티콘 버튼
function insertEmoji() {
    alert('이모티콘 기능은 준비 중입니다.');
}
