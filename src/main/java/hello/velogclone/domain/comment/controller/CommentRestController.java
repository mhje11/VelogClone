package hello.velogclone.domain.comment.controller;

import hello.velogclone.domain.comment.dto.CommentCreateDto;
import hello.velogclone.domain.comment.dto.CommentUpdateDto;
import hello.velogclone.domain.comment.entity.Comment;
import hello.velogclone.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/blogs/{blogId}/{postId}/comments")
public class CommentRestController {
    private final CommentService commentService;

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable("commentId") Long commentId,
                                                 @RequestBody CommentUpdateDto commentUpdateDto,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(null);
        }
        if (!commentService.Authorization(commentId, userDetails.getUsername())) {
            return ResponseEntity.status(403).body(null);
        }
        Comment comment = commentService.updateComment(commentUpdateDto, commentId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        if (!commentService.Authorization(commentId, userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Comment>> findAllComment(@PathVariable("postId") Long postId) {
        List<Comment> comments = commentService.findAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<String> createComment(@PathVariable("postId") Long postId, @RequestBody CommentCreateDto commentCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        commentService.createComment(commentCreateDto, userDetails.getUsername(), postId);
        return ResponseEntity.ok("댓글 생성 완료");
    }
}
