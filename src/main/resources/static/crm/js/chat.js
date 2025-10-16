// 채팅 페이지 JavaScript

document.addEventListener('DOMContentLoaded', function() {

    // 대화 목록 로드
    initializeConversations

    // 메시지 입력 이벤트
    setupMessageInput();
    
    // 대화 선택 이벤트
    setupConversationSelection();
    
    // 상세 정보 토글
    setupDetailsToggle();
});

// ===== 공통 =====
const CSRF_HEADER = document.querySelector('meta[name="_csrf_header"]')?.content;
const CSRF_TOKEN  = document.querySelector('meta[name="_csrf"]')?.content;

// 서버에서 템플릿으로 주입해둔 전역(예: 세션)
// window.CURRENT_USER_ID, window.SENDER_ROLE('SOCIAL' | 'COMPANY')를 사용한다고 가정
let currentThreadId = null;

// 날짜
function fmt(dt) {
    if (!dt) return '';
    const d = new Date(dt);
    return d.toLocaleString();
}

// 대화 목록 렌더링
asyn function initializeConversations {
    try {
        const res = await fetch(`/api/chat/threads/user/${window.CURRENT_USER_ID}?page1&size=30`);
        if (!res.ok) throw new Error('thread list fail');
        const page = await res.json();
        const threads = page.content || [];
        renderConversations(threads);

        // 첫 스레드 자동 선택
        if (threads.length > 0) selectConversation(threads[0].threadId);
    } catch (e) {
        const.error('스레드 로드 실패: ', e);
    }
}

// 대화 아이템 생성
function renderConversations(threads) {
    const container = document.querySelector('.conversations-section');
    if (!container) return;

    const html = threads.map(t => {
        const name = t.tile || `대화 #${t.threadId}`;
        const time = fmt(t.lastMsgAt);
        const avatar = (name || 'U').charAt(0);

    return `
        <div class="conversation-item" data-conversation-id="${t.threadId}">
            <div class="conversation-avatar">
                ${avatar}
            </div>
            <div class="conversation-info">
                <div class="conversation-name">${name}</div>
                <div class="conversation-last-message">${time}</div>
            </div>
            <div class="conversation-time">${time}</div>
        </div>
    `;
    }).join('');

    container.innerHTML = html;
}

// 대화 선택 이벤트
function setupConversationSelection() {
    document.addEventListener('click', function(e) {
        const item = e.target.closest('.conversation-item');
        if (!item) return;

        const item = e.target.closest('.conversation-item').forEach(el => el.classList.remove('active'));
        item.classList.add('active');

        const threadId = Number(item.dataset.conversationId);
        selectConversation(threadId);
    });
}

// 대화 선택 > 메시지 로드
function selectConversation(threadId) {
    currentThreadId = Number(threadId);
    // 채팅 내용 로드
    loadChatMessages(currentThreadId);
}

// 메시지 렌더링
asyn function loadChatMessages(threadId) {
    try {
        const res = await fetch(`/api/chat/messages/${threadId}?page=1&size=50`);
        if (!res.ok) throw new Error('messages fail');
        const page = await res.json();

        // 서버는 보통 최신순(내림차순)
        // UI는 과거 > 현재 순으로 보여줌 reverse()
        const list = (page.content || []).slice().reverse();

        // DTO > UI모델 매핑
        const uiMessages = list.map(m => {

            const isMine = (m.senderRole === window.SENDER_ROLE) && (
                (window.SENDER_ROLE === 'SOCIAL' && m.senderUserId === window.CURRENT_USER_ID) ||
                (window.SENDER_ROLE === 'COMPANY' && m.senderCompanyId === window.CURRENT_USER_ID)
            );

            return {
                type: isMine ? 'sent' : 'received',
                sender: isMine ? null : (m.senderRole || 'USER'),
                content: m.body || (m.attachmentMime ? `[첨부파일: ${m.attachmentMime}]` : ''),
                time: fmt(m.createdAt),
                avatar: isMine ? 'Me' : (m.senderRole ? m.senderRole.charAt(0) : 'U')
            };
        });

        renderMessages(uiMessages);
    }   catch (e) {
        console.error('메시지 로드 실패: ', e);
    }
}

// 메시지 요소 생성
function renderMessages(message) {
    const messageContainer = document.querySelectory('.chat-messages');

    if(!messagesContainer) return;

    const html = messages.map(createMessageElement).join('');
    messagesContainer.innerHTML = html;

    // 맨 아래로 스크롤
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 메시지 DOM 생성
function createMessageElement(message){

    const messageClass = message.type === 'sent' ? 'sent' : 'received';
    const avatar = message.avatar || message.sender?.charAt(0) || 'U';

    return `
        <div class="message ${messageClass}">
            <div class="message-avatar">${avatar}</div>
            <div class="message-content">
                ${escapeHtml(message.content)}
                <div class="message-time">${message.time}</div>
            </div>
        </div>
    `;
}

// XSS 최소 방어
function escapeHtml(str = '') {
    return str.replace(/[&<>"']/g, s => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[s]));
}

// 메시지를 입력 전송
function setupMessageInput() {
  const messageInput = document.querySelector('.message-input');
  const sendBtn = document.querySelector('.send-btn');

  if (!messageInput || !sendBtn) return;

  // 엔터(Shift+Enter는 줄바꿈)
  messageInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });

  // 버튼 클릭
  sendBtn.addEventListener('click', sendMessage);

  // 자동 높이
  messageInput.addEventListener('input', function () {
    this.style.height = 'auto';
    this.style.height = this.scrollHeight + 'px';
  });
}

async function sendMessage() {
  const input = document.querySelector('.message-input');
  const content = input?.value?.trim();
  if (!content || !currentThreadId) return;

  // 낙관적 UI 추가
  addMessageToChat({ type: 'sent', content, time: getCurrentTime(), avatar: 'Me' });

  // 서버 전송
  const fd = new FormData();
  fd.append('threadId', currentThreadId);
  fd.append('senderRole', window.SENDER_ROLE); // 'SOCIAL' (고객화면)
  if (window.SENDER_ROLE === 'SOCIAL')  fd.append('senderUserId', window.CURRENT_USER_ID);
  if (window.SENDER_ROLE === 'COMPANY') fd.append('senderCompanyId', window.CURRENT_USER_ID);
  fd.append('body', content);
  // 파일 있으면: fd.append('file', fileInput.files[0]);

  try {
    const headers = {};
    if (CSRF_HEADER && CSRF_TOKEN) headers[CSRF_HEADER] = CSRF_TOKEN;

    const res = await fetch('/api/chat/message', { method: 'POST', body: fd, headers });
    if (!res.ok) throw new Error('send fail');

    // 서버 시간/정합 반영을 위해 재조회(또는 응답 DTO로 보강)
    await loadChatMessages(currentThreadId);
  } catch (e) {
    console.error('전송 실패:', e);
  } finally {
    input.value = '';
    input.style.height = 'auto';
  }
}

// 메시지 추가(낙관적 UI)
function addMessageToChat(message) {
  const messagesContainer = document.querySelector('.chat-messages');
  if (!messagesContainer) return;

  const html = createMessageElement(message);
  messagesContainer.insertAdjacentHTML('beforeend', html);
  messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 현재 시간(클라이언트 표시용)
function getCurrentTime() {
  const now = new Date();
  const h = now.getHours();
  const m = now.getMinutes().toString().padStart(2, '0');
  const ampm = h >= 12 ? 'PM' : 'AM';
  const displayH = h % 12 || 12;
  return `${displayH}:${m} ${ampm}`;
}

// 상세 정보 토글 (필요 시 유지)
function setupDetailsToggle() {
  document.addEventListener('click', function (e) {
    if (!e.target.classList.contains('detail-section-title')) return;
    const section = e.target.closest('.detail-section');
    const content = section?.querySelector('.detail-section-content');
    if (content) content.style.display = content.style.display === 'none' ? 'block' : 'none';
  });
}

// 첨부/링크/멘션/이모지 (추후 구현)
function attachFile(){ alert('파일 첨부 기능은 준비 중입니다.'); }
function shareLink(){
  const link = prompt('공유할 링크를 입력하세요:');
  if (!link) return;
  addMessageToChat({ type: 'sent', content: link, time: getCurrentTime(), avatar: 'Me' });
}
function mentionUser(){ alert('멘션 기능은 준비 중입니다.'); }
function insertEmoji(){ alert('이모티콘 기능은 준비 중입니다.'); }