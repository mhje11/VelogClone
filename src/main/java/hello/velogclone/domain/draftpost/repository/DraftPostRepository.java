package hello.velogclone.domain.draftpost.repository;

import hello.velogclone.domain.draftpost.entity.DraftPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftPostRepository extends JpaRepository<DraftPost, Long> {
}
