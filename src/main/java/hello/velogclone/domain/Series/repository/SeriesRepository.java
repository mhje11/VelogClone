package hello.velogclone.domain.Series.repository;

import hello.velogclone.domain.Series.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findAllByBlogId(Long blogId);

    Page<Series> findAllByBlogId(Long blogId, Pageable pageable);
}
