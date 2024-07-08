package hello.velogclone.domain.post.controller;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.blog.repository.BlogRepository;
import hello.velogclone.domain.post.dto.PostRequestDto;
import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.domain.postimage.entity.PostImage;
import hello.velogclone.domain.postimage.service.PostImageService;
import hello.velogclone.domain.tag.entity.Tag;
import hello.velogclone.domain.tag.service.TagService;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.BlogNotFoundException;
import hello.velogclone.global.exception.UnauthorizedException;
import hello.velogclone.global.util.CommonUtil;
import jakarta.servlet.http.HttpSession;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Controller
@RequestMapping("/api/blogs/{blogId}")
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final BlogRepository blogRepository;
    private final CommonUtil commonUtil;
    private final PostImageService postImageService;


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
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("message", "로그인 후 이용 가능합니다.");
                return "redirect:/api/login";
            }
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
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        try {
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("error", "로그인 후 이용 가능합니다.");
                return "redirect:/api/login";
            }
            User user = userRepository.findByLoginId(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));
            postRequestDto.setBlogId(blogId);
            String content = commonUtil.markdownToHtml(postRequestDto.getContent());
            postRequestDto.setContent(content);
            Post post = postService.createPost(postRequestDto, user);

            // 세션에서 이미지 URL을 가져와서 PostImage로 저장
            List<String> imageUrls = (List<String>) session.getAttribute("imageUrls");
            if (imageUrls != null) {
                for (String imageUrl : imageUrls) {
                    PostImage postImage = new PostImage();
                    postImage.setPost(post);
                    postImage.setUrl(imageUrl);
                    postImageService.save(postImage);
                }
                session.removeAttribute("imageUrls"); // 세션에서 이미지 URL 제거
            }

            return "redirect:/api/blogs/" + blogId;
        } catch (Exception e) {
            log.error("게시글 작성 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "게시글 작성 중 오류가 발생했습니다.");
            return "redirect:/api/blogs/" + blogId + "/create";
        }
    }

    @ResponseBody
    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file,
                                                           @AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userRepository.findByLoginId(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다."));

            String imageUrl = postImageService.uploadImage(file, userDetails.getUsername());

            // 세션에 이미지 URL 저장
            List<String> imageUrls = (List<String>) session.getAttribute("imageUrls");
            if (imageUrls == null) {
                imageUrls = new ArrayList<>();
            }
            imageUrls.add(imageUrl);
            session.setAttribute("imageUrls", imageUrls);

            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("이미지 업로드 중 오류 발생", e);
            response.put("error", "이미지 업로드 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            log.error("기타 오류 발생", e);
            response.put("error", "기타 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @GetMapping("/{postId}/update")
    public String editForm(@PathVariable("postId") Long postId,
                           @PathVariable("blogId") Long blogId,
                           Model model,
                           @AuthenticationPrincipal UserDetails userDetails
    , RedirectAttributes redirectAttributes) {
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("message", "로그인 후 이용 가능합니다.");
                return "redirect:/api/login";
            }
            Blog blog = blogRepository.findById(blogId)
                    .orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));

            if (!blog.getUser().getLoginId().equals(userDetails.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "수정할 권한이 없습니다.");
                return "redirect:/api/blogs/" + blogId;
            }

            PostResponseDto post = postService.findPostById(postId);

            if (post == null) {
                redirectAttributes.addFlashAttribute("error", "게시글을 찾을 수 없습니다.");
                return "redirect:/api/blogs/" + blogId;
            }
            String content = commonUtil.htmlToMarkdown(post.getContent());
            post.setContent(content);

            model.addAttribute("post", post);
            model.addAttribute("blogId", blogId);
            model.addAttribute("postId", postId);
            return "post/edit";
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable("blogId") Long blogId,
                             @PathVariable("postId") Long postId,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model
    , RedirectAttributes redirectAttributes) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("error", "로그인후 이용 가능합니다.");
            return "redirect:/api/login";
        }

        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new BlogNotFoundException("해당 블로그를 찾을 수 없습니다."));

        if (!blog.getUser().getLoginId().equals(userDetails.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "삭제할 권한이 없습니다.");
            return "redirect:/api/blogs/" + blogId;
        }
        postImageService.deleteImagesByPostId(postId);
        postService.deletePost(postId, userDetails.getUsername());
        redirectAttributes.addFlashAttribute("error", "게시글 삭제 완료");
        return "redirect:/api/blogs/" + blogId;


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
