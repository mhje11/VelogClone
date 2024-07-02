package hello.velogclone.domain.postimage.repository;

import hello.velogclone.domain.postimage.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    Optional<PostImage> findByUuid(String uuid);
}
