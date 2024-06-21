package hello.velogclone.domain.follow.repository;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.follow.entity.Follow;
import hello.velogclone.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByBlog(Blog blog);

    List<Follow> findByUser(User user);

    Follow findByBlogAndUser(Blog blog, User user);

    int countByUser(User user);
    int countByBlog(Blog blog);
}
