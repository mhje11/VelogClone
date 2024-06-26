package hello.velogclone.domain.comment.service;

import hello.velogclone.domain.comment.dto.CommentCreateDto;
import hello.velogclone.domain.comment.dto.CommentResponseDto;
import hello.velogclone.domain.comment.dto.CommentUpdateDto;
import hello.velogclone.domain.comment.entity.Comment;
import hello.velogclone.domain.comment.repository.CommentRepository;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.repository.PostRepository;
import hello.velogclone.domain.user.entity.Role;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.global.exception.CommentNotFoundException;
import hello.velogclone.global.exception.PostNotFoundException;
import hello.velogclone.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Comment createComment(CommentCreateDto commentCreateDto, String loginId, Long postId) {
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException("해당 포스트를 찾을 수 없습니다."));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(commentCreateDto.getContent());

        return commentRepository.save(comment);
    }

    public Comment updateComment(CommentUpdateDto commentUpdateDto, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));        comment.setContent(commentUpdateDto.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));
        commentRepository.delete(comment);
    }

    public boolean Authorization(Long commentId, String loginId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CommentNotFoundException("해당 댓글을 찾을 수 없습니다."));
        User user = userRepository.findByLoginId(loginId).orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));
        if(comment.getUser().getLoginId().equals(loginId) || user.getRole().equals(Role.ROLE_ADMIN)) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAllCommentByPostId(Long postId) {
        List<Comment> comments = commentRepository.findCommentByPostId(postId);
        return comments.stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getUser().getLoginId(),
                        comment.getContent()
                ))
                .collect(Collectors.toList());
    }
}
