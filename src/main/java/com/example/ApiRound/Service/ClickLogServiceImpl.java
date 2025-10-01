package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ClickLog;
import com.example.ApiRound.repository.ClickLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ClickLogServiceImpl implements ClickLogService {
    
    @Autowired
    private ClickLogRepository clickLogRepository;
    
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
}