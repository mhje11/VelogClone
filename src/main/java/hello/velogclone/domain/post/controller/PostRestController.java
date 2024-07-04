package hello.velogclone.domain.post.controller;


import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.global.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostRestController {
    private final PostService postService;
    private final CommonUtil commonUtil;


    @GetMapping("/api/blogs/{blogId}/edit/post")
    public ResponseEntity<List<PostResponseDto>> findAllPostNotTemporal(@PathVariable("blogId") Long blogId) {
        List<PostResponseDto> posts = postService.findAllPostByBlogId(blogId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<Map<String, Object>> getBlogPosts(@PathVariable("blogId") Long blogId,
                                                            @RequestParam(value = "page", defaultValue = "0") int page,
                                                            @RequestParam(value = "size", defaultValue = "4") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PostResponseDto> postPage = postService.findAllByBlogIdAndTemporal(blogId, false, pageRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("posts", postPage.getContent());
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/blogs/{blogId}/edit/temporalPost")
    public ResponseEntity<List<PostResponseDto>> findAllPostTemporal(@PathVariable("blogId") Long blogId) {
        List<PostResponseDto> posts = postService.findAllPostByBlogId(blogId);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/api/blogs/{blogId}/edit/post/{postId}/delete")
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok("게시글이 성공적으로 삭제됐습니다.");
    }

    @PutMapping("/api/blogs/{blogId}/posts/{postId}/update")
    public ResponseEntity<Post> updatePost(@PathVariable("postId") Long postId,
                                           @RequestBody Post post,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        String content = commonUtil.markdownToHtml(post.getContent());
        post.setContent(content);
        postService.updatePost(postId, post, userDetails.getUsername());
        return ResponseEntity.ok(post);
    }


}
