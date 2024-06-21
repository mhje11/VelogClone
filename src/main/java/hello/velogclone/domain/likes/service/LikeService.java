package hello.velogclone.domain.likes.service;

import hello.velogclone.domain.likes.entity.Likes;
import hello.velogclone.domain.likes.repository.LikeRepository;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.post.service.PostService;
import hello.velogclone.domain.user.entity.User;
import hello.velogclone.domain.user.repository.UserRepository;
import hello.velogclone.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostService postService;

    public void likePost(Long postId, String loginId) {
        User user = userRepository.findByLoginId(loginId).get();
        Post post = postService.findPostEntityById(postId);
        Optional<Likes> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return;
        }
        Likes likes = new Likes();
        likes.setUser(user);
        likes.setPost(post);
        likeRepository.save(likes);
    }
}
