package com.example.ApiRound.crm.hyeonah.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ManagerUserServiceImpl implements ManagerUserService {

    @Override
    public List<Map<String, Object>> getUserList(int pageNo, int amount, String search) {
        List<Map<String, Object>> users = new ArrayList<>();
        
        // 샘플 사용자 데이터 (실제로는 DB에서 조회) - 페이지네이션 테스트를 위해 14개 추가
        String[] usernames = {"둥그리둥둥", "힝구리퐁퐁", "thrujsjdf", "영화조아", "감튀엔케찹", "김민수", "이영희", "박철수", "정수진", "최동현",
                             "이서연", "박지훈", "최민지", "정현우"};
        String[] names = {"김은아", "성은지", "정승하", "고정민", "김정서", "김민수", "이영희", "박철수", "정수진", "최동현",
                         "이서연", "박지훈", "최민지", "정현우"};
        String[] emails = {"euneun@gmail.com", "sungsilver@naver.com", "hiJSH@gmail.com", "hihiko@naver.com", "sjdiahnw@gmail.com", 
                          "minsu@gmail.com", "younghee@naver.com", "chulsoo@gmail.com", "sujin@naver.com", "donghyun@gmail.com",
                          "seoyeon@naver.com", "jihoon@gmail.com", "minji@daum.net", "hyunwoo@naver.com"};
        
        // 검색 필터링
        List<Integer> filteredIndices = new ArrayList<>();
        for (int i = 0; i < usernames.length; i++) {
            if (search == null || search.trim().isEmpty() || 
                usernames[i].toLowerCase().contains(search.toLowerCase()) ||
                names[i].toLowerCase().contains(search.toLowerCase()) ||
                emails[i].toLowerCase().contains(search.toLowerCase())) {
                filteredIndices.add(i);
            }
        }
        
        // 페이지네이션 적용
        int startIndex = (pageNo - 1) * amount;
        int endIndex = Math.min(startIndex + amount, filteredIndices.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            int index = filteredIndices.get(i);
            Map<String, Object> user = new HashMap<>();
            user.put("userId", String.format("%05d", 10014 - index));
            user.put("username", usernames[index] + " 님");
            user.put("name", names[index]);
            user.put("email", emails[index]);
            user.put("joinDate", "Sep 12, 2021");
            user.put("reportCount", 0); // 신고 횟수
            users.add(user);
        }
        
        return users;
    }

    @Override
    public int getTotalUserCount(String search) {
        // 샘플 데이터의 전체 사용자 수 (실제로는 DB에서 조회) - 페이지네이션 테스트를 위해 14개 추가
        String[] usernames = {"둥그리둥둥", "힝구리퐁퐁", "thrujsjdf", "영화조아", "감튀엔케찹", "김민수", "이영희", "박철수", "정수진", "최동현",
                             "이서연", "박지훈", "최민지", "정현우"};
        String[] names = {"김은아", "성은지", "정승하", "고정민", "김정서", "김민수", "이영희", "박철수", "정수진", "최동현",
                         "이서연", "박지훈", "최민지", "정현우"};
        String[] emails = {"euneun@gmail.com", "sungsilver@naver.com", "hiJSH@gmail.com", "hihiko@naver.com", "sjdiahnw@gmail.com", 
                          "minsu@gmail.com", "younghee@naver.com", "chulsoo@gmail.com", "sujin@naver.com", "donghyun@gmail.com",
                          "seoyeon@naver.com", "jihoon@gmail.com", "minji@daum.net", "hyunwoo@naver.com"};
        
        if (search == null || search.trim().isEmpty()) {
            return usernames.length;
        }
        
        int count = 0;
        for (int i = 0; i < usernames.length; i++) {
            if (usernames[i].toLowerCase().contains(search.toLowerCase()) ||
                names[i].toLowerCase().contains(search.toLowerCase()) ||
                emails[i].toLowerCase().contains(search.toLowerCase())) {
                count++;
            }
        }
        
        return count;
    }
}
