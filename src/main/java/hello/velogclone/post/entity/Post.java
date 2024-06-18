package hello.velogclone.post.entity;

import hello.velogclone.blog.entity.Blog;
import hello.velogclone.comment.entity.Comment;
import hello.velogclone.tag.entity.Tag;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    private Long likes = 0L;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToMany(mappedBy = "post")
    private List<Tag> tags;
}
