package hello.velogclone.domain.Series.entity;

import hello.velogclone.domain.blog.entity.Blog;
import hello.velogclone.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "series")
@Getter@Setter
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_name")
    private String seriesName;

    @ManyToOne
    @JoinColumn(name = "blogId")
    private Blog blog;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
