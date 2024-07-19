package hello.velogclone.domain.follow.controller;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.service.BlogService;
import hello.velogclone.domain.follow.dto.FollowDto;
import hello.velogclone.domain.follow.service.FollowService;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.service.UserService;
import hello.velogclone.global.exception.UnauthorizedException;
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
    private final UserService userService;
    private final BlogService blogService;

    @PostMapping("/follow")
    public ResponseEntity<String> followBlog(@PathVariable("blogId") Long blogId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인 후 이용 가능합니다.");
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

        Blog blog = blogService.getBlogById(blogId);
        User user = userService.findUserEntityByLoginId(blog.getUser().getLoginId());
        List<FollowDto> followings = followService.findAllFollowingsByUser(user);
        return ResponseEntity.ok(followings);
    }
}
