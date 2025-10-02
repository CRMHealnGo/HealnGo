package com.example.ApiRound.Controller;

import com.example.ApiRound.Service.CommunityPostService;
import com.example.ApiRound.entity.CommunityPost;
import com.example.ApiRound.dto.SocialUserDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/community")
public class CommunityPostController {

    private final CommunityPostService service;
    public CommunityPostController(CommunityPostService service) { this.service = service; }

    // 목록: /community, /community/ , /community/list 모두 허용
    @GetMapping({"", "/", "/list"})
    public String listPosts(Model model, HttpSession session){
        List<CommunityPost> posts = service.getAllPosts();
        SocialUserDTO loginUser = (SocialUserDTO) session.getAttribute("loginUser");
        if (loginUser != null) {
            for (CommunityPost p : posts) {
                boolean liked = service.hasUserLiked(p.getPostId(), loginUser.getName());
                // Entity에는 setLikedByCurrentUser 메서드가 없으므로 별도 처리 필요
            }
        }
        System.out.println("posts size: " + posts.size());
        model.addAttribute("posts", posts);
        return "community";
    }

    @GetMapping("/view/{postId}")
    public String viewPost(@PathVariable Long postId, Model model){
        CommunityPost post = service.getPostById(postId);
        model.addAttribute("post", post);
        return "community/view";
    }

    @GetMapping("/write")
    public String writeForm(Model model){
        model.addAttribute("post", new CommunityPost());
        return "community/write";
    }

    @PostMapping("/write") // ← 슬래시 추가
    public String submitWrite(@ModelAttribute CommunityPost post){
        service.createPost(post);
        return "redirect:/community";       // ← 목록으로
        // (만약 /community/list 로 가고 싶으면 위의 @GetMapping({"", "/", "/list"}) 가 있으니 그대로 써도 됨)
    }

    // 글 작성 (fetch POST)
    @PostMapping(value="/write/fetch", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String writeFetch(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String category,
            HttpSession session
    ) {
        // 로그인 상태 확인
        SocialUserDTO loginUser = (SocialUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "login_required";
        }

        CommunityPost post = new CommunityPost();
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        post.setUserId(loginUser.getName()); // userId 저장

        try {
            service.createPost(post);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping(value="/edit/fetch", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public String editFetch(
            @RequestParam Long postId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String category,
            HttpSession session
    ) {
        // 로그인 상태 확인
        SocialUserDTO loginUser = (SocialUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "login_required";
        }

        // 간단 검증
        if (postId <= 0 || title == null || title.isBlank()
                || content == null || content.isBlank()
                || category == null || category.isBlank()) {
            return "invalid";
        }

        // 글 작성자 확인
        CommunityPost existingPost = service.getPostById(postId);
        if (existingPost == null) {
            return "post_not_found";
        }

        // 본인이 작성한 글인지 확인 (userId 대신 authorName 사용)
        if (!loginUser.getName().equals(existingPost.getUserId())) {
            return "unauthorized";
        }

        // Entity 업데이트
        existingPost.setTitle(title);
        existingPost.setContent(content);
        existingPost.setCategory(category);

        try {
            // 업데이트 실행
            service.updatePost(existingPost);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @GetMapping("/edit/{postId}")
    public String editForm(@PathVariable Long postId, Model model){
        CommunityPost post = service.getPostById(postId);
        model.addAttribute("post", post);
        return "community/edit";
    }

    @PostMapping("/edit")
    public String submitEdit(@ModelAttribute CommunityPost post) {
        service.updatePost(post);
        return "redirect:/community/view/" + post.getPostId();
    }

    // fetch 전용 삭제 (시큐리티 없음)
    @PostMapping("/delete")
    @ResponseBody
    public String deletePostFetch(@RequestParam Long postId, HttpSession session) {
        // 로그인 상태 확인
        SocialUserDTO loginUser = (SocialUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "login_required";
        }

        // 글 작성자 확인
        CommunityPost existingPost = service.getPostById(postId);
        if (existingPost == null) {
            return "post_not_found";
        }

        // 본인이 작성한 글인지 확인 (userId 대신 authorName 사용)
        if (!loginUser.getName().equals(existingPost.getUserId())) {
            return "unauthorized";
        }

        try {
            service.deletePost(postId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/insert")
    @ResponseBody
    public String insertPost(@RequestParam String title, @RequestParam String content) {
        CommunityPost post = new CommunityPost();
        post.setTitle(title);
        post.setContent(content);
        service.createPost(post);
        return "success";
    }

        // 좋아요 토글: 세션에 사용자별 likedPosts를 저장하여 토글 상태를 관리
        @PostMapping(value="/like/toggle", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        @ResponseBody
        public java.util.Map<String, Object> toggleLike(@RequestParam Long postId, HttpSession session) {
            java.util.Map<String, Object> resp = new java.util.HashMap<>();
            com.example.ApiRound.dto.SocialUserDTO loginUser = (com.example.ApiRound.dto.SocialUserDTO) session.getAttribute("loginUser");
            if (loginUser == null) {
                resp.put("status", "login_required");
                return resp;
            }

            Object likedAttr = session.getAttribute("likedPosts");
            java.util.Set<Long> likedPosts;
            if (likedAttr instanceof java.util.Set) {
                likedPosts = (java.util.Set<Long>) likedAttr;
            } else {
                likedPosts = new java.util.HashSet<>();
                session.setAttribute("likedPosts", likedPosts);
            }

            boolean alreadyLiked = likedPosts.contains(postId);
            try {
                if (alreadyLiked) {
                    // 취소 → 카운트 감소
                    service.decrementLikeCount(postId);
                    likedPosts.remove(postId);
                } else {
                    // 좋아요 → 카운트 증가
                    service.incrementLikeCount(postId);
                    likedPosts.add(postId);
                }
                int newCount = service.getLikeCount(postId);
                resp.put("status", "success");
                resp.put("liked", !alreadyLiked);
                resp.put("likeCount", newCount);
            } catch (Exception e) {
                e.printStackTrace();
                resp.put("status", "fail");
            }
            return resp;
        }

}
