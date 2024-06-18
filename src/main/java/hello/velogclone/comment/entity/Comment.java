package hello.velogclone.comment.entity;

import hello.velogclone.post.entity.Post;
import hello.velogclone.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "login_id")
    private User user;

    private String content;
}
