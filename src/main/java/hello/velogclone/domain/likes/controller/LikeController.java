package hello.velogclone.domain.likes.controller;

import hello.velogclone.domain.likes.service.LikeService;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/blogs/{blogId}/{postId}")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final PostService postService;
    @PostMapping("/likes")
    public String Likes(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            if (userDetails == null) {
                throw  new UnauthorizedException("로그인 후 사용가능한 기능 입니다.");
            }
            likeService.likePost(postId, userDetails.getUsername());
            PostResponseDto postDto = postService.findPostById(postId);
            Post post = postService.findPostEntityById(postId);
            postDto.setLikes((long) post.getLikes().size());
            model.addAttribute("post", postDto);
            model.addAttribute("blogId", blogId);
            model.addAttribute("postId", postId);
            return "redirect:/api/blogs/{blogId}/{postId}";
        } catch (UnauthorizedException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/api/login";
        }
    }
}
