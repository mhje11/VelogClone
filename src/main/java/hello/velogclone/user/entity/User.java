package hello.velogclone.user.entity;

import hello.velogclone.Follower.entity.Follower;
import hello.velogclone.Following.entity.Following;
import hello.velogclone.blog.entity.Blog;
import hello.velogclone.comment.entity.Comment;
import hello.velogclone.profileimages.entity.ProfileImage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, name = "registration_date", updatable = false)
    @CreatedDate
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "user")
    private List<Blog> blogs;

    @OneToMany(mappedBy = "user")
    private List<Follower> followers;

    @OneToMany(mappedBy = "user")
    private List<Following> followings;

    @OneToMany(mappedBy = "user")
    private List<ProfileImage> profileImages;


}
