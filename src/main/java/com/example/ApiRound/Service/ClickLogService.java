package com.example.ApiRound.Service;

import com.example.ApiRound.entity.ClickLog;

import java.util.List;

public interface ClickLogService {
    void logClick(Long companyId);
    List<Object[]> getClickCountsLast7Days();
    List<Object[]> getTop3CompaniesLast7Days();
    ClickLog saveClickLog(ClickLog clickLog);
}