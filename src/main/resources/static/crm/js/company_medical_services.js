// ===================== 사이드바 활성 표시 =====================
document.addEventListener('DOMContentLoaded', async () => {
  const allNavLinks = document.querySelectorAll('.nav-link');
  allNavLinks.forEach(link => link.classList.remove('active'));
  const medicalServiceLink = document.querySelector('a[href="/company/medical-services"]');
  if (medicalServiceLink) medicalServiceLink.classList.add('active');

});

// ===================== CSRF/메타/유틸 =====================
function getMeta(name) {
  const el = document.querySelector(`meta[name="${name}"]`);
  return el ? el.content : null;
}
function getHeaders() {
  const headers = { "Content-Type": "application/json" };
  const csrfToken = getMeta("_csrf");
  const csrfHeader = getMeta("_csrf_header");
  if (csrfToken && csrfHeader) headers[csrfHeader] = csrfToken;
  return headers;
}
function getCompanyId() {
  return getMeta("companyId") || window.companyIdFromServer || null;
}

// ===================== API =====================
// (새 API) 아이템 + 서비스 LEFT JOIN 결과
async function fetchCompanyItemsWithServices() {
  const companyId = getCompanyId();
  if (!companyId) return [];
  const res = await fetch(`/company/medical-services/api/company/${companyId}/with-items`, {
    method: "GET"
  });
  if (!res.ok) throw new Error(`[${res.status}] 조회 실패`);
  return res.json();
}

// 기존: 서비스만 목록
async function fetchMediList() {
  const companyId = getCompanyId();
  if (!companyId) return [];
  const res = await fetch(`/company/medical-services/api/company/${companyId}`, { method: "GET" });
  if (!res.ok) throw new Error(`[${res.status}] 목록 조회 실패`);
  return res.json();
}

// ===================== 렌더 =====================
function renderList(rows) {
  const wrap = document.getElementById('listWrap');
  wrap.innerHTML = '';

  if (!rows || rows.length === 0) {
    wrap.innerHTML = `<div class="card"><p class="muted">등록된 지점/서비스가 없습니다.</p></div>`;
    return;
  }

  // itemId 기준으로 묶기
  const byItem = new Map();
  rows.forEach(r => {
    const key = r.itemId;
    if (!byItem.has(key)) {
      byItem.set(key, { itemId: r.itemId, itemName: r.itemName, services: [] });
    }
    if (r.serviceId) {
      byItem.get(key).services.push({
        serviceId: r.serviceId,
        serviceName: r.serviceName,
        price: r.price,
        currency: r.currency,
        updatedAt: r.updatedAt
      });
    }
  });

  byItem.forEach(group => {
    const card = document.createElement('div');
    card.className = 'card';

    const header = document.createElement('div');
    header.className = 'item-header';
    header.innerHTML = `
      <div>
        <strong>${escapeHtml(group.itemName || '(무명 지점)')}</strong>
        <span class="muted"> #${group.itemId}</span>
      </div>
      <div>
        <button class="btn-outline" onclick="openCreate(${group.itemId})">
          <i class="fa fa-plus"></i> 서비스 추가
        </button>
      </div>
    `;
    card.appendChild(header);

    const body = document.createElement('div');
    if (group.services.length === 0) {
      body.innerHTML = `<span class="muted">등록된 서비스가 없습니다.</span>`;
    } else {
      group.services.forEach(s => {
        const pill = document.createElement('div');
        pill.className = 'service-pill';
        const priceStr = (s.price != null ? Number(s.price).toLocaleString() : '-');
        pill.innerHTML = `
          <strong>${escapeHtml(s.serviceName || '(무명 서비스)')}</strong>
          <span class="muted"> · ${priceStr}${s.currency ? ' ' + s.currency : ''}</span>
        `;
        pill.onclick = () => openDetail(s.serviceId);
        body.appendChild(pill);
      });
    }
    card.appendChild(body);
    wrap.appendChild(card);
  });
}

function escapeHtml(str) {
  return String(str ?? '').replace(/[&<>"']/g, s => ({
    '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'
  }[s]));
}

// 예시 네비게이션 함수(원하는 페이지로 연결)
function openCreate(itemId) {
  // ex) /company/event-registration?itemId=...
  window.location.href = `/company/event-registration?itemId=${itemId}`;
}
function openDetail(serviceId) {
  // ex) 상세 페이지 라우팅
  window.location.href = `/company/medical-services/detail/${serviceId}`;
}
