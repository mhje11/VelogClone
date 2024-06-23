package hello.velogclone.domain.follow.controller;

import hello.velogclone.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

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
}
