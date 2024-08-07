package hello.velogclone.domain.post.repository;

import hello.velogclone.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByBlogId(Long blogId);
    List<Post> findAllByBlogId(Long blogId);
    Page<Post> findAllByBlogIdAndTemporal(Long blogId, boolean temporal, Pageable pageable);

    Page<Post> findAllBySeriesId(Long SeriesId, Pageable pageable);

}
