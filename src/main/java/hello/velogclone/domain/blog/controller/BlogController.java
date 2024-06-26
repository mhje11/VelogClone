package hello.velogclone.domain.blog.controller;

import hello.velogclone.domain.blog.dto.BlogDto;
import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.service.BlogService;
import hello.velogclone.domain.follow.service.FollowService;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;
    private final PostService postService;
    private final FollowService followService;

    @GetMapping("/create")
    public String createBlogForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                throw new UnauthorizedException("로그인후 사용 가능한 기능입니다.");
            }
            model.addAttribute("blog", new BlogDto());
            return "blog/createBlogForm";
        } catch (UnauthorizedException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/api/login";
        }

    }

    @PostMapping("/create")
    public String createBlog(@ModelAttribute BlogDto blogDto, @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
        Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
        if (blog.isEmpty()) {
            Blog createdBlog = blogService.createBlog(blogDto, userDetails.getUsername());
            return "redirect:/api/blogs/" + createdBlog.getId();
        }
        redirectAttributes.addFlashAttribute("blogExistError", "이미 블로그가 존재 합니다.");
        return "redirect:/";
    }

    @GetMapping("/{blogId}")
    public String getBlog(@PathVariable("blogId") Long blogId, Model model) {
        Blog blog = blogService.getBlogById(blogId);
        List<PostResponseDto> posts = postService.findAllByBlogIdAndTemporal(blogId, false);
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        model.addAttribute("followingCount", followService.getFollowingCount(blogId));
        model.addAttribute("followerCount", followService.getFollowerCount(blogId));
        return "blog/viewBlog";
    }

    @GetMapping("/my")
    public String getMyBlog(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        try {
            if (userDetails == null) {
                throw new UnauthorizedException("로그인후 사용 가능한 기능입니다.");
            }
            Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
            if (blog.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "먼저 블로그를 생성하세요");
                return "redirect:/";
            }
            return "redirect:/api/blogs/" + blog.get().getId();
        } catch (UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/api/login";
        }

    }

    @GetMapping("/{blogId}/edit")
    public String editBlogForm(@PathVariable("blogId") Long blogId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            Blog blog = blogService.getBlogById(blogId);
            if (!blog.getUser().getLoginId().equals(userDetails.getUsername())) {
                throw new UnauthorizedException("블로그를 수정할 권한이 없습니다.");
            }
            BlogDto blogDto = new BlogDto();
            blogDto.setTitle(blog.getTitle());
            model.addAttribute("blog", blogDto);
            model.addAttribute("blogId", blogId);
            return "blog/editBlogForm";
        } catch (UnauthorizedException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/api/blogs/" + blogId;
        }

    }

    @PostMapping("/{blogId}/edit")
    public String editBlog(@PathVariable("blogId") Long blogId, @ModelAttribute BlogDto blogDto, @AuthenticationPrincipal UserDetails userDetails) {
        blogService.updateBlog(blogDto, userDetails.getUsername());
        return "redirect:/api/blogs/" + blogId;
    }
}
