package hello.velogclone.domain.comment.entity;

import hello.velogclone.domain.post.entity.Post;
import hello.velogclone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter@Setter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "login_id", referencedColumnName = "login_id")
    private User user;

    private String content;
}
