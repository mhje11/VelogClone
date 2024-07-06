package hello.velogclone.domain.blog.controller;

import hello.velogclone.domain.blog.dto.BlogDto;
import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.service.BlogService;
import hello.velogclone.domain.follow.service.FollowService;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/blogs")
public class BlogController {

    private final BlogService blogService;
    private final PostService postService;
    private final FollowService followService;

    @GetMapping("/create")
    public String createBlogForm(Model model, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 사용 가능한 기능입니다.");
            return "redirect:/api/login";
        }
        model.addAttribute("blog", new BlogDto());
        return "blog/createBlogForm";
    }

    @PostMapping("/create")
    public String createBlog(@ModelAttribute BlogDto blogDto, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
        if (blog.isEmpty()) {
            Blog createdBlog = blogService.createBlog(blogDto, userDetails.getUsername());
            return "redirect:/api/blogs/" + createdBlog.getId();
        }
        redirectAttributes.addFlashAttribute("error", "이미 블로그가 존재 합니다.");
        return "redirect:/";
    }

    @GetMapping("/{blogId}")
    public String getBlog(@PathVariable("blogId") Long blogId,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          Model model) {
        Blog blog = blogService.getBlogById(blogId);
        int size = 4; // 페이지 당 게시글 수
        Page<PostResponseDto> postPage = postService.findAllByBlogIdAndTemporal(blogId, false, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("blog", blog);
        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("followingCount", followService.getFollowingCount(blogId));
        model.addAttribute("followerCount", followService.getFollowerCount(blogId));
        return "blog/viewBlog";
    }


    @GetMapping("/my")
    public String getMyBlog(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "로그인 후 사용 가능한 기능입니다.");
            return "redirect:/api/login";
        }
        Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
        if (blog.isEmpty()) {
            redirectAttributes.addFlashAttribute("blogExistError", "먼저 블로그를 생성하세요");
            return "redirect:/";
        }
        return "redirect:/api/blogs/" + blog.get().getId();
    }

    @GetMapping("/{blogId}/edit")
    public String editBlogForm(@PathVariable("blogId") Long blogId, @AuthenticationPrincipal UserDetails userDetails, Model model, RedirectAttributes redirectAttributes) {
            Blog blog = blogService.getBlogById(blogId);
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("error", "로그인 후 이용 가능 합니다.");
                return "redirect:/api/login";
            }
            if (!blog.getUser().getLoginId().equals(userDetails.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "블로그를 수정할 권한이 없습니다.");
                return "redirect:/api/blogs/" + blogId;
            }
            BlogDto blogDto = new BlogDto();
            blogDto.setTitle(blog.getTitle());
            model.addAttribute("blog", blogDto);
            model.addAttribute("blogId", blogId);
            return "blog/editBlogForm";

    }

    @PostMapping("/{blogId}/edit")
    public String editBlog(@PathVariable("blogId") Long blogId, @ModelAttribute BlogDto blogDto, @AuthenticationPrincipal UserDetails userDetails) {
        blogService.updateBlog(blogDto, userDetails.getUsername());
        return "redirect:/api/blogs/" + blogId;
    }
}

