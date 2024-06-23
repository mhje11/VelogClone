package hello.velogclone.domain.follow.controller;

import hello.velogclone.domain.follow.dto.FollowDto;
import hello.velogclone.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
public class FollowRestController {
    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<String> followBlog(@PathVariable("blogId") Long blogId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        followService.followBlog(blogId, userDetails.getUsername());
        return ResponseEntity.ok("블로그를 팔로우했습니다.");
    }

    @GetMapping("/follower/List")
    public ResponseEntity<List<FollowDto>> getFollowers(@PathVariable("blogId") Long blogId) {
        List<FollowDto> followers = followService.findAllFollowers(blogId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/following/List")
    public ResponseEntity<List<FollowDto>> getFollowings(@PathVariable("blogId") Long blogId) {
        List<FollowDto> followings = followService.findAllFollowingsByBlogId(blogId);
        return ResponseEntity.ok(followings);
    }
}
