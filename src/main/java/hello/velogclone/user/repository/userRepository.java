package hello.velogclone.user.repository;

import hello.velogclone.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface userRepository extends JpaRepository<User, Long> {
}
