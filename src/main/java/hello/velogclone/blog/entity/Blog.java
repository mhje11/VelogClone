package hello.velogclone.blog.entity;

import hello.velogclone.draftpost.entity.DraftPost;
import hello.velogclone.post.entity.Post;
import hello.velogclone.user.entity.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blogs")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "login_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "blog")
    private List<Post> posts;

    @OneToMany(mappedBy = "blog")
    private List<DraftPost> draftPosts;

}
