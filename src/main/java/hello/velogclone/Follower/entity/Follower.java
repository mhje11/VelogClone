package hello.velogclone.Follower.entity;

import hello.velogclone.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "followers")
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "followers_id")
    private User user;
}
