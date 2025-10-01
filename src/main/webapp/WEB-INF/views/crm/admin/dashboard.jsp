<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HealnGo 관리자 모드</title>
    <link rel="stylesheet" href="<c:url value='/resources/crm/css/admin-dashboard.css'/>">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <!-- 상단 헤더 -->
    <header class="admin-header">
        <div class="header-left">
            <div class="logo">
                <i class="fas fa-heart"></i>
                <span class="logo-text">HealnGo</span>
                <span class="logo-subtitle">Care first, then go</span>
            </div>
        </div>
        <div class="header-center">
            <div class="search-box">
                <input type="text" placeholder="Search" class="search-input">
                <i class="fas fa-search search-icon"></i>
            </div>
        </div>
        <div class="header-right">
            <div class="notification">
                <i class="fas fa-bell"></i>
            </div>
            <div class="user-profile">
                <div class="user-avatar">
                    <i class="fas fa-user"></i>
                </div>
                <div class="user-info">
                    <span class="user-name">관리자 정민서</span>
                    <i class="fas fa-chevron-down"></i>
                </div>
            </div>
        </div>
    </header>

    <!-- 날짜 표시 -->
    <div class="date-range">
        <i class="fas fa-calendar"></i>
        <span>Sep 16, 2025 - Oct 16, 2025</span>
    </div>

    <div class="admin-container">
        <!-- 사이드바 -->
        <nav class="sidebar">
            <ul class="nav-menu">
                <li class="nav-item active">
                    <i class="fas fa-th-large"></i>
                    <span>Dashboard</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-users"></i>
                    <span>사용자 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-building"></i>
                    <span>업체 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-calendar-alt"></i>
                    <span>예약 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-chart-bar"></i>
                    <span>리포트 & 통계</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-bullhorn"></i>
                    <span>공지사항 & 알림 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-comments"></i>
                    <span>문의/신고 접수</span>
                </li>
            </ul>
            <div class="logout-section">
                <a href="#" class="logout-link">
                    <i class="fas fa-arrow-right"></i>
                    <span>Logout</span>
                </a>
            </div>
        </nav>

        <!-- 메인 콘텐츠 -->
        <main class="main-content">
            <div class="dashboard-header">
                <h1>HealnGo, 관리자 모드</h1>
                <h2>Dashboard</h2>
            </div>

            <!-- 사용자 관리 섹션 -->
            <section class="dashboard-section">
                <div class="section-header">
                    <h3>사용자 관리</h3>
                    <a href="#" class="view-all">View all ></a>
                </div>
                <div class="chart-container">
                    <canvas id="userChart" width="400" height="200"></canvas>
                </div>
            </section>

            <!-- 10월 캘린더 섹션 -->
            <section class="dashboard-section">
                <div class="section-header">
                    <h3>10월</h3>
                </div>
                <div class="calendar-container">
                    <div class="calendar">
                        <div class="calendar-header">
                            <div class="day-header">일</div>
                            <div class="day-header">월</div>
                            <div class="day-header">화</div>
                            <div class="day-header">수</div>
                            <div class="day-header">목</div>
                            <div class="day-header">금</div>
                            <div class="day-header">토</div>
                        </div>
                        <div class="calendar-grid">
                            <!-- 캘린더 그리드는 JavaScript로 동적 생성 -->
                        </div>
                    </div>
                    <div class="schedule-details">
                        <div class="schedule-item">
                            <div class="schedule-date">10/13</div>
                            <div class="schedule-content">
                                <div class="schedule-title">밝은미소 패키지 예약</div>
                                <div class="schedule-title">반짝업체 입점</div>
                                <div class="schedule-title">반짝업체 광고일(13~16)</div>
                                <div class="schedule-title">공지사항</div>
                                <div class="schedule-detail">-1번 공지 올라감.</div>
                                <div class="schedule-detail">-고객 신고 처리 공지 올라감</div>
                            </div>
                        </div>
                        <div class="schedule-item">
                            <div class="schedule-date">10/16</div>
                            <div class="schedule-content">
                                <div class="schedule-title">입점 업체 결제처리</div>
                                <div class="schedule-title">해맑업체 입점</div>
                                <div class="schedule-title">해맑업체 광고일(16~19)</div>
                                <div class="schedule-title">공지사항</div>
                                <div class="schedule-detail">-고객 신고 처리 공지 올라감</div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- 업체 관리 섹션 -->
            <section class="dashboard-section">
                <div class="section-header">
                    <h3>업체 관리</h3>
                    <a href="#" class="view-all">View all ></a>
                </div>
                <div class="company-list">
                    <div class="company-item">
                        <div class="company-id">PN0001265</div>
                        <div class="company-name">(주)말랑핑업체</div>
                        <div class="company-contract">계약일 Sep 12, 2021</div>
                        <div class="company-rating">
                            <span class="rating-arrow up">↑</span>
                            <span class="rating-score">4.8</span>
                            <span class="rating-count">(288)</span>
                        </div>
                        <div class="company-data">
                            <div class="data-item">
                                <span class="data-label">패키지:</span>
                                <span class="data-value">34</span>
                            </div>
                            <div class="data-item">
                                <span class="data-label">플래너:</span>
                                <span class="data-value">13</span>
                            </div>
                            <div class="data-item">
                                <span class="data-label">예약건:</span>
                                <span class="data-value">+2</span>
                                <div class="user-icons">
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="company-item">
                        <div class="company-id">PN0001221</div>
                        <div class="company-name">허나허나업체</div>
                        <div class="company-contract">계약일 Sep 10, 2020</div>
                        <div class="company-rating">
                            <span class="rating-arrow up">↑</span>
                            <span class="rating-score">4.7</span>
                            <span class="rating-count">(312)</span>
                        </div>
                        <div class="company-data">
                            <div class="data-item">
                                <span class="data-label">패키지:</span>
                                <span class="data-value">50</span>
                            </div>
                            <div class="data-item">
                                <span class="data-label">플래너:</span>
                                <span class="data-value">24</span>
                            </div>
                            <div class="data-item">
                                <span class="data-label">예약건:</span>
                                <span class="data-value">+1</span>
                                <div class="user-icons">
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="company-item">
                        <div class="company-id">PN0001290</div>
                        <div class="company-name">요요업체</div>
                        <div class="company-contract">계약일 May 28, 2024</div>
                        <div class="company-rating">
                            <span class="rating-arrow down">↓</span>
                            <span class="rating-score">4.2</span>
                            <span class="rating-count">(162)</span>
                        </div>
                        <div class="company-data">
                            <div class="data-item">
                                <span class="data-label">패키지:</span>
                                <span class="data-value">23</span>
                            </div>
                            <div class="data-item">
                                <span class="data-label">플래너:</span>
                                <span class="data-value">20</span>
                            </div>
                            <div class="data-item">
                                <span class="data-label">예약건:</span>
                                <span class="data-value">+5</span>
                                <div class="user-icons">
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                    <i class="fas fa-user"></i>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- 인기 패키지 섹션 -->
            <section class="dashboard-section">
                <div class="section-header">
                    <h3>인기 패키지</h3>
                    <a href="#" class="view-all">View all ></a>
                </div>
                <div class="package-list">
                    <div class="package-item">
                        <div class="package-name">화이트닝 패키지</div>
                        <div class="package-company">하얀만두 업체</div>
                        <div class="package-count up">↑ 553 +</div>
                    </div>
                    <div class="package-item">
                        <div class="package-name">다이어트 패키지</div>
                        <div class="package-company">노란리본 업체</div>
                        <div class="package-count down">↓ 260 +</div>
                    </div>
                    <div class="package-item">
                        <div class="package-name">여름 패키지</div>
                        <div class="package-company">핫핫 업체</div>
                        <div class="package-count down">↓ 200 +</div>
                    </div>
                </div>
            </section>
        </main>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="<c:url value='/resources/crm/js/admin-dashboard.js'/>"></script>
</body>
</html>
