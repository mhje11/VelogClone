package hello.velogclone.domain.post.controller;

import hello.velogclone.domain.post.dto.PostResponseDto;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostRestController {
    private final PostService postService;


    @GetMapping("/api/blogs/{blogId}/edit/post")
    public ResponseEntity<List<PostResponseDto>> findAllPost(@PathVariable("blogId") Long blogId) {
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
        postService.updatePost(postId, post, userDetails.getUsername());
        return ResponseEntity.ok(post);
    }

}
