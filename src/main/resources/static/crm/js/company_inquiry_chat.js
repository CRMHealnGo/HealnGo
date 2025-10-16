// ===== 상태 =====
  let currentThreadId = null;
  let threads = []; // 서버에서 받은 스레드 캐시
  let currentFilter = 'all'; // all, new, in-progress, completed

  // ===== 유틸 =====
  function fmtTime(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleString('ko-KR', { hour: '2-digit', minute: '2-digit', hour12: true });
  }
  function firstLetter(text) {
    return (text?.trim()?.[0] || '-');
  }

  // ===== 스레드 목록 =====
  async function loadThreads() {
    const companyId = window.CURRENT_COMPANY_ID;
    if (!companyId) return;

    const res = await fetch(`/api/chat/threads?companyId=${companyId}&page=1&size=50`, { credentials: 'same-origin' });
    if (!res.ok) { console.error('thread load failed'); return; }
    const page = await res.json(); // Spring Data Page<ChatThreadDto>
    threads = page.content || [];
    filterThreads(); // 필터 적용하여 렌더링
  }

  function renderThreadList(list) {
    const el = document.getElementById('threadList');
    if (!el) return;
    if (!list.length) {
      el.innerHTML = `<div style="padding:16px; color:#9aa1ad;">대화가 없습니다.</div>`;
      return;
    }
    el.innerHTML = list.map(t => {
      const statusClass = (t.status || 'new').toLowerCase().replace('_', '-');
      const statusLabel = getStatusLabel(t.status);
      return `
        <div class="thread-item" data-thread-id="${t.threadId}" data-status="${statusClass}">
          <div class="thread-avatar">${firstLetter(t.title || '대화')}</div>
          <div class="thread-main">
            <div class="thread-title">${t.title || '대화'}</div>
            <div class="thread-preview">${t.lastMessagePreview || ''}</div>
            <div class="inquiry-status">
              <span class="inquiry-status-badge ${statusClass}">${statusLabel}</span>
              ${t.unreadCount ? `<span class="inquiry-unread-count">${t.unreadCount > 99 ? '99+' : t.unreadCount}</span>` : ''}
            </div>
          </div>
          <div class="thread-right">
            <div class="thread-time">${t.lastMsgAt ? fmtTime(t.lastMsgAt) : ''}</div>
          </div>
        </div>
      `;
    }).join('');
  }

  function getStatusLabel(status) {
    const statusMap = {
      'NEW': '신규',
      'IN_PROGRESS': '진행중',
      'COMPLETED': '완료'
    };
    return statusMap[status] || '신규';
  }

  function filterThreads() {
    const filtered = currentFilter === 'all' 
      ? threads 
      : threads.filter(t => {
          const status = (t.status || 'NEW').toLowerCase().replace('_', '-');
          return status === currentFilter;
        });
    renderThreadList(filtered);
  }

  // 스레드 클릭 핸들러
  document.addEventListener('click', (e) => {
    const item = e.target.closest('.thread-item');
    if (!item) return;
    document.querySelectorAll('.thread-item').forEach(n => n.classList.remove('active'));
    item.classList.add('active');
    const threadId = Number(item.dataset.threadId);
    const t = threads.find(x => x.threadId === threadId);
    onSelectThread(t);
  });

  function onSelectThread(thread) {
    currentThreadId = thread.threadId;
    // 헤더 표시
    document.getElementById('peerAvatar').textContent = firstLetter(thread.title);
    document.getElementById('peerTitle').textContent = thread.title || `스레드 #${thread.threadId}`;
    document.getElementById('peerSub').textContent = `itemId: ${thread.itemId ?? '-'}`;

    // 입력/버튼 활성화
    document.getElementById('btnSend').disabled = false;
    document.getElementById('inputMessage').disabled = false;
    document.getElementById('btnMarkDone').disabled = false;
    document.getElementById('btnReserve').disabled = false;

    // 메시지 가져오기
    loadMessages(thread.threadId);
  }

  // ===== 메시지 =====
  async function loadMessages(threadId) {
    const box = document.getElementById('messageList');
    box.innerHTML = '';
    const res = await fetch(`/api/chat/messages/${threadId}?page=1&size=50`, { credentials: 'same-origin' });
    if (!res.ok) { box.innerHTML = `<div class="inquiry-empty">메시지를 불러오지 못했습니다.</div>`; return; }
    const page = await res.json();          // Page<ChatMessageDto>
    const list = page.content || [];

    // 서버가 최신순(Desc)으로 준다면 UI는 시간순(Asc)로 보여주기 위해 뒤집기
    list.reverse();
    renderMessages(list);
  }

  function renderMessages(list) {
    const box = document.getElementById('messageList');
    if (!list.length) { box.innerHTML = `<div class="inquiry-empty">메시지가 없습니다. 첫 메시지를 보내보세요.</div>`; return; }

    box.innerHTML = '';
    let lastDate = '';
    list.forEach(m => {
      const d = new Date(m.createdAt);
      const dayStr = d.toLocaleDateString('ko-KR');
      if (dayStr !== lastDate) {
        lastDate = dayStr;
        box.insertAdjacentHTML('beforeend', `<div class="date-sep">${dayStr}</div>`);
      }
      const mine = (m.senderRole === 'COMPANY'); // 업체 화면: 업체 메시지는 오른쪽
      const who = mine ? '회사' : '고객';
      box.insertAdjacentHTML('beforeend', `
        <div class="msg ${mine ? 'company' : ''}">
          <div class="pp">${mine ? '업' : '고'}</div>
          <div>
            <div class="bubble">${escapeHtml(m.body || '')}</div>
            <div class="time">${fmtTime(m.createdAt)}</div>
          </div>
        </div>
      `);
    });
    box.scrollTop = box.scrollHeight;
  }

  // ===== 전송 =====
  async function sendMessage() {
    const input = document.getElementById('inputMessage');
    const text = (input.value || '').trim();
    if (!text || !currentThreadId) return;

    const payload = {
      threadId: currentThreadId,
      senderRole: 'COMPANY',                // 업체 화면 고정
      senderUserId: null,                   // 회사가 보냄 -> null
      senderCompanyId: window.CURRENT_COMPANY_ID,
      body: text
    };

    const headers = { 'Content-Type':'application/json' };
    if (window.CSRF_HEADER && window.CSRF_TOKEN) headers[window.CSRF_HEADER] = window.CSRF_TOKEN;

    const res = await fetch('/api/chat/send', {
      method: 'POST',
      headers,
      credentials: 'same-origin',
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      alert('메시지 전송 실패');
      return;
    }
    // 낙관적 UI 반영 or 서버 반환 사용
    input.value = '';
    input.style.height = 'auto';

    // 바로 새로고침(간단)
    await loadMessages(currentThreadId);
    // 스레드 최신시간 갱신 위해 목록 재조회(선택)
    // await loadThreads();
  }

  // 상담 완료 처리
  async function markAsDone() {
    if (!currentThreadId) return;
    if (!confirm('이 상담을 완료 처리하시겠습니까?')) return;

    const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    if (window.CSRF_HEADER && window.CSRF_TOKEN) headers[window.CSRF_HEADER] = window.CSRF_TOKEN;

    const res = await fetch(`/api/chat/thread/${currentThreadId}/status?status=COMPLETED`, {
      method: 'POST',
      headers,
      credentials: 'same-origin'
    });

    if (res.ok) {
      alert('상담이 완료되었습니다.');
      // 스레드 목록 새로고침
      await loadThreads();
    } else {
      alert('상담 완료 처리에 실패했습니다.');
    }
  }

  // 버튼/입력 이벤트
  document.addEventListener('DOMContentLoaded', () => {
    // 초기에 스레드 로드
    loadThreads();

    const input = document.getElementById('inputMessage');
    const btn = document.getElementById('btnSend');
    const btnMarkDone = document.getElementById('btnMarkDone');
    
    input.addEventListener('keypress', (e) => {
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
      }
    });
    input.addEventListener('input', function() {
      this.style.height = 'auto';
      this.style.height = Math.min(this.scrollHeight, 140) + 'px';
    });
    btn.addEventListener('click', sendMessage);
    btnMarkDone.addEventListener('click', markAsDone);

    // 필터 탭 클릭 이벤트
    document.querySelectorAll('.inquiry-filter-tab').forEach(tab => {
      tab.addEventListener('click', (e) => {
        // 활성 탭 변경
        document.querySelectorAll('.inquiry-filter-tab').forEach(t => t.classList.remove('active'));
        e.target.classList.add('active');
        
        // 필터 적용
        currentFilter = e.target.dataset.filter;
        filterThreads();
      });
    });
  });

  // XSS 간단 이스케이프
  function escapeHtml(s) {
    return s.replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m]));
  }