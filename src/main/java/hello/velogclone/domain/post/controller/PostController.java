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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
@Slf4j
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
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        log.info("createPost called with blogId: {}, postRequestDto: {}", blogId, postRequestDto);
        try {
            User user = userRepository.findByLoginId(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
            postRequestDto.setBlogId(blogId);
            postService.createPost(postRequestDto, user);
            return "redirect:/api/blogs/" + blogId;
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "게시글 작성 중 오류가 발생했습니다.");
            return "redirect:/api/blogs/" + blogId + "/create";
        }
    }
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "_" + file.getOriginalFilename();
            String baseDir = new File("src/main/resources/static/images/posts/").getAbsolutePath();
            String imagePathString = baseDir + "/" + fileName;
            Path imagePath = Paths.get(imagePathString);

            if (!Files.exists(imagePath.getParent())) {
                Files.createDirectories(imagePath.getParent());
            }

            file.transferTo(imagePath.toFile());
            String imageUrl = "/images/posts/" + fileName;

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("이미지 업로드 중 오류 발생", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "이미지 업로드 중 오류 발생");
            return ResponseEntity.status(500).body(response);
        }
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
