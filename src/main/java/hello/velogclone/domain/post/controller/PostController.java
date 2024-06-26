package hello.velogclone.domain.post.controller;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.domain.tag.entity.Tag;
import hello.velogclone.domain.tag.service.TagService;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.BlogNotFoundException;
import hello.velogclone.global.exception.PostNotFoundException;
import hello.velogclone.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final BlogRepository blogRepository;

    @GetMapping("/{postId}")
    public String viewPost(@PathVariable("blogId") Long blogId,
                           @PathVariable("postId") Long postId,
                           Model model) {
        PostResponseDto post = postService.findPostById(postId);
        List<Tag> tags = tagService.findTagByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("blogId", blogId);
        model.addAttribute("tags", tags);
        return "post/detail";
    }

    @GetMapping("/create")
    public String createForm(@PathVariable("blogId") Long blogId,
                             Model model, @AuthenticationPrincipal UserDetails userDetails
    , RedirectAttributes redirectAttributes) {
        try {
            Blog blog = checkBlogAndUser(blogId, userDetails);
            PostRequestDto post = new PostRequestDto();
            model.addAttribute("blogId", blogId);
            model.addAttribute("post", post);
            return "post/create";
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/blogs/" + blogId;
        } catch (BlogNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/create")
    public String createPost(@PathVariable("blogId") Long blogId,
                             @ModelAttribute PostRequestDto postRequestDto,
                             @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
        postRequestDto.setBlogId(blogId);
        postService.createPost(postRequestDto, user);
        return "redirect:/api/blogs/" + blogId;
    }

    @GetMapping("/{postId}/update")
    public String editForm(@PathVariable("postId") Long postId,
                           @PathVariable("blogId") Long blogId,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails
    , RedirectAttributes redirectAttributes) {
        try {
            Blog blog = checkBlogAndUser(blogId, userDetails);
            PostResponseDto post = postService.findPostById(postId);
            if (post == null) {
                throw new PostNotFoundException("게시글을 찾을 수 없습니다.");
            }
            model.addAttribute("post", post);
            model.addAttribute("blogId", blogId);
            model.addAttribute("postId", postId);
            return "post/edit";
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/blogs/" + blogId;
        } catch (BlogNotFoundException | PostNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("blogId") Long blogId,
                             @PathVariable("postId") Long postId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model
    , RedirectAttributes redirectAttributes) {
        try {
            Blog blog = checkBlogAndUser(blogId, userDetails);
            postService.deletePost(postId, userDetails.getUsername());
            return "redirect:/api/blogs/" + blogId;
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/blogs/" + blogId;
        } catch (BlogNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    private Blog checkBlogAndUser(Long blogId, UserDetails userDetails) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));
        if (!blog.getUser().getLoginId().equals(userDetails.getUsername())) {
            throw new UnauthorizedException("권한이 없습니다.");
        }
        return blog;
    }
}
