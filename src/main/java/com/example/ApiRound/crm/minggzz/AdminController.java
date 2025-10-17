package com.example.ApiRound.crm.minggzz;

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

import com.example.ApiRound.crm.hyeonah.Repository.CompanyUserRepository;
import com.example.ApiRound.crm.hyeonah.Repository.SocialUsersRepository;
import com.example.ApiRound.crm.hyeonah.entity.SocialUsers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SocialUsersRepository usersRepo;
    private final CompanyUserRepository companyRepo;

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

        model.addAttribute("stats", stats);

        // 최근 활동 데이터
        List<Map<String, Object>> recentActivities = getRecentActivities();
        model.addAttribute("recentActivities", recentActivities);

        // 차트 데이터
        Map<String, Object> chartData = getChartData();
        model.addAttribute("chartData", chartData);

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

        return "crm/inquiry_report";
    }

    /**
     * 문의/신고 상세 페이지 (관리자용)
     */
    @GetMapping("/inquiry-report/detail/{id}")
    public String inquiryReportDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        // 문의/신고 상세 데이터 (실제로는 서비스에서 가져와야 함)
        Map<String, Object> report = getInquiryReportById(id);
        model.addAttribute("report", report);
        model.addAttribute("sidebarType", "admin");
        model.addAttribute("managerName", session.getAttribute("managerName"));

        return "crm/inquiry_detail";
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

        // 시술 후 부작용 문의
        Map<String, Object> report1 = new HashMap<>();
        report1.put("id", 1);
        report1.put("type", "inquiry");
        report1.put("title", "시술 후 부작용 문의");
        report1.put("reporterName", "김민수");
        report1.put("reporterPhone", "010-1234-5678");
        report1.put("reporterEmail", "kim@example.com");
        report1.put("companyName", "힝거 피부과");
        report1.put("description", "브이라인 리프팅 시술을 받은 후 얼굴이 부어오르고 통증이 있습니다. 정상적인 반응인지 확인하고 싶습니다.");
        report1.put("status", "pending");
        report1.put("priority", "high");
        report1.put("createdDate", "2024-01-15 14:30");
        reports.add(report1);

        // 의료진 태도 문제 신고
        Map<String, Object> report2 = new HashMap<>();
        report2.put("id", 2);
        report2.put("type", "report");
        report2.put("title", "의료진 태도 문제 신고");
        report2.put("reporterName", "이영희");
        report2.put("reporterPhone", "010-2345-6789");
        report2.put("reporterEmail", "lee@example.com");
        report2.put("companyName", "서울 성형외과");
        report2.put("description", "시술 중 의료진이 불친절하고 무성의한 태도로 시술을 진행했습니다. 환자에 대한 기본적인 예의가 부족했습니다.");
        report2.put("status", "processing");
        report2.put("priority", "medium");
        report2.put("createdDate", "2024-01-14 16:45");
        reports.add(report2);

        // 예약 변경 요청
        Map<String, Object> report3 = new HashMap<>();
        report3.put("id", 3);
        report3.put("type", "inquiry");
        report3.put("title", "예약 변경 요청");
        report3.put("reporterName", "박준호");
        report3.put("reporterPhone", "010-3456-7890");
        report3.put("reporterEmail", "park@example.com");
        report3.put("companyName", "강남 치과");
        report3.put("description", "개인 사정으로 인해 예약된 시술 일정을 다음 주로 변경하고 싶습니다. 가능한지 확인 부탁드립니다.");
        report3.put("status", "resolved");
        report3.put("priority", "low");
        report3.put("createdDate", "2024-01-13 10:20");
        reports.add(report3);

        // 시설 청결도 문제 신고
        Map<String, Object> report4 = new HashMap<>();
        report4.put("id", 4);
        report4.put("type", "report");
        report4.put("title", "시설 청결도 문제 신고");
        report4.put("reporterName", "최수진");
        report4.put("reporterPhone", "010-4567-8901");
        report4.put("reporterEmail", "choi@example.com");
        report4.put("companyName", "제주 한의원");
        report4.put("description", "병원 내부 시설이 불결하고 위생상 문제가 있다고 생각됩니다. 정기적인 청소와 소독이 필요합니다.");
        report4.put("status", "rejected");
        report4.put("priority", "medium");
        report4.put("createdDate", "2024-01-12 09:15");
        reports.add(report4);

        // 시술 비용 환불 요청
        Map<String, Object> report5 = new HashMap<>();
        report5.put("id", 5);
        report5.put("type", "inquiry");
        report5.put("title", "시술 비용 환불 요청");
        report5.put("reporterName", "정다은");
        report5.put("reporterPhone", "010-5678-9012");
        report5.put("reporterEmail", "jung@example.com");
        report5.put("companyName", "부산 피부과");
        report5.put("description", "시술 결과가 만족스럽지 않아 환불을 요청합니다. 계약서에 명시된 환불 정책에 따라 처리해주세요.");
        report5.put("status", "pending");
        report5.put("priority", "high");
        report5.put("createdDate", "2024-01-11 15:30");
        reports.add(report5);

        return reports;
    }

    private Map<String, Object> getInquiryReportById(Long id) {
        // 실제로는 DB에서 조회해야 함
        List<Map<String, Object>> reports = getInquiryReports();

        return reports.stream()
                .filter(report -> report.get("id").equals(id.intValue()))
                .findFirst()
                .orElse(new HashMap<>());
    }

    /**
     * 공지사항 & 알림 관리 페이지
     */
    @GetMapping("/notice-notify")
    public String noticeNotify(Model model, HttpSession session) {

        model.addAttribute("managerName", session.getAttribute("managerName"));

        // 공지사항 목록 (실제로는 서비스에서 가져와야 함)
        List<Map<String, Object>> notices = getNotices();
        model.addAttribute("notices", notices);

        // 알림 목록 (실제로는 서비스에서 가져와야 함)
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

    private List<Map<String, Object>> getNotices() {
        List<Map<String, Object>> notices = new ArrayList<>();

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
}
