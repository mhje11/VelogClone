package hello.velogclone.domain.blog.controller;

import hello.velogclone.domain.blog.dto.BlogDto;
import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.service.BlogService;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/create")
    public String createBlogForm(Model model) {
        model.addAttribute("blog", new BlogDto());
        return "blog/createBlogForm";
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
        List<PostResponseDto> posts = postService.findAllPostByBlogId(blogId);
        model.addAttribute("blog", blog);
        model.addAttribute("posts", posts);
        return "blog/viewBlog";
    }

    @GetMapping("/my")
    public String getMyBlog(@AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        Optional<Blog> blog = blogService.findBlogByUserLoginId(userDetails.getUsername());
        if (blog.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "먼저 블로그를 생성하세요");
            return "redirect:/";
        }
        return "redirect:/api/blogs/" + blog.get().getId();
    }

    @GetMapping("/{blogId}/edit")
    public String editBlogForm(@PathVariable("blogId") Long blogId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        Blog blog = blogService.getBlogById(blogId);
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
