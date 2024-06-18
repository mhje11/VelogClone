package hello.velogclone.Following.entity;

import hello.velogclone.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "followings")
public class Following {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "followers_id")
    private User user;
}
