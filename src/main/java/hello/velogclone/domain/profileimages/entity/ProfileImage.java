package hello.velogclone.domain.profileimages.entity;

import hello.velogclone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "profile_images")
@NoArgsConstructor
@Getter
@Setter
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String url;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ProfileImage(String type, String url, User user) {
        this.type = type;
        this.url = url;
        this.user = user;
    }
}
