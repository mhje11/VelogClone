package hello.velogclone.domain.profileimages.repository;

import hello.velogclone.domain.profileimages.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    Optional<ProfileImage> findByUrl(String url);
}
