package hello.velogclone.domain.post.controller;


import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.domain.tag.entity.Tag;
import hello.velogclone.domain.tag.service.TagService;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final TagService tagService;


    @GetMapping("/{postId}")
    public String viewPost(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, Model model) {
        PostResponseDto post = postService.findPostById(postId);
        List<Tag> tags = tagService.findTagByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
        model.addAttribute("tags", tags);
        return "post/detail";
    }

    @GetMapping("/create")
    public String createForm(@PathVariable("blogId") Long blogId, Model model) {
        PostRequestDto post = new PostRequestDto();
        model.addAttribute("blogId", blogId);
        model.addAttribute("post", post);
        return "post/create";
    }

    @PostMapping("/create")
    public String createPost(@PathVariable("blogId") Long blogId, @ModelAttribute PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByLoginId(userDetails.getUsername()).get();
        postRequestDto.setBlogId(blogId);
        postService.createPost(postRequestDto, user);
        return "redirect:/api/blogs/" + blogId;
    }

    @GetMapping("/{postId}/update")
    public String editForm(@PathVariable("postId") Long postId, @PathVariable("blogId") Long blogId, Model model) {
        PostResponseDto post = postService.findPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
        model.addAttribute("postId", postId);
        return "post/edit";
    }


    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return "redirect:/api/blogs/" + blogId;
    }

}
