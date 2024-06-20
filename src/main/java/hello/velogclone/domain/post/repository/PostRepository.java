package hello.velogclone.domain.post.repository;

import hello.velogclone.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByBlogId(Long blogId);
    List<Post> findAllByBlogId(Long blogId);
}
