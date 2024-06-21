package hello.velogclone.domain.post.controller;

import hello.velogclone.domain.comment.dto.CommentCreateDto;
import hello.velogclone.domain.comment.dto.CommentResponseDto;
import hello.velogclone.domain.comment.entity.Comment;
import hello.velogclone.domain.comment.service.CommentService;
import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
    private final CommentService commentService;


    @GetMapping("/{postId}")
    public String viewPost(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, Model model) {
        PostResponseDto post = postService.findPostById(postId);
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        List<CommentResponseDto> comments = commentService.findAllCommentByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
//        model.addAttribute("comment", commentCreateDto);
//        model.addAttribute("comments", comments);
        return "post/detail";
    }

    @GetMapping("/create")
    public String createForm(@PathVariable("blogId") Long blogId, Model model) {
        model.addAttribute("blogId", blogId);
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
    public String editForm(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, Model model) {
        PostResponseDto post = postService.findPostById(postId);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
        return "post/edit";
    }

    @PostMapping("/{postId}/update")
    public String updatePost(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, @ModelAttribute PostRequestDto postRequestDto) {
        postService.updatePost(postId, postRequestDto);
        return "redirect:/api/blogs/" + blogId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("blogId") Long blogId, @PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return "redirect:/api/blogs/" + blogId;
    }
}
