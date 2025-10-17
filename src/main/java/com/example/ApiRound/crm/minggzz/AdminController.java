package com.example.ApiRound.crm.minggzz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ApiRound.Service.UserInquiryService;
import com.example.ApiRound.crm.hyeonah.Repository.CompanyUserRepository;
import com.example.ApiRound.crm.hyeonah.Repository.SocialUsersRepository;
import com.example.ApiRound.crm.hyeonah.entity.CompanyUser;
import com.example.ApiRound.crm.hyeonah.entity.SocialUsers;
import com.example.ApiRound.crm.hyeonah.notice.Notice;
import com.example.ApiRound.crm.hyeonah.notice.NoticeService;
import com.example.ApiRound.crm.yoyo.reservation.ReservationRepository;
import com.example.ApiRound.entity.UserInquiry;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SocialUsersRepository usersRepo;
    private final CompanyUserRepository companyRepo;
    private final ReservationRepository reservationRepo;
    private final NoticeService noticeService;
    private final UserInquiryService userInquiryService;

    /**
     * 관리자 대시보드 메인 페이지
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // 세션 체크: 관리자로 로그인한 사용자만 접근 가능
        Object managerIdObj = session.getAttribute("managerId");
        Long managerId = null;
        if (managerIdObj instanceof Integer) {
            managerId = ((Integer) managerIdObj).longValue();
        } else if (managerIdObj instanceof Long) {
            managerId = (Long) managerIdObj;
        }
        String userType = (String) session.getAttribute("userType");

        if (managerId == null || !"manager".equals(userType)) {
            return "redirect:/crm/crm_login";
        }

        // 관리자 정보 추가
        model.addAttribute("managerId", managerId);
        model.addAttribute("managerName", session.getAttribute("managerName"));
        model.addAttribute("managerEmail", session.getAttribute("managerEmail"));

        // 실제 DB 통계 데이터
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", usersRepo.countByIsDeletedFalse());
        stats.put("activeUsers", usersRepo.countByStatusAndIsDeletedFalse("ACTIVE"));
        stats.put("suspendedUsers", usersRepo.countByStatusAndIsDeletedFalse("SUSPENDED"));
        stats.put("totalCompanies", companyRepo.count());
        stats.put("totalReservations", 0); // TODO: 예약 테이블 연동
        stats.put("totalRevenue", 0); // TODO: 결제 테이블 연동

        // 월별 가입자 데이터 (1월~12월) - 실제 DB 데이터 사용
        List<Map<String, Object>> monthlyData = getMonthlyUserData();
        stats.put("monthlyData", monthlyData);

        stats.put("totalUsers", usersRepo.countByIsDeletedFalse());
        stats.put("activeUsers", usersRepo.countByStatusAndIsDeletedFalse("ACTIVE"));
        stats.put("suspendedUsers", usersRepo.countByStatusAndIsDeletedFalse("SUSPENDED"));

        // 업체 관련 통계 - 실제 DB 데이터 사용
        stats.put("totalCompanies", companyRepo.countByApprovalStatus("APPROVED")); // 승인된 업체 수
        stats.put("newThisMonth", companyRepo.countNewCompaniesThisMonth(
            java.time.LocalDate.now().getYear(), 
            java.time.LocalDate.now().getMonthValue()
        )); // 이번 달 신규 업체 수
        stats.put("reportsReceived", companyRepo.countByApprovalStatus("REPORTED")); // 신고 접수된 업체 수
        stats.put("underSanction", companyRepo.countByApprovalStatusAndIsActive("SUSPENDED", true)); // 제재 중인 업체 수
        
        stats.put("totalReservations", 0); // TODO: 예약 테이블 연동
        stats.put("totalRevenue", 0); // TODO: 결제 테이블 연동

        model.addAttribute("stats", stats);

        // 최근 활동 데이터
        List<Map<String, Object>> recentActivities = getRecentActivities();
        model.addAttribute("recentActivities", recentActivities);

        // 차트 데이터
        Map<String, Object> chartData = getChartData();
        model.addAttribute("chartData", chartData);
        
        // 예약 많은 순으로 업체 리스트 (상위 5개)
        List<Map<String, Object>> topCompanies = getTopCompaniesByReservations();
        model.addAttribute("topCompanies", topCompanies);

        // 최근 문의/신고 데이터 (최대 3건)
        List<UserInquiry> recentInquiries = userInquiryService.getAdminPagedList(1, 3, null).getContent();
        model.addAttribute("recentInquiries", recentInquiries);

        return "admin/admin";
    }

    /**
     * 사용자 관리 페이지
     * DB연동, 검색, 정렬, 페이지네이션
     */
    @GetMapping("/users")
    public String users(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String statusFilter,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "desc") String dir,
            Model model, HttpSession session) {

        model.addAttribute("managerName", session.getAttribute("managerName"));

        // 페이지(0based), 사이즈(안전 범위) 보정
        int pageIdx = Math.max(page, 1) - 1;
        int pageSize = Math.min(Math.max(size, 1), 100);

        // UI 정렬 키 > 엔티티 필드 매핑
        String sortProp = switch (sort) {
            case "name" -> "name";
            case "email" -> "email";
            case "joinDate" -> "createdAt";
            default -> "createdAt";
        };

        // Sort 생성
        Sort sortOrder = "asc".equalsIgnoreCase(dir)
                ? Sort.by(sortProp).ascending()
                : Sort.by(sortProp).descending();

        Pageable pageable = PageRequest.of(pageIdx, pageSize, sortOrder);

        // DB에서 사용자 조회 (검색 + 상태 필터)
        Page<SocialUsers> userPage;

        if (search != null && !search.trim().isEmpty() && statusFilter != null && !statusFilter.isEmpty()) {
            // 검색 + 상태 필터
            userPage = usersRepo.searchActiveByStatus(search.trim(), statusFilter, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            // 검색만
            userPage = usersRepo.searchActive(search.trim(), pageable);
        } else if (statusFilter != null && !statusFilter.isEmpty()) {
            // 상태 필터만
            userPage = usersRepo.findByStatusAndIsDeletedFalse(statusFilter, pageable);
        } else {
            // 전체 조회
            userPage = usersRepo.findByIsDeletedFalse(pageable);
        }

        // SocialUsers를 Map으로 변환 (HTML에서 사용하기 쉽게)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> users = userPage.getContent().stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", user.getUserId());
                    map.put("name", user.getName() != null ? user.getName() : "-");
                    map.put("email", user.getEmail());
                    map.put("phone", user.getPhone());
                    map.put("joinDate", user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "-");
                    map.put("status", getStatusLabel(user.getStatus(), user.getIsDeleted()));
                    return map;
                })
                .toList();

        model.addAttribute("users", users);

        // 페이지네이션 정보
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalUsers", userPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("sortBy", sort);
        model.addAttribute("sortDir", dir);

        return "admin/users";
    }

    /**
     * 예약 관리 페이지
     */
    @GetMapping("/reservations")
    public String reservations(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            Model model, HttpSession session) {

        model.addAttribute("managerName", session.getAttribute("managerName"));

        // 예약 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> reservations = getReservations(page, size, search);
        model.addAttribute("reservations", reservations);

        // 페이지네이션 정보
        int totalReservations = 320; // 실제로는 DB에서 조회
        int totalPages = (int) Math.ceil((double) totalReservations / size);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("search", search);

        return "admin/reservations";
    }

    /**
     * 리포트 & 통계 페이지
     */
    @GetMapping("/report")
    public String report(Model model, HttpSession session) {
        model.addAttribute("managerName", session.getAttribute("managerName"));

        // 통계 데이터 (실제로는 서비스에서 가져와야 함)
        Map<String, Object> reportStats = new HashMap<>();
        reportStats.put("totalRevenue", 0);
        reportStats.put("totalReservations", 0);
        reportStats.put("totalUsers", 0);
        model.addAttribute("reportStats", reportStats);

        return "admin/report";
    }

    /**
     * 문의/신고 접수 페이지 (관리자용)
     */
    @GetMapping("/inquiry-report")
    public String inquiryReport(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            Model model, HttpSession session) {

        model.addAttribute("managerName", session.getAttribute("managerName"));

        // 문의/신고 목록 데이터 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> reports = getInquiryReports();
        model.addAttribute("reports", reports);

        // 페이지네이션 정보
        int totalReports = reports.size(); // 실제로는 DB에서 조회
        int totalPages = (int) Math.ceil((double) totalReports / size);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalReports", totalReports);
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("sidebarType", "admin");

        return "admin/inquiry_report";
    }

    /**
     * 문의/신고 상세 페이지 (관리자용)
     */
    @GetMapping("/inquiry-report/detail/{id}")
    public String inquiryReportDetail(@PathVariable("id") Integer id, Model model, HttpSession session) {
        model.addAttribute("reportId", id);
        model.addAttribute("sidebarType", "admin");
        model.addAttribute("managerName", session.getAttribute("managerName"));

        return "crm/inquiry_detail";
    }

    /**
     * 문의/신고 상세 조회 API (관리자용)
     */
    @GetMapping("/api/inquiry-reports/{id}")
    @ResponseBody
    public ResponseEntity<UserInquiry> getInquiryDetail(@PathVariable Integer id) {
        try {
            List<UserInquiry> inquiries = userInquiryService.getAdminPagedList(1, 1000, null).getContent();

            UserInquiry inquiry = inquiries.stream()
                    .filter(i -> i.getInquiryId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("문의/신고를 찾을 수 없습니다."));

            return ResponseEntity.ok(inquiry);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 문의/신고 목록 조회 API (관리자용)
     */
    @GetMapping("/api/inquiry-reports")
    @ResponseBody
    public ResponseEntity<List<UserInquiry>> getAllInquiryReports(
            @RequestParam(value = "status", required = false) String status) {
        try {
            List<UserInquiry> inquiries = userInquiryService.getAdminPagedList(1, 100, status).getContent();
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 문의/신고 상태 변경 API (관리자용)
     */
    @PostMapping("/api/inquiry-reports/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateInquiryStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String newStatus = request.get("status");
            Object managerIdObj = session.getAttribute("managerId");
            Integer adminId = null;
            if (managerIdObj instanceof Integer) {
                adminId = (Integer) managerIdObj;
            } else if (managerIdObj instanceof Long) {
                adminId = ((Long) managerIdObj).intValue();
            }

            userInquiryService.updateStatus(id, newStatus, adminId);

            response.put("success", true);
            response.put("message", "상태가 변경되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 문의/신고 답변 작성 API (관리자용)
     */
    @PostMapping("/api/inquiry-reports/{id}/reply")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> replyInquiry(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String replyText = request.get("reply");

            if (replyText == null || replyText.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "답변 내용을 입력해주세요.");
                return ResponseEntity.badRequest().body(response);
            }

            Object managerIdObj = session.getAttribute("managerId");
            Integer adminId = null;
            if (managerIdObj instanceof Integer) {
                adminId = (Integer) managerIdObj;
            } else if (managerIdObj instanceof Long) {
                adminId = ((Long) managerIdObj).intValue();
            }

            userInquiryService.answer(id, replyText, adminId);

            response.put("success", true);
            response.put("message", "답변이 전송되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 임시 데이터 생성 메서드들 (실제로는 서비스에서 구현)
    private List<Map<String, Object>> getRecentActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();

        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("type", "user_registration");
        activity1.put("message", "새 사용자가 가입했습니다");
        activity1.put("time", "2분 전");
        activity1.put("icon", "fas fa-user-plus");
        activities.add(activity1);

        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("type", "reservation");
        activity2.put("message", "새로운 예약이 생성되었습니다");
        activity2.put("time", "5분 전");
        activity2.put("icon", "fas fa-calendar-plus");
        activities.add(activity2);

        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("type", "company_approval");
        activity3.put("message", "업체 승인이 완료되었습니다");
        activity3.put("time", "10분 전");
        activity3.put("icon", "fas fa-building");
        activities.add(activity3);

        return activities;
    }

    private Map<String, Object> getChartData() {
        Map<String, Object> chartData = new HashMap<>();

        // 월별 사용자 증가 데이터
        List<Integer> userGrowth = List.of(120, 150, 180, 200, 220, 250);
        chartData.put("userGrowth", userGrowth);

        // 월별 예약 데이터
        List<Integer> reservationData = List.of(45, 60, 75, 90, 85, 95);
        chartData.put("reservations", reservationData);

        return chartData;
    }

    // 상태 레이블 헬퍼 메서드
    private String getStatusLabel(String status, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return "비활성화";
        }
        if ("SUSPENDED".equals(status)) {
            return "정지";
        }
        return "활성";
    }

    private List<Map<String, Object>> getReservations(int page, int size, String search) {
        List<Map<String, Object>> reservations = new ArrayList<>();

        // 임시 예약 데이터
        String[] services = {"진료", "미용", "마사지", "치과진료", "한의진료"};

        for (int i = 1; i <= size; i++) {
            Map<String, Object> reservation = new HashMap<>();
            reservation.put("id", (page - 1) * size + i);
            reservation.put("userName", "고객" + i);
            reservation.put("companyName", "업체" + i);
            reservation.put("service", services[i % services.length]);
            reservation.put("date", "2024-01-" + String.format("%02d", i % 28 + 1));
            reservation.put("time", String.format("%02d:00", 9 + (i % 8)));
            reservation.put("status", i % 3 == 0 ? "완료" : i % 3 == 1 ? "예약" : "취소");
            reservation.put("amount", 50000 + (i * 10000));
            reservations.add(reservation);
        }

        return reservations;
    }

    private List<Map<String, Object>> getInquiryReports() {
        List<Map<String, Object>> reports = new ArrayList<>();

        try {
            // DB에서 모든 문의/신고 조회 (최신순)
            List<UserInquiry> inquiries = userInquiryService.getAdminPagedList(1, 100, null).getContent();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (UserInquiry inquiry : inquiries) {
                Map<String, Object> report = new HashMap<>();
                report.put("id", inquiry.getInquiryId());

                // subject에서 [문의:xxx] 또는 [신고:xxx] 패턴 제거
                String cleanSubject = inquiry.getSubject() != null ?
                    inquiry.getSubject().replaceAll("^\\[(?:문의|신고):[^\\]]+\\]\\s*", "") : "제목 없음";

                // subject에 [문의] 또는 [신고]가 포함되어 있으면 type 결정
                String type = "inquiry";
                if (inquiry.getSubject() != null && inquiry.getSubject().contains("[신고:")) {
                    type = "report";
                }
                report.put("type", type);
                report.put("title", cleanSubject);
                report.put("reporterName", inquiry.getReporterType() == UserInquiry.ReporterType.COMPANY ? "업체" : "사용자");
                report.put("reporterPhone", "-");
                report.put("reporterEmail", "-");
                report.put("companyName", "-");
                report.put("description", inquiry.getContent() != null ? inquiry.getContent() : "");

                // 상태 매핑
                String status = "pending";
                if (inquiry.getStatus() == UserInquiry.Status.ANSWERED) {
                    status = "resolved";
                } else if (inquiry.getStatus() == UserInquiry.Status.CLOSED) {
                    status = "rejected";
                }
                report.put("status", status);
                report.put("priority", "medium");
                report.put("createdDate", inquiry.getCreatedAt() != null ?
                    inquiry.getCreatedAt().format(formatter) : "-");

                reports.add(report);
            }
        } catch (Exception e) {
            System.err.println("문의/신고 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }

        return reports;
    }

    private Map<String, Object> getInquiryReportById(Long id) {
        try {
            // DB에서 특정 문의/신고 조회
            List<UserInquiry> inquiries = userInquiryService.getAdminPagedList(1, 1000, null).getContent();

            UserInquiry inquiry = inquiries.stream()
                    .filter(i -> i.getInquiryId().equals(id.intValue()))
                    .findFirst()
                    .orElse(null);

            if (inquiry == null) {
                return new HashMap<>();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            Map<String, Object> report = new HashMap<>();
            report.put("id", inquiry.getInquiryId());

            String cleanSubject = inquiry.getSubject() != null ?
                inquiry.getSubject().replaceAll("^\\[(?:문의|신고):[^\\]]+\\]\\s*", "") : "제목 없음";

            String type = "inquiry";
            if (inquiry.getSubject() != null && inquiry.getSubject().contains("[신고:")) {
                type = "report";
            }
            report.put("type", type);
            report.put("title", cleanSubject);
            report.put("reporterName", inquiry.getReporterType() == UserInquiry.ReporterType.COMPANY ? "업체" : "사용자");
            report.put("reporterPhone", "-");
            report.put("reporterEmail", "-");
            report.put("companyName", "-");
            report.put("description", inquiry.getContent() != null ? inquiry.getContent() : "");

            String status = "pending";
            if (inquiry.getStatus() == UserInquiry.Status.ANSWERED) {
                status = "resolved";
            } else if (inquiry.getStatus() == UserInquiry.Status.CLOSED) {
                status = "rejected";
            }
            report.put("status", status);
            report.put("priority", "medium");
            report.put("createdDate", inquiry.getCreatedAt() != null ?
                inquiry.getCreatedAt().format(formatter) : "-");

            return report;
        } catch (Exception e) {
            System.err.println("문의/신고 상세 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * 공지사항 & 알림 관리 페이지
     */
    @GetMapping("/notice-notify")
    public String noticeNotify(
            @RequestParam(defaultValue = "1") int page,
            Model model,
            HttpSession session) {

        // 다른 admin 페이지와 동일한 세션 처리
        model.addAttribute("managerName", session.getAttribute("managerName"));

        // 실제 공지사항 목록 조회 (페이지네이션)
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Notice> noticePage = noticeService.getAllNotices(pageable);

        int totalPages = Math.max(noticePage.getTotalPages(), 1);
        int startPage = Math.max(1, page - 2);
        int endPage = Math.min(totalPages, page + 2);

        model.addAttribute("notices", noticePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("totalCount", noticePage.getTotalElements());
        model.addAttribute("now", LocalDateTime.now()); // 현재 시각 추가

        // 알림 목록 (더미 데이터 - 나중에 구현)
        List<Map<String, Object>> notifications = getNotifications();
        model.addAttribute("notifications", notifications);

        // 알림 통계
        Map<String, Object> notifyStats = new HashMap<>();
        notifyStats.put("totalSent", 1234);
        notifyStats.put("delivered", 1180);
        notifyStats.put("pending", 54);
        notifyStats.put("failed", 12);
        model.addAttribute("notifyStats", notifyStats);

        return "admin/admin_notice_notify";
    }

    /**
     * 공지사항 작성 API
     */
    @PostMapping("/notice-notify/create")
    @ResponseBody
    public String createNotice(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "ALL") String audience,
            @RequestParam(defaultValue = "false") Boolean topFixed,
            HttpSession session) {

        // 관리자 ID 가져오기
        Object managerIdObj = session.getAttribute("managerId");
        Integer managerId = null;
        if (managerIdObj instanceof Integer) {
            managerId = (Integer) managerIdObj;
        } else if (managerIdObj instanceof Long) {
            managerId = ((Long) managerIdObj).intValue();
        }

        if (managerId == null) {
            return "login_required";
        }

        try {
            Notice notice = new Notice();
            notice.setTitle(title);
            notice.setBody(content);
            notice.setAudience(Notice.Audience.valueOf(audience));
            notice.setIsPinned(topFixed);
            notice.setCreatedBy(managerId);
            notice.setPublishAt(LocalDateTime.now()); // 즉시 게시

            noticeService.createNotice(notice);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    /**
     * 공지사항 수정 API
     */
    @PostMapping("/notice-notify/update")
    @ResponseBody
    public String updateNotice(
            @RequestParam Integer noticeId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "ALL") String audience,
            @RequestParam(defaultValue = "false") Boolean topFixed,
            HttpSession session) {

        Map<String, Object> notice1 = new HashMap<>();
        notice1.put("id", 1);
        notice1.put("title", "시스템 점검 안내");
        notice1.put("content", "2024년 10월 15일 새벽 2시~4시 시스템 점검이 예정되어 있습니다.");
        notice1.put("type", "important");
        notice1.put("status", "active");
        notice1.put("author", "관리자");
        notice1.put("date", "2024-10-09 14:30");
        notice1.put("views", 1234);
        notices.add(notice1);

        Map<String, Object> notice2 = new HashMap<>();
        notice2.put("id", 2);
        notice2.put("title", "신규 서비스 출시 안내");
        notice2.put("content", "새로운 예약 시스템이 출시되었습니다. 더욱 편리한 예약 관리를 경험해보세요.");
        notice2.put("type", "general");
        notice2.put("status", "active");
        notice2.put("author", "관리자");
        notice2.put("date", "2024-10-08 10:00");
        notice2.put("views", 856);
        notices.add(notice2);

        return notices;
    }

    /**
     * 공지사항 삭제 API
     */
    @PostMapping("/notice-notify/delete")
    @ResponseBody
    public String deleteNotice(
            @RequestParam Integer noticeId,
            HttpSession session) {

        // 관리자 권한 확인 (간단히)
        if (session.getAttribute("managerId") == null) {
            return "login_required";
        }

        try {
            noticeService.deleteNotice(noticeId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }


    private List<Map<String, Object>> getNotifications() {
        List<Map<String, Object>> notifications = new ArrayList<>();

        Map<String, Object> notify1 = new HashMap<>();
        notify1.put("id", 1);
        notify1.put("title", "시스템 점검 안내");
        notify1.put("message", "금일 새벽 2시~4시 시스템 점검이 예정되어 있습니다.");
        notify1.put("type", "all");
        notify1.put("status", "sent");
        notify1.put("date", "2024-10-09 14:30");
        notify1.put("recipients", 5234);
        notifications.add(notify1);

        return notifications;
    }

    // ===== REST API 엔드포인트 =====

    /**
     * 사용자 상세 조회
     */
    @GetMapping("/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Integer userId) {
        return usersRepo.findById(userId)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getUserId());
                    response.put("name", user.getName() != null ? user.getName() : "-");
                    response.put("email", user.getEmail());
                    response.put("phone", user.getPhone() != null ? user.getPhone() : "");
                    response.put("joinDate", user.getCreatedAt() != null ?
                            user.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "-");
                    response.put("lastLogin", user.getLastLoginAt() != null ?
                            user.getLastLoginAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "정보 없음");
                    response.put("status", getStatusLabel(user.getStatus(), user.getIsDeleted()));
                    response.put("totalReservations", 0); // TODO: 예약 테이블과 연동
                    response.put("totalSpent", 0); // TODO: 결제 테이블과 연동
                    response.put("notes", ""); // TODO: 관리자 메모 기능

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Integer userId,
            @RequestBody Map<String, String> updateData) {

        return usersRepo.findByUserIdAndIsDeletedFalse(userId)
                .map(user -> {
                    // 수정 가능한 필드 업데이트
                    if (updateData.containsKey("name")) {
                        user.setName(updateData.get("name"));
                    }
                    if (updateData.containsKey("phone")) {
                        user.setPhone(updateData.get("phone"));
                    }
                    if (updateData.containsKey("status")) {
                        String status = updateData.get("status");
                        user.setIsDeleted(!"active".equals(status));
                    }

                    usersRepo.save(user);

                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "사용자 정보가 수정되었습니다.");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "사용자를 찾을 수 없습니다.");
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 사용자 상태 토글 (활성 → 정지 → 비활성화)
     */
    @PostMapping("/api/users/{userId}/toggle-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleUserStatus(@PathVariable Integer userId) {
        return usersRepo.findById(userId)
                .map(user -> {
                    String currentStatus = user.getStatus() != null ? user.getStatus() : "ACTIVE";
                    boolean isDeleted = Boolean.TRUE.equals(user.getIsDeleted());

                    String newStatusLabel;

                    // 상태 토글: 활성 → 정지 → 비활성화 → 활성 (순환)
                    if (isDeleted) {
                        // 비활성화 → 활성
                        user.setIsDeleted(false);
                        user.setStatus("ACTIVE");
                        newStatusLabel = "활성";
                    } else if ("SUSPENDED".equals(currentStatus)) {
                        // 정지 → 비활성화
                        user.setIsDeleted(true);
                        user.setStatus("INACTIVE");
                        newStatusLabel = "비활성화";
                    } else {
                        // 활성 → 정지
                        user.setStatus("SUSPENDED");
                        newStatusLabel = "정지";
                    }

                    usersRepo.save(user);

                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "사용자 상태가 " + newStatusLabel + "(으)로 변경되었습니다.");
                    response.put("newStatus", newStatusLabel);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "사용자를 찾을 수 없습니다.");
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 일괄 정지 처리
     */
    @PostMapping("/api/users/bulk-suspend")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> bulkSuspend(@RequestBody Map<String, List<Integer>> request) {
        List<Integer> userIds = request.get("userIds");

        if (userIds == null || userIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "선택된 사용자가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        int count = 0;
        for (Integer userId : userIds) {
            usersRepo.findById(userId).ifPresent(user -> {
                user.setStatus("SUSPENDED");
                user.setIsDeleted(false);
                usersRepo.save(user);
            });
            count++;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", count + "명의 사용자가 정지되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 월별 가입자 데이터 조회
    private List<Map<String, Object>> getMonthlyUserData() {
        List<Map<String, Object>> monthlyData = new ArrayList<>();

        try {
            for (int month = 1; month <= 12; month++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                monthData.put("totalUsers", usersRepo.countByMonthAndIsDeletedFalse(month));
                monthData.put("activeUsers", usersRepo.countByMonthAndStatusAndIsDeletedFalse(month, "ACTIVE"));
                monthData.put("suspendedUsers", usersRepo.countByMonthAndStatusAndIsDeletedFalse(month, "SUSPENDED"));
                monthlyData.add(monthData);
            }
        } catch (Exception e) {
            // 쿼리 실패 시 더미 데이터로 대체
            System.err.println("월별 데이터 조회 실패, 더미 데이터 사용: " + e.getMessage());
            for (int month = 1; month <= 12; month++) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", month);
                monthData.put("totalUsers", 0);
                monthData.put("activeUsers", 0);
                monthData.put("suspendedUsers", 0);
                monthlyData.add(monthData);
            }
        }

        return monthlyData;
    }
    
    // 예약 많은 순으로 업체 리스트 조회
    private List<Map<String, Object>> getTopCompaniesByReservations() {
        List<Map<String, Object>> topCompanies = new ArrayList<>();
        
        try {
            // 승인된 업체 목록 조회 (상위 10개)
            Pageable pageable = PageRequest.of(0, 10);
            List<CompanyUser> companies = companyRepo.findTop5ApprovedCompanies(pageable);
            
            // 각 업체별 예약 수를 계산하고 정렬
            List<Map<String, Object>> companyWithReservations = new ArrayList<>();
            
            for (CompanyUser company : companies) {
                Long reservationCount = reservationRepo.countByCompany(company);
                
                Map<String, Object> companyData = new HashMap<>();
                companyData.put("companyId", company.getCompanyId());
                companyData.put("companyName", company.getCompanyName());
                companyData.put("category", company.getCategory());
                companyData.put("reservationCount", reservationCount);
                companyData.put("createdAt", company.getCreatedAt());
                
                companyWithReservations.add(companyData);
            }
            
            // 예약 수로 정렬 (내림차순)
            companyWithReservations.sort((a, b) -> {
                Long countA = (Long) a.get("reservationCount");
                Long countB = (Long) b.get("reservationCount");
                return countB.compareTo(countA);
            });
            
            // 상위 5개만 추출
            topCompanies = companyWithReservations.subList(0, Math.min(5, companyWithReservations.size()));
            
        } catch (Exception e) {
            System.err.println("업체 리스트 조회 실패: " + e.getMessage());
        }
        
        return topCompanies;
    }
}


