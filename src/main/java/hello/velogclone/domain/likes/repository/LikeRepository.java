package hello.velogclone.domain.likes.repository;

import hello.velogclone.domain.likes.entity.Likes;
import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserAndPost(User user, Post post);
}
