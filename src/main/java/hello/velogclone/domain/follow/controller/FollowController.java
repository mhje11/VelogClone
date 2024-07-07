package hello.velogclone.domain.follow.controller;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.service.BlogService;
import hello.velogclone.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final BlogService blogService;

    @GetMapping("/follower")
    public String followList(@PathVariable("blogId") Long blogId, Model model) {
        model.addAttribute("blogId", blogId);
        return "follow/followerList";
    }

    @GetMapping("/following")
    public String followingList(@PathVariable("blogId") Long blogId, Model model) {
        model.addAttribute("blogId", blogId);
        return "follow/followingList";
    }

    @GetMapping("/user/{loginId}")
    public String redirectToUserBlog(@PathVariable("loginId") String loginId, RedirectAttributes redirectAttributes) {
        Optional<Blog> blog = blogService.findBlogByUserLoginId(loginId);

        if (blog.isPresent()) {
            Long blogId = blog.get().getId();
            return "redirect:/api/blogs/" + blogId;
        }
        redirectAttributes.addFlashAttribute("error", "해당 유저는 블로그가 존재하지 않습니다.");
        return "redirect:/api/blogs/{blogId}/follower";
    }
}
