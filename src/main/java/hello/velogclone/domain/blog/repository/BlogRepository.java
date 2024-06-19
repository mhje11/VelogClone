package hello.velogclone.domain.blog.repository;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findBlogByUser(User user);
}
