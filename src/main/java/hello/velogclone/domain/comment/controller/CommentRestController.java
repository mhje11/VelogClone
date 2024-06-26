package hello.velogclone.domain.comment.controller;

import hello.velogclone.domain.comment.dto.CommentCreateDto;
import hello.velogclone.domain.comment.dto.CommentResponseDto;
import hello.velogclone.domain.comment.dto.CommentUpdateDto;
import hello.velogclone.domain.comment.entity.Comment;
import hello.velogclone.domain.comment.service.CommentService;
import hello.velogclone.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
                throw new UnauthorizedException("로그인이 필요합니다.");
            }
            if (!commentService.Authorization(commentId, userDetails.getUsername())) {
                throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
            }
            Comment comment = commentService.updateComment(commentUpdateDto, commentId);
            return ResponseEntity.ok(comment);
        }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인 후 이용 가능합니다.");
        }
        if (!commentService.Authorization(commentId, userDetails.getUsername())) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.ok("댓글 삭제 완료");
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseDto>> findAllComment(@PathVariable("postId") Long postId) {
        List<CommentResponseDto> comments = commentService.findAllCommentByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<String> createComment(@PathVariable("postId") Long postId, @RequestBody CommentCreateDto commentCreateDto, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("로그인 후 이용 가능합니다.");
        }
        commentService.createComment(commentCreateDto, userDetails.getUsername(), postId);
        return ResponseEntity.ok("댓글 생성 완료");
    }
}
