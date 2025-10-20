package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ClickLog;
import com.example.ApiRound.repository.ClickLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ClickLogServiceImpl implements ClickLogService {

    private final ClickLogRepository clickLogRepository;

    @Override
    public void logClick(Long companyId) {
        ClickLog clickLog = new ClickLog(companyId);
        clickLogRepository.save(clickLog);
    }

    @Override
    public List<Object[]> getClickCountsLast7Days() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return clickLogRepository.findTopCompaniesByClickCount(startDate);
    }

    @Override
    public List<Object[]> getTop3CompaniesLast7Days() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        return clickLogRepository.findTopCompaniesByClickCount(startDate);
    }

    @Override
    public ClickLog saveClickLog(ClickLog clickLog) {
        return clickLogRepository.save(clickLog);
    }

    @Override
    public long getClickCountByCompanyIdSince(Long companyId, LocalDateTime since) {
        return clickLogRepository.countByCompanyIdAndClickedAtAfter(companyId, since);
    }

    @Override
    public List<Map<String, Object>> getDailyClicksByCompany(Long companyId, int days) {
        List<Map<String, Object>> dailyStats = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            long count = clickLogRepository.countByCompanyIdAndClickedAtBetween(
                    companyId, startOfDay, endOfDay);

            Map<String, Object> stat = new HashMap<>();
            stat.put("date", date.toString());
            stat.put("count", count);
            dailyStats.add(stat);
        }
        return dailyStats;
    }
}
