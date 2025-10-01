<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HealnGo 업체 모드</title>
    <link rel="stylesheet" href="<c:url value='/resources/crm/css/company-dashboard.css'/>">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body>
    <div class="company-container">
        <!-- 사이드바 -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <div class="logo">
                    <div class="logo-icon">
                        <i class="fas fa-heart"></i>
                    </div>
                    <div class="logo-text">
                        <h1>HealnGo</h1>
                        <p>Care first, then go</p>
                    </div>
                </div>
            </div>
            
            <ul class="nav-menu">
                <li class="nav-item active">
                    <i class="fas fa-th-large"></i>
                    <span>Dashboard</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-file-medical"></i>
                    <span>의료 서비스 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-calendar-alt"></i>
                    <span>예약 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-star"></i>
                    <span>후기 관리</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-comments"></i>
                    <span>문의 & 채팅</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-bullhorn"></i>
                    <span>마케팅</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-chart-bar"></i>
                    <span>리포트 & 통계</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-users"></i>
                    <span>업체 정보 수정</span>
                </li>
                <li class="nav-item">
                    <i class="fas fa-question-circle"></i>
                    <span>도움말/고객센터</span>
                </li>
            </ul>
            
            <div class="logout-section">
                <a href="#" class="logout-link">
                    <i class="fas fa-sign-out-alt"></i>
                    <span>Logout</span>
                </a>
            </div>
        </nav>

        <!-- 메인 콘텐츠 -->
        <main class="main-content">
            <div class="content-header">
                <h1>업체 모드</h1>
                <p>업체 관리 대시보드에 오신 것을 환영합니다.</p>
            </div>

            <!-- 통계 카드 -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${companyStats.totalReservations}</h3>
                        <p>총 예약</p>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-day"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${companyStats.todayReservations}</h3>
                        <p>오늘 예약</p>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${companyStats.pendingReservations}</h3>
                        <p>대기 예약</p>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon">
                        <i class="fas fa-star"></i>
                    </div>
                    <div class="stat-content">
                        <h3>${companyStats.averageRating}</h3>
                        <p>평균 평점</p>
                    </div>
                </div>
            </div>

            <!-- 예약 현황 -->
            <div class="content-section">
                <div class="section-header">
                    <h2>최근 예약 현황</h2>
                    <a href="/company/reservations" class="view-all">전체 보기 ></a>
                </div>
                <div class="reservation-list">
                    <c:forEach var="reservation" items="${reservations}">
                        <div class="reservation-item">
                            <div class="reservation-info">
                                <h4>${reservation.customerName}</h4>
                                <p>${reservation.service}</p>
                                <span class="reservation-date">${reservation.date} ${reservation.time}</span>
                            </div>
                            <div class="reservation-status status-${reservation.status}">
                                ${reservation.status}
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- 최근 후기 -->
            <div class="content-section">
                <div class="section-header">
                    <h2>최근 후기</h2>
                    <a href="/company/reviews" class="view-all">전체 보기 ></a>
                </div>
                <div class="review-list">
                    <c:forEach var="review" items="${reviews}">
                        <div class="review-item">
                            <div class="review-header">
                                <h4>${review.customerName}</h4>
                                <div class="rating">
                                    <c:forEach begin="1" end="5" var="i">
                                        <i class="fas fa-star ${i <= review.rating ? 'active' : ''}"></i>
                                    </c:forEach>
                                </div>
                            </div>
                            <p class="review-content">${review.content}</p>
                            <span class="review-date">${review.date}</span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </main>
    </div>

    <script src="<c:url value='/resources/crm/js/company-dashboard.js'/>"></script>
</body>
</html>
