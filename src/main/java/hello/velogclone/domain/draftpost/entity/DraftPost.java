package hello.velogclone.domain.draftpost.entity;

import hello.velogclone.domain.blog.entity.Blog;
import jakarta.persistence.*;

@Entity
@Table(name = "draft_posts")
public class DraftPost {
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
}
